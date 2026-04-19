# `UserQueryServiceImpl` テスト仕様書

## 1. 基本情報

- 対象クラス: `UserQueryServiceImpl`
- 対象メソッド: `getUserProfile(UserId)`, `findUserProfile(UserId)`
- 対象パッケージ: `jp.i432kg.footprint.infrastructure.datasource.query`
- 対応するテストクラス: `UserQueryServiceImplTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をする処理か: ユーザープロフィール参照を `UserQueryMapper` に委譲し、取得失敗時は `UserNotFoundException` に変換する
- 入力: `UserId`
- 出力: `UserProfileSummary`, `Optional<UserProfileSummary>`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | プロフィール取得成功 | `getUserProfile` が mapper 取得結果を返すこと |
| 2 | 正常系 | Optional 透過 | `findUserProfile` が mapper の `Optional<UserProfileSummary>` をそのまま返すこと |
| 3 | 異常系 | ユーザー不存在 | `getUserProfile` が `Optional.empty()` を `UserNotFoundException` へ変換すること |
| 4 | エッジケース | mapper 呼び出し最小化 | `getUserProfile` / `findUserProfile` が余分な処理を持たないこと |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | ユーザープロフィールを取得する | `findProfileByUserId(userId)` が `Optional.of(summary)` を返す | `getUserProfile(userId)` が `summary` を返す |  |
| 2 | 異常系 | ユーザー不存在を例外へ変換する | `findProfileByUserId(userId)` が `Optional.empty()` を返す | `UserNotFoundException` を送出する | `userId` を保持していることも確認候補 |
| 3 | 正常系 | プロフィール検索結果を `Optional` で返す | `findProfileByUserId(userId)` が `Optional.of(summary)` または `Optional.empty()` | `findUserProfile(userId)` が mapper 戻り値をそのまま返す | 2 パターンに分けてもよい |

## 5. 実装メモ

- モック化する依存: `UserQueryMapper`
- 固定化が必要な値: `UserId`, `UserProfileSummary`
- `@DisplayName` 方針: `UserQueryServiceImpl` の取得成功 / 不存在変換を日本語で明確にする
- 備考:
  - `getUserProfile` は内部で `findUserProfile` を利用するが、公開メソッドの振る舞いとして検証する
  - `verify(userQueryMapper).findProfileByUserId(userId)` と余分な interaction がないことを確認してよい

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_returnUserProfile_when_getUserProfileFindsUser` | `UserQueryServiceImpl.getUserProfile はユーザーが存在する場合にプロフィールを返す` |
| 2 | `should_throwUserNotFoundException_when_getUserProfileFindsNothing` | `UserQueryServiceImpl.getUserProfile はユーザーが存在しない場合に UserNotFoundException を送出する` |
| 3 | `should_returnOptionalResult_when_findUserProfileCalled` | `UserQueryServiceImpl.findUserProfile は mapper の Optional 結果をそのまま返す` |
