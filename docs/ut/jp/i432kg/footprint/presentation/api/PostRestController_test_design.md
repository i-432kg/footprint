# `PostRestController` テスト仕様書

## 1. 基本情報

- 対象クラス: `PostRestController`
- 対象パッケージ: `jp.i432kg.footprint.presentation.api`
- 対応するテストクラス: `PostRestControllerTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をするクラスか: 投稿一覧・検索・詳細・返信一覧取得と投稿作成を提供する controller
- 主な依存: `PostCommandService`, `PostQueryService`, `ReplyQueryService`, `PostResponseMapper`, `ReplyResponseMapper`

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 最新一覧取得 | `lastId` の有無に応じて `PostQueryService.listRecentPosts(...)` を呼び、mapper 結果を 200 で返すこと |
| 2 | 正常系 | キーワード検索 | `SearchKeyword`, `PostId` 変換後に `searchPosts(...)` を呼ぶこと |
| 3 | 正常系 | 地図検索 | `Latitude` / `Longitude` へ変換して `searchPostsByBBox(...)` を呼ぶこと |
| 4 | 正常系 | 投稿詳細取得 | `postId` を `PostId` へ変換して `getPost(...)` を呼ぶこと |
| 5 | 正常系 | 投稿配下返信取得 | `postId` を `PostId` へ変換して `listTopLevelReplies(...)` を呼ぶこと |
| 6 | 正常系 | 投稿作成 | `PostRequest` と認証ユーザーから `CreatePostCommand` を生成し `createPost(...)` を呼ぶこと |
| 7 | 正常系 | コメント未指定 | `request.comment=null` の場合に空文字コメントで command を生成すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | 最新一覧を取得する | `lastId` あり、`size=10` | status=200、mapper 結果を body に返す |
| 2 | 正常系 | キーワード検索を行う | `keyword`, `lastId` あり | `SearchKeyword` / `PostId` 変換後に service を呼ぶ |
| 3 | 正常系 | 地図検索を行う | 緯度経度の範囲指定 | `Latitude` / `Longitude` 変換後に service を呼ぶ |
| 4 | 正常系 | 投稿詳細を取得する | `postId` 指定 | status=200、mapper 結果を body に返す |
| 5 | 正常系 | トップレベル返信一覧を取得する | `postId` 指定 | status=200、mapper 結果を body に返す |
| 6 | 正常系 | 投稿を作成する | 非空画像、コメントあり | status=201、`CreatePostCommand` を生成して command service を呼ぶ |
| 7 | 正常系 | コメント未指定で投稿を作成する | `comment=null` | `PostComment.of(\"\")` 相当の command を生成する |

## 5. 実装メモ

- `MultipartFile` は `MockMultipartFile` を使う
- `CreatePostCommand` は `ArgumentCaptor` で受けて中身を確認する
- MVC レベルの validation ではなく controller 単体の変換責務を確認する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_returnRecentPosts_when_getRecentPostsIsCalled` | `PostRestController は最新投稿一覧取得時に service と mapper の結果を 200 で返す` |
| 2 | `should_searchPosts_when_searchIsCalled` | `PostRestController は検索条件を値オブジェクトへ変換して投稿検索を行う` |
| 3 | `should_searchPostsByMapBounds_when_searchMapIsCalled` | `PostRestController は地図範囲を値オブジェクトへ変換して投稿検索を行う` |
| 4 | `should_returnPostDetail_when_getPostIsCalled` | `PostRestController は投稿詳細取得時に mapper 結果を 200 で返す` |
| 5 | `should_returnTopLevelReplies_when_getRepliesIsCalled` | `PostRestController は投稿配下のトップレベル返信一覧を 200 で返す` |
| 6 | `should_createPost_when_createIsCalled` | `PostRestController は投稿作成リクエストから command を生成して 201 を返す` |
| 7 | `should_createPostWithEmptyComment_when_requestCommentIsNull` | `PostRestController は comment 未指定時に空文字コメントで command を生成する` |
