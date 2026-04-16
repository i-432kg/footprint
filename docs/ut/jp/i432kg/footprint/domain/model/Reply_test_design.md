# `Reply` テスト仕様書

## 1. 基本情報

- 対象クラス: `Reply`
- 対象メソッド: `of(...)`, `getParentReplyId()`, `hasParentReply()`
- 対象パッケージ: `jp.i432kg.footprint.domain.model`
- 対応するテストクラス: `ReplyTest`
- 作成者: Codex
- 作成日: 2026-04-16

## 2. 対象概要

- 何をする処理か: 返信の主要属性を保持し、親返信の有無を補助メソッドで公開する
- 入力: `ReplyId`, `PostId`, `UserId`, `ParentReply`, `ReplyComment`, `LocalDateTime`
- 出力: `Reply`, `ReplyId`, `boolean`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 生成 | 入力値を保持した `Reply` を生成できること |
| 2 | 正常系 | 親あり | 親返信ありの場合に `getParentReplyId()` が親 ID を返し `hasParentReply()==true` となること |
| 3 | 正常系 | 親なし | ルート返信の場合に `getParentReplyId()==null`, `hasParentReply()==false` となること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 親返信ありの返信を生成する | `ParentReply.of(replyId)` | 生成成功 |  |
| 2 | 正常系 | 親返信ありの状態を返す | 親ありの `Reply` | `getParentReplyId()==親ID`, `hasParentReply()==true` |  |
| 3 | 正常系 | ルート返信の状態を返す | `ParentReply.root()` | `getParentReplyId()==null`, `hasParentReply()==false` |  |

## 5. 実装メモ

- 固定化が必要な値: `createdAt`, 妥当な `ParentReply`
- `@DisplayName` 方針: `Reply.of の生成と親返信状態判定を分けて記載する`

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_createReply_when_valuesAreValid` | `Reply.of は妥当な値から返信を生成できる` |
| 2 | `should_returnParentReplyState_when_replyHasParent` | `Reply は親返信ありの場合に親返信 ID と true を返す` |
| 3 | `should_returnRootReplyState_when_replyHasNoParent` | `Reply はルート返信の場合に親返信 ID と false を返す` |
