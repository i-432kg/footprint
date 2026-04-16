# `ParentReply` テスト仕様書

## 1. 基本情報

- 対象クラス: `ParentReply`
- 対象メソッド: `of(ReplyId)`, `root()`, `getReplyId()`, `hasParent()`
- 対象パッケージ: `jp.i432kg.footprint.domain.model`
- 対応するテストクラス: `ParentReplyTest`
- 作成者: Codex
- 作成日: 2026-04-16

## 2. 対象概要

- 何をする処理か: 親返信あり / なしを `null` ではなく状態として表現する
- 入力: `ReplyId`
- 出力: `ParentReply`, `ReplyId`, `boolean`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 親あり | `of(...)` が `replyId` を保持し `hasParent()==true` となること |
| 2 | 正常系 | 親なし | `root()` が `replyId=null` と `hasParent()==false` を表すこと |
| 3 | 派生 getter | `getReplyId()` | 親あり / なしで期待どおりの `ReplyId` / `null` を返すこと |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 親返信ありを生成する | `ReplyId` | `getReplyId()==replyId`, `hasParent()==true` |  |
| 2 | 正常系 | ルート返信を生成する | なし | `getReplyId()==null`, `hasParent()==false` |  |
| 3 | 派生 getter | 親なし時の getter を確認する | `root()` | `getReplyId()==null` |  |

## 5. 実装メモ

- 固定化が必要な値: 妥当な `ReplyId`
- `@DisplayName` 方針: `ParentReply.of / root の状態差を記載する`

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_createParentReplyWithReplyId_when_parentReplyExists` | `ParentReply.of は親返信ありの状態を生成する` |
| 2 | `should_createRootParentReply_when_rootIsCalled` | `ParentReply.root は親返信なしの状態を生成する` |
| 3 | `should_returnNullReplyId_when_parentReplyIsRoot` | `ParentReply.getReplyId はルート返信の場合に null を返す` |
