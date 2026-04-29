# `AccessLogFilter` UT仕様書

## 1. 対象概要

- 対象クラス: `jp.i432kg.footprint.logging.access.AccessLogFilter`
- 主な責務:
  - HTTP リクエスト単位の access ログ出力
  - `event`, `method`, `path`, `status`, `durationMs` の key-value 化
  - controller から受け取った追加 field の集約
  - 認証済みユーザーの `userId`, `username` 付与
  - 未処理例外時の `500` 補正
- 対応するテストクラス: `AccessLogFilterTest`

## 2. 観点

| No. | 観点 | 内容 |
|---|---|---|
| 1 | access 基本項目 | `event`, `method`, `path`, `status`, `durationMs` を access カテゴリへ出力すること |
| 2 | 認証済みユーザー | 認証済み時は `userId`, `username` を key-value に載せること |
| 3 | controller 追加項目 | `setEvent(...)`, `addField(...)` で渡した追加項目がログへ集約されること |
| 4 | traceId 連携 | `TraceIdFilter` と併用した場合に `traceId` が MDC から引き継がれること |
| 5 | 未処理例外 | 下流例外時でも access ログを欠落させず、`status=500` を記録すること |

## 3. テストケース

| No. | 分類 | テスト内容 | 入力 / 条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | 基本項目を出力する | `GET /api/posts`, `204` | `event=HTTP_ACCESS`, `method=GET`, `path=/api/posts`, `status=204`, `durationMs` が key-value に入る |
| 2 | 正常系 | 認証済みユーザー情報を出力する | `SecurityContext` に `UserDetailsImpl` を設定 | `userId`, `username` が key-value に入る |
| 3 | 正常系 | controller 追加項目を出力する | `setEvent(POST_SEARCH_FETCH)`, `addField(lastIdPresent,size,items)` | `event=POST_SEARCH_FETCH`, `lastIdPresent`, `size`, `items` が key-value に入る |
| 4 | 正常系 | `traceId` を引き継ぐ | `TraceIdFilter` と併用し 404 応答を返す | MDC に `traceId` が入り、access ログにも `status=404` が残る |
| 5 | 異常系 | 未処理例外時も access ログを残す | 下流で `IllegalStateException` を送出 | 例外再送出、access ログに `status=500` が入る |

## 4. 実装メモ

- `ListAppender<ILoggingEvent>` を使い、`getKeyValuePairs()` と `getMDCPropertyMap()` を確認する
- `durationMs` は実行時間に依存するため、存在確認に留める
- `traceId` は ULID 正規表現 `^[0-9A-HJKMNP-TV-Z]{26}$` で確認する
- `MockMvcBuilders.standaloneSetup(...)` を使うケースでは `GlobalExceptionHandler` も併用する

## 5. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_logMethodPathStatusAndDuration_when_requestSucceeds` | `AccessLogFilter は method/path/status/duration を access カテゴリへ出力する` |
| 2 | `should_logUserFields_when_authenticatedUserExists` | `AccessLogFilter は認証済みユーザーがいる場合 userId と username を出力する` |
| 3 | `should_logControllerSuppliedEventAndFields_when_requestAttributesExist` | `AccessLogFilter は controller が設定した event と追加項目を access ログへ含める` |
| 4 | `should_includeTraceIdInMdc_when_usedWithTraceIdFilter` | `AccessLogFilter は TraceIdFilter と併用したとき access ログへ traceId を引き継ぐ` |
| 5 | `should_logInternalServerError_when_downstreamThrowsUnhandledException` | `AccessLogFilter はハンドリングされない例外でも 500 として access ログを残す` |
