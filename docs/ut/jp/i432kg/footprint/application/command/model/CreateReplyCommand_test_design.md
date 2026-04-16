# `CreateReplyCommand` テスト仕様書

## 1. 基本情報

- 対象クラス: `CreateReplyCommand`
- 対象メソッド: `of(PostId, UserId, ParentReply, ReplyComment)`
- 対象パッケージ: `jp.i432kg.footprint.application.command.model`
- 対応するテストクラス: `CreateReplyCommandTest`
- 作成者: Codex
- 作成日: 2026-04-17

## 2. 対象概要

- 何をする処理か: 返信作成に必要な値を保持する command を生成する
- 入力: `PostId`, `UserId`, `ParentReply`, `ReplyComment`
- 出力: `CreateReplyCommand`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 保持値 | `of(...)` に渡した値をそのまま保持すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | command を生成する | `postId`, `userId`, `parentReply`, `message` | 各 getter で同値を取得できる |  |

## 5. 実装メモ

- モック化する依存: なし
- `@DisplayName` 方針: `CreateReplyCommand.of の保持値を記載する`

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_createCommand_when_valuesAreProvided` | `CreateReplyCommand.of は渡された値を保持する` |
