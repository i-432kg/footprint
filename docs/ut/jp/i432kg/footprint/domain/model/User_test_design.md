# `User` テスト仕様書

## 1. 基本情報

- 対象クラス: `User`
- 対象メソッド: `of(UserId, UserName, EmailAddress, HashedPassword, BirthDate)`
- 対象パッケージ: `jp.i432kg.footprint.domain.model`
- 対応するテストクラス: `UserTest`
- 作成者: Codex
- 作成日: 2026-04-16

## 2. 対象概要

- 何をする処理か: ユーザーの主要属性を保持するドメインモデルを生成する
- 入力: `UserId`, `UserName`, `EmailAddress`, `HashedPassword`, `BirthDate`
- 出力: `User`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 生成 | 妥当な各値から `User` を生成できること |
| 2 | 正常系 | 保持値 | `userId`, `userName`, `email`, `hashedPassword`, `birthDate` をそのまま返すこと |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | ユーザーを生成する | 妥当な各値オブジェクト | 生成成功 |  |
| 2 | 正常系 | 入力値を保持する | 生成済み `User` | getter が入力値と一致 |  |

## 5. 実装メモ

- 固定化が必要な値: 妥当な各値オブジェクト
- `@DisplayName` 方針: `User.of の生成と保持値確認を記載する`

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_createUser_when_valuesAreValid` | `User.of は妥当な値からユーザーを生成できる` |
| 2 | `should_keepGivenValues_when_userIsCreated` | `User.of は入力した値を保持する` |
