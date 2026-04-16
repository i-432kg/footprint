# `ReplyDomainService` テスト仕様書

## 1. 基本情報

- 対象クラス: `ReplyDomainService`
- 対象メソッド: `findReplyById(ReplyId)`, `validateParentReplyBelongsToPost(PostId, Reply)`
- 対象パッケージ: `jp.i432kg.footprint.domain.service`
- 対応するテストクラス: `ReplyDomainServiceTest`
- 作成者: Codex
- 作成日: 2026-04-16

## 2. 対象概要

- 何をする処理か: 返信の取得補助と、親返信が対象投稿に属するかの整合性を検証する
- 入力: `ReplyId`, `PostId`, `Reply`
- 出力: `Optional<Reply>`, 例外送出の有無
- 主な副作用: `ReplyRepository.findReplyById(...)` 呼び出し

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | `findReplyById` 委譲 | `replyId` が non-null の場合に repository の結果をそのまま返すこと |
| 2 | エッジケース | `findReplyById(null)` | `null` 入力時は `Optional.empty()` を返し repository を呼ばないこと |
| 3 | 正常系 | 投稿一致 | 親返信の `postId` と引数 `postId` が一致すると例外を送出しないこと |
| 4 | 異常系 | 投稿不一致 | 親返信の `postId` と引数 `postId` が不一致なら `ReplyPostMismatchException` を送出すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 該当返信が存在する場合 | `ReplyId`, `findReplyById(...) -> Optional.of(reply)` | 同じ `Optional` を返す |  |
| 2 | 正常系 | 該当返信が存在しない場合 | `ReplyId`, `findReplyById(...) -> Optional.empty()` | `Optional.empty()` を返す |  |
| 3 | エッジケース | `replyId` が `null` の場合 | `null` | `Optional.empty()` を返し repository 未呼び出し |  |
| 4 | 正常系 | 親返信が同じ投稿に属する場合 | 引数 `postId` と `parentReply.getPostId()` が一致 | 例外なし |  |
| 5 | 異常系 | 親返信が別投稿に属する場合 | 引数 `postId` と `parentReply.getPostId()` が不一致 | `ReplyPostMismatchException` |  |

## 5. 実装メモ

- モック化する依存: `ReplyRepository`, `Reply`
- 固定化が必要な値: 妥当な `ReplyId`, `PostId`
- `@DisplayName` 方針: `ReplyDomainService の取得補助と整合性検証を分けて記載する`
- 備考: `validateParentReplyBelongsToPost(...)` は repository 非依存の比較ロジックなので、例外送出の有無を明確に確認する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_returnReplyOptional_when_replyIdIsGiven` | `ReplyDomainService.findReplyById は repository の検索結果をそのまま返す` |
| 2 | `should_returnEmptyAndNotCallRepository_when_replyIdIsNull` | `ReplyDomainService.findReplyById は replyId が null の場合に空を返し repository を呼ばない` |
| 3 | `should_notThrowException_when_parentReplyBelongsToSamePost` | `ReplyDomainService.validateParentReplyBelongsToPost は親返信が同じ投稿に属する場合に例外を送出しない` |
| 4 | `should_throwReplyPostMismatchException_when_parentReplyBelongsToDifferentPost` | `ReplyDomainService.validateParentReplyBelongsToPost は親返信が別投稿に属する場合に不一致例外を送出する` |
