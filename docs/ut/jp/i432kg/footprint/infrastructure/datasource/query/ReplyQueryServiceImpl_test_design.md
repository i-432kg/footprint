# `ReplyQueryServiceImpl` テスト仕様書

## 1. 基本情報

- 対象クラス: `ReplyQueryServiceImpl`
- 対象メソッド: `listTopLevelReplies(PostId)`, `listNestedReplies(ReplyId)`, `listMyReplies(UserId, ReplyId, int)`
- 対象パッケージ: `jp.i432kg.footprint.infrastructure.datasource.query`
- 対応するテストクラス: `ReplyQueryServiceImplTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をする処理か: 返信参照系ユースケースを `ReplyQueryMapper` に委譲し、`listMyReplies` は `lastId` の有無で初回表示用 mapper と seek 継続取得用 mapper を呼び分ける。取得結果はそのまま返す
- 入力: `PostId`, `ReplyId`, `UserId`, `size`
- 出力: `List<ReplySummary>`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | トップレベル返信取得 | `listTopLevelReplies` が `findTopLevelRepliesByPostId` に委譲されること |
| 2 | 正常系 | 子返信取得 | `listNestedReplies` が `findNestedRepliesByParentId` に委譲されること |
| 3 | 正常系 | 自分の返信取得 | `listMyReplies` が `lastId` の有無に応じて対応する mapper に委譲されること |
| 4 | 境界値 | nullable lastId | `listMyReplies` の `lastId=null` のとき初回表示用 mapper を呼ぶこと |
| 5 | エッジケース | 空一覧 | mapper が空一覧を返した場合でもそのまま返すこと |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | トップレベル返信一覧を取得する | `postId`、mapper が一覧を返す | `findTopLevelRepliesByPostId(postId)` が呼ばれ、同等の一覧を返す |  |
| 2 | 正常系 | 子返信一覧を取得する | `parentReplyId`、mapper が一覧を返す | `findNestedRepliesByParentId(parentReplyId)` が呼ばれ、同等の一覧を返す |  |
| 3 | 正常系 | 自分の返信一覧を seek 継続取得する | `userId`, `lastId`, `size`、mapper が一覧を返す | `findMyRepliesAfterCursor(userId, lastId, size)` が呼ばれ、同等の一覧を返す |  |
| 4 | 境界値 | 自分の返信一覧取得で `lastId=null` のとき初回表示用 mapper を呼ぶ | `lastId=null`、mapper が空一覧を返す | `findMyRepliesFirstPage(userId, size)` が呼ばれ、空一覧を返す | ページング先頭 |

## 5. 実装メモ

- モック化する依存: `ReplyQueryMapper`
- 固定化が必要な値: `PostId`, `ReplyId`, `UserId`, `ReplySummary`
- `@DisplayName` 方針: 委譲先 mapper 名ではなく service メソッドの利用意図を日本語で示す
- 備考:
  - 参照系で副作用がないことを `verifyNoMoreInteractions(replyQueryMapper)` で補強してよい
  - `listMyReplies` は `lastId` あり / なしで mapper 呼び分けを行う

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_returnTopLevelReplies_when_listTopLevelRepliesCalled` | `ReplyQueryServiceImpl.listTopLevelReplies はトップレベル返信一覧を返す` |
| 2 | `should_returnNestedReplies_when_listNestedRepliesCalled` | `ReplyQueryServiceImpl.listNestedReplies は子返信一覧を返す` |
| 3 | `should_returnMyReplies_when_listMyRepliesCalled` | `ReplyQueryServiceImpl.listMyReplies は自分の返信一覧を返す` |
| 4 | `should_delegateNullLastId_when_listMyRepliesCalledWithoutPagingCursor` | `ReplyQueryServiceImpl.listMyReplies は lastId が null の場合に初回表示用 mapper を呼ぶ` |
