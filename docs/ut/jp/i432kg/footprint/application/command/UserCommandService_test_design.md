# `UserCommandService` テスト仕様書

## 1. 基本情報

- 対象クラス: `UserCommandService`
- 対象メソッド: `createUser(CreateUserCommand)`
- 対象パッケージ: `jp.i432kg.footprint.application.command`
- 対応するテストクラス: `UserCommandServiceTest`
- 作成者: Codex
- 作成日: 2026-04-17

## 2. 対象概要

- 何をする処理か: メール重複確認後、パスワードをハッシュ化して `User` を生成し、永続化する
- 入力: `CreateUserCommand`
- 出力: `void`
- 主な副作用: ユーザー保存

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | ユーザー作成成功 | 重複確認、パスワードエンコード、保存が行われること |
| 2 | 異常系 | 重複メール | `ensureEmailNotAlreadyUsed(...)` の例外をそのまま伝播し、保存しないこと |
| 3 | 異常系 | 保存失敗 | `DataAccessException` を `UserCommandFailedException.saveFailed(...)` に変換すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | ユーザーを作成する | 重複確認成功、`PasswordEncoder.encode` 成功、保存成功 | `saveUser` 呼び出し、ハッシュ済みパスワードで保存 |  |
| 2 | 異常系 | メール重複例外を伝播する | `ensureEmailNotAlreadyUsed` が例外送出 | 同例外を送出、`saveUser` 未呼び出し |  |
| 3 | 異常系 | 保存失敗を変換する | `saveUser` が `DataAccessException` | `UserCommandFailedException` |  |

## 5. 実装メモ

- モック化する依存: `UserDomainService`, `UserRepository`, `PasswordEncoder`
- 固定化が必要な値: `CreateUserCommand`, `PasswordEncoder` 戻り値
- `@DisplayName` 方針: `createUser の重複確認、ハッシュ化、保存を記載する`
- 備考:
  - `UserId` は内部生成のため、保存される `User` ではメール・ユーザー名・生年月日・ハッシュ済みパスワードを優先確認する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_createUser_when_dependenciesSucceed` | `UserCommandService.createUser は重複確認とパスワードハッシュ化後にユーザーを保存する` |
| 2 | `should_propagateException_when_emailAlreadyUsed` | `UserCommandService.createUser はメール重複例外をそのまま送出する` |
| 3 | `should_throwUsecaseException_when_saveFails` | `UserCommandService.createUser は保存失敗を UserCommandFailedException に変換する` |
