# `ReplyItemResponse` テスト仕様書

## 1. 基本情報

- 対象クラス: `ReplyItemResponse`
- 対象パッケージ: `jp.i432kg.footprint.presentation.api.response`
- 対応するテストクラス: `ReplyItemResponseTest`
- 作成者: Codex
- 作成日: 2026-04-18

## 2. 対象概要

- 何をするクラスか: 返信一覧で返却する返信レスポンス DTO
- 主な項目: `id`, `postId`, `parentReplyId`, `message`, `childCount`, `createdAt`

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 保持値 | 返信参照情報をそのまま保持すること |
| 2 | 正常系 | nullable 親返信 ID | `parentReplyId=null` を保持できること |
| 3 | 正常系 | 日時保持 | `createdAt` をそのまま保持すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | 返信レスポンスの各項目を保持する | 親返信 ID あり、`createdAt` 指定 | getter で同じ値を取得できる |
| 2 | 正常系 | ルート返信を保持する | `parentReplyId=null` | `parentReplyId=null` を保持する |

## 5. 実装メモ

- ルート返信/子返信の違いは `parentReplyId` のみで確認する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_holdValues_when_allFieldsAreSet` | `ReplyItemResponse は設定された返信情報を保持する` |
| 2 | `should_allowNullParentReplyId_when_replyIsRoot` | `ReplyItemResponse はルート返信として null の parentReplyId を保持できる` |
