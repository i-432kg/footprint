# `ReplyRestController` テスト仕様書

## 1. 基本情報

- 対象クラス: `ReplyRestController`
- 対象パッケージ: `jp.i432kg.footprint.presentation.api`
- 対応するテストクラス: `ReplyRestControllerTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をするクラスか: ネスト返信一覧取得と返信作成を提供する controller
- 主な依存: `ReplyCommandService`, `ReplyQueryService`, `ReplyResponseMapper`

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | ネスト返信取得 | `parentReplyId` を `ReplyId` へ変換して `listNestedReplies(...)` を呼び、mapper 結果を返すこと |
| 2 | 正常系 | ルート返信作成 | `parentReplyId=null` の場合に `ParentReply.root()` で command を生成すること |
| 3 | 正常系 | 子返信作成 | `parentReplyId` ありの場合に `ParentReply.of(...)` で command を生成すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | ネスト返信一覧を取得する | `parentReplyId` 指定 | status=200、mapper 結果を body に返す |
| 2 | 正常系 | ルート返信を作成する | `request.parentReplyId=null` | status=201、`ParentReply.root()` の command を生成する |
| 3 | 正常系 | 子返信を作成する | `request.parentReplyId=ULID` | status=201、親返信ありの command を生成する |

## 5. 実装メモ

- `CreateReplyCommand` は `ArgumentCaptor` で検証する
- `userDetails.getUserId()` が command に引き継がれることも確認する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_returnNestedReplies_when_getNextRepliesIsCalled` | `ReplyRestController は親返信配下のネスト返信一覧を 200 で返す` |
| 2 | `should_createRootReply_when_parentReplyIdIsNull` | `ReplyRestController は parentReplyId 未指定時にルート返信として 201 を返す` |
| 3 | `should_createChildReply_when_parentReplyIdIsPresent` | `ReplyRestController は parentReplyId 指定時に子返信として 201 を返す` |
