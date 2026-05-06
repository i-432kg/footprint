# `CreateUserCommand` テスト仕様書

## 1. 基本情報

- 対象クラス: `CreateUserCommand`
- 対象メソッド: `of(UserName, EmailAddress, RawPassword, BirthDate)`
- 対象パッケージ: `jp.i432kg.footprint.application.command.model`
- 対応するテストクラス: `CreateUserCommandTest`
- 作成者: Codex
- 作成日: 2026-04-17

## 2. 対象概要

- 何をする処理か: ユーザー作成に必要な値を保持する command を生成する
- 入力: `UserName`, `EmailAddress`, `RawPassword`, `BirthDate`
- 出力: `CreateUserCommand`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 保持値 | `of(...)` に渡した値をそのまま保持すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | command を生成する | `userName`, `email`, `rawPassword`, `birthDate` | 各 getter で同値を取得できる |  |

## 5. 実装メモ

- モック化する依存: なし
- `@DisplayName` 方針: `CreateUserCommand.of の保持値を記載する`

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_createCommand_when_valuesAreProvided` | `CreateUserCommand.of は渡された値を保持する` |
