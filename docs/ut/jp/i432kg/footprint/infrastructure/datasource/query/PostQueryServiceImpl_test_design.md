# `PostQueryServiceImpl` テスト仕様書

## 1. 基本情報

- 対象クラス: `PostQueryServiceImpl`
- 対象メソッド: `listRecentPosts(PostId, int)`, `listMyPosts(UserId, PostId, int)`, `searchPosts(SearchKeyword, PostId, int)`, `searchPostsByBBox(BoundingBox)`, `getPost(PostId)`, `findPost(PostId)`
- 対象パッケージ: `jp.i432kg.footprint.infrastructure.datasource.query`
- 対応するテストクラス: `PostQueryServiceImplTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をする処理か: 投稿参照系ユースケースを `PostQueryMapper` に委譲し、ページング系は `lastId` の有無で初回表示用 mapper と seek 継続取得用 mapper を呼び分ける。詳細取得時のみ `Optional.empty()` を `PostNotFoundException` に変換する
- 入力: `PostId`, `UserId`, `SearchKeyword`, `BoundingBox`, `size`
- 出力: `List<PostSummary>`, `PostSummary`, `Optional<PostSummary>`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 一覧取得委譲 | `listRecentPosts`, `listMyPosts`, `searchPosts`, `searchPostsByBBox` が `lastId` の有無を含めて対応する mapper メソッドへ正しい引数で委譲されること |
| 2 | 正常系 | 詳細取得成功 | `getPost` が mapper 取得結果を返し、例外へ変換しないこと |
| 3 | 正常系 | Optional 透過 | `findPost` が mapper の `Optional<PostSummary>` をそのまま返すこと |
| 4 | 異常系 | 投稿不存在 | `getPost` が `Optional.empty()` を `PostNotFoundException` へ変換すること |
| 5 | 境界値 | nullable lastId | `lastId=null` のとき初回表示用 mapper を呼び、`lastId` ありのとき seek 継続取得用 mapper を呼ぶこと |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 最新投稿一覧を seek 継続取得する | `lastId` あり、mapper が `List<PostSummary>` を返す | `findRecentPostsAfterCursor(lastId, size)` が呼ばれ、同等の一覧を返す |  |
| 2 | 境界値 | 最新投稿一覧取得で `lastId=null` のとき初回表示用 mapper を呼ぶ | `lastId=null`、mapper が `List<PostSummary>` を返す | `findRecentPostsFirstPage(size)` が呼ばれる | ページング先頭 |
| 3 | 正常系 | 自分の投稿一覧を seek 継続取得する | `userId`, `lastId`, `size`、mapper が一覧を返す | `findMyPostsAfterCursor(userId, lastId, size)` が呼ばれ、同等の一覧を返す |  |
| 4 | 境界値 | 自分の投稿一覧取得で `lastId=null` のとき初回表示用 mapper を呼ぶ | `userId`, `lastId=null`, `size`、mapper が一覧を返す | `findMyPostsFirstPage(userId, size)` が呼ばれる | ページング先頭 |
| 5 | 正常系 | キーワード検索を seek 継続取得する | `keyword`, `lastId`, `size`、mapper が一覧を返す | `findPostsByKeywordAfterCursor(keyword, lastId, size)` が呼ばれ、同等の一覧を返す |  |
| 6 | 境界値 | キーワード検索で `lastId=null` のとき初回表示用 mapper を呼ぶ | `keyword`, `lastId=null`, `size`、mapper が一覧を返す | `findPostsByKeywordFirstPage(keyword, size)` が呼ばれる | ページング先頭 |
| 7 | 正常系 | BBox 検索を行う | `BoundingBox`、mapper が一覧を返す | `findPostsByBBox(boundingBox)` が呼ばれ、同等の一覧を返す |  |
| 8 | 正常系 | 投稿詳細を取得する | `findPostById(postId)` が `Optional.of(summary)` を返す | `getPost(postId)` が `summary` を返す |  |
| 9 | 異常系 | 投稿不存在を例外へ変換する | `findPostById(postId)` が `Optional.empty()` を返す | `PostNotFoundException` を送出する | `postId` を保持していることも確認候補 |
| 10 | 正常系 | 投稿詳細検索で `Optional` を返す | `findPostById(postId)` が `Optional.of(summary)` または `Optional.empty()` | `findPost(postId)` が mapper 戻り値をそのまま返す | 2 パターンに分けてもよい |

## 5. 実装メモ

- モック化する依存: `PostQueryMapper`
- 固定化が必要な値: `PostId`, `UserId`, `SearchKeyword`, `Latitude`, `Longitude`, `PostSummary`
- `@DisplayName` 方針: `PostQueryServiceImpl` のメソッド名と期待振る舞いを日本語で明記する
- 備考:
  - `getPost` の例外変換では mapper 呼び出しが 1 回であることを確認する
  - `List` の検証は `containsExactly(...)` などで要素一致を確認する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_returnRecentPosts_when_listRecentPostsCalled` | `PostQueryServiceImpl.listRecentPosts は mapper から取得した最新投稿一覧を返す` |
| 2 | `should_delegateNullLastId_when_listRecentPostsCalledWithoutPagingCursor` | `PostQueryServiceImpl.listRecentPosts は lastId が null の場合に初回表示用 mapper を呼ぶ` |
| 3 | `should_returnMyPosts_when_listMyPostsCalled` | `PostQueryServiceImpl.listMyPosts は mapper から取得した自分の投稿一覧を返す` |
| 4 | `should_returnSearchedPosts_when_searchPostsCalled` | `PostQueryServiceImpl.searchPosts はキーワード検索結果を返す` |
| 5 | `should_callFirstPageMapper_when_listMyPostsCalledWithoutPagingCursor` | `PostQueryServiceImpl.listMyPosts は lastId が null の場合に初回表示用 mapper を呼ぶ` |
| 6 | `should_callFirstPageMapper_when_searchPostsCalledWithoutPagingCursor` | `PostQueryServiceImpl.searchPosts は lastId が null の場合に初回表示用 mapper を呼ぶ` |
| 7 | `should_returnPostsInBoundingBox_when_searchPostsByBBoxCalled` | `PostQueryServiceImpl.searchPostsByBBox は境界ボックス検索結果を返す` |
| 8 | `should_returnPostSummary_when_getPostFindsPost` | `PostQueryServiceImpl.getPost は投稿が存在する場合に投稿詳細を返す` |
| 9 | `should_throwPostNotFoundException_when_getPostFindsNothing` | `PostQueryServiceImpl.getPost は投稿が存在しない場合に PostNotFoundException を送出する` |
| 10 | `should_returnOptionalResult_when_findPostCalled` | `PostQueryServiceImpl.findPost は mapper の Optional 結果をそのまま返す` |
