# `jp.i432kg.footprint.presentation.api` パッケージ UT仕様書サマリー

## 1. 対象概要

- 対象パッケージ: `jp.i432kg.footprint.presentation.api`
- 対象クラス: `GlobalExceptionHandler`, `PostRestController`, `ReplyRestController`, `UserRestController`
- 個別仕様書: 各クラスごとに `ファイル名_test_design.md` を作成

## 2. 横断観点

| No. | 観点 | 内容 |
|---|---|---|
| 1 | HTTP ステータス | controller / exception handler が期待どおりの `ResponseEntity` / `ProblemDetail` のステータスを返すこと |
| 2 | DTO 変換 | query model と response mapper の連携結果がレスポンスへ反映されること |
| 3 | コマンド生成 | request DTO から command / 値オブジェクトへ正しく変換して service を呼ぶこと |
| 4 | 認証ユーザー利用 | `@AuthenticationPrincipal` 由来の `UserDetailsImpl` から必要な `UserId` を利用すること |
| 5 | nullable パラメータ | `lastId`, `parentReplyId` など任意入力が適切に `null` / root として扱われること |
| 6 | 例外応答整形 | validation 例外や独自例外を `ProblemDetail` へ変換し、`errorCode` と `details` を含めること |
| 7 | 時刻依存 | `UserRestController` は `Clock` から当日を取得して `BirthDate` を生成すること |

## 3. グルーピング方針

- 例外応答: `GlobalExceptionHandler`
  - 代表的な application/domain/validation/汎用例外の変換を確認する
- 投稿 API: `PostRestController`
  - 一覧・検索・詳細・返信一覧・投稿作成のルーティングと変換を確認する
- 返信 API: `ReplyRestController`
  - ネスト返信取得と返信作成、`parentReplyId` の root / child 分岐を確認する
- ユーザー API: `UserRestController`
  - 現在ユーザー参照、自分の投稿/返信一覧、サインアップと login 呼び出しを確認する

## 4. テスト実装メモ

- controller は Spring MVC フルスタックではなく、クラス単体 UT として依存をモックする
- `ResponseEntity` の body と status、service / mapper 呼び出し引数を確認する
- `GlobalExceptionHandler` は `ProblemDetail` の `status`, `title`, `detail`, `errorCode`, `details` を確認する
- validation 例外は `BindingResult` や `ConstraintViolationException` をテストダブルで組み立てる
