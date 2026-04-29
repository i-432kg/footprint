# ログ設計 TODO

作成日: 2026-04-24

対象:

- `docs/design/tmp/06_log_design.md`
- ログ出力実装全般

## 目的

`06_log_design.md` を正とし、現状実装で不足しているログ設計項目を実装タスクとして整理する。

## 対応状況

| ID | 項目 | ステータス | 対応内容 | 備考 |
|---|---|---|---|---|
| LOG-01 | `traceId` の採番と MDC 格納 | Closed | `TraceIdFilter` を追加し、リクエスト単位の ULID を採番して MDC / request attribute へ格納するようにした | Spring Security より前で適用 |
| LOG-02 | `X-Trace-Id` レスポンスヘッダ返却 | Closed | `TraceIdFilter` で `X-Trace-Id` を全レスポンスへ付与するようにした | 正常系 / `GlobalExceptionHandler` 経由のエラー系をテスト済み |
| LOG-03 | access / auth / app / audit のカテゴリ分割 | Closed | `LoggingCategories` で `footprint.access`, `footprint.auth`, `footprint.app`, `footprint.audit` を定義し、auth ハンドラ・`AccessLogFilter`・`GlobalExceptionHandler`・command service 群へ適用した | read 系 access ログの request 受け渡しは `AccessLogContext` に集約 |
| LOG-04 | JSON 構造化ログの導入 | Closed | Spring Boot 標準 structured logging を導入し、`local` / `stg` / `prod` の console を `logstash` JSON で出力するようにした。`environment` は共通設定から profile ごとに差し替え、`local-logfile` では file 出力も確認できるようにした。加えて `access` / `auth` / `app` / `audit` に加え、repository / storage / seed 系の周辺技術ログも fluent logging に寄せて主要項目を独立 JSON field 化した | 運用基盤向けの rename / exclude / add 最終調整やログ検証観点の追補は、本番後の運用改善で扱う |
| LOG-05 | 認証/認可イベントのログ整備 | Closed | success / failure / entry point / access denied を専用ハンドラへ切り出し、`AUTH_LOGIN_SUCCESS`, `AUTH_LOGIN_FAILURE`, `AUTH_UNAUTHORIZED`, `AUTH_FORBIDDEN`, `AUTH_CSRF_REJECTED` を扱える形にした | auth ログ |
| LOG-06 | 投稿/返信/検索系イベントのログ整備 | Closed | 投稿作成 / 返信作成 / ユーザー登録成功は `audit` / `app` へ、read 系成功イベントは controller で event 名と補助項目を設定し `AccessLogFilter` で 1 リクエスト 1 本の `access` ログへ集約した。加えて `@LogOperation` と `FailureEventResolver` により、validation failure / upload rejected / paging invalid を operation ベースで解決する形を実装した | bbox 業務警告や semantic cursor invalid などの read warning は、リリース後の運用改善タスクとして別管理する |
| LOG-07 | バリデーション・例外時のログ粒度見直し | Closed | `GlobalExceptionHandler` の独自例外ログは `errorCode` とマスク済み `details` へ整理し、validation 系例外は `event={}, errors={}` の形式へ統一した。想定外 500 ログにも `errorCode=UNEXPECTED_ERROR` を明示し、validation / warning event の命名ルールを設計書へ固定した | validation ログ出力は helper 化して共通化済み |
| LOG-08 | 機微情報マスキング運用の継続確認 | Closed | `SensitiveDataMasker` / `MaskingTarget` を objectKey / fileName 系まで拡張し、既存ログの棚卸しと seed 例外ルール整理を反映した | password / token / email / objectKey / fileName |

## TODO 詳細

### 1. `traceId` の採番と伝播

対応済み:

- `TraceIdFilter` を追加し、リクエスト開始時に ULID を採番するようにした
- `MDC`, request attribute, response header へ同一値を設定するようにした
- `finally` で `MDC.remove("traceId")` を行い、例外時でも後始末するようにした

### 2. `X-Trace-Id` レスポンスヘッダ返却

対応済み:

- `TraceIdFilter` で `X-Trace-Id` をレスポンスヘッダへ付与するようにした
- 正常レスポンスと `GlobalExceptionHandler` 経由のエラーレスポンスで付与されることをテストで確認した

### 3. ログカテゴリ分割

対応済み:

- `LoggingCategories` で `footprint.access`, `footprint.auth`, `footprint.app`, `footprint.audit` を定義した
- 認証/認可ハンドラは `footprint.auth` を使うようにした
- `AccessLogFilter` を追加し、HTTP リクエスト共通ログを `footprint.access` へ集約した
- `GlobalExceptionHandler` は `footprint.app` を使うようにした
- 投稿作成成功、返信作成成功などの重要操作成功イベントは `footprint.audit` を使うようにした
- read 系 access ログの request 内受け渡しは `AccessLogContext` に集約し、attribute 名や生 `Map` への依存を隠蔽した

### 4. JSON 構造化ログの導入

状況:

- 設計では JSON 構造化ログを前提
- Spring Boot 標準の structured logging を導入し、`local` / `stg` / `prod` の console は `logstash` JSON で出力するようにした
- `application.yml` に共通設定を寄せ、`environment` だけを各 profile の `app.logging.environment` で差し替える形に整理した
- `local-logfile` 補助 profile では `logs/footprint-local-${PID}.json` への file 出力も行えるようにした

TODO:

- `access` / `auth` / `app` / `audit` の主要ログを key-value 形式へ統一し、`event`, `status`, `durationMs`, `errorCode` などを独立 JSON field として出せるようにした
- repository / storage / seed 系の周辺技術ログも fluent logging に寄せ、`event` と主要識別子で検索しやすい形へそろえた
- 運用基盤側で必要な rename / exclude / add 項目があれば最終調整する
- 主要ログの JSON field を前提とした UT / UT 仕様書の追補を必要に応じて進める

対象キー例:

- `timestamp`
- `level`
- `logger`
- `traceId`
- `method`
- `path`
- `status`
- `durationMs`
- `userId`
- `event`
- `errorCode`
- `environment`

### 5. 認証/認可ログの整備

対応済み:

- 認証成功は `ApiAuthenticationSuccessHandler` で `AUTH_LOGIN_SUCCESS` を出すようにした
- 認証失敗は `ApiAuthenticationFailureHandler` で `AUTH_LOGIN_FAILURE` を出すようにした
- 未認証アクセスは `ApiAuthenticationEntryPoint` で `AUTH_UNAUTHORIZED` を出すようにした
- 認可失敗は `ApiAccessDeniedHandler` で `AUTH_FORBIDDEN` を出すようにした
- `CsrfException` は `ApiAccessDeniedHandler` 内で分岐し、`AUTH_CSRF_REJECTED` を出すようにした

### 6. 業務イベントログの整備

進捗:

- `POST_CREATE_SUCCESS`, `REPLY_CREATE_SUCCESS`, `USER_CREATE_SUCCESS` を command service で出すようにした
- `POST_TIMELINE_FETCH`, `POST_SEARCH_FETCH`, `POST_MAP_BBOX_FETCH`, `POST_DETAIL_FETCH`, `REPLY_LIST_FETCH`, `ME_FETCH`, `ME_POSTS_FETCH`, `ME_REPLIES_FETCH` を実装した
- read 系成功イベントは controller が event 名と補助項目を設定し、`AccessLogFilter` が 1 リクエスト 1 本の `footprint.access` ログへ集約する形にした
- `items`, `size`, `lastIdPresent`, `postId`, `parentReplyId`, bbox 値を access ログへ載せるようにした
- request 内の read 系 access ログ文脈は `AccessLogContext` に集約した
- `@LogOperation` と `LoggingOperationInterceptor` で request 文脈へ `operation` を設定し、`FailureEventResolver` が `POST_CREATE_VALIDATION_FAIL`, `POST_CREATE_UPLOAD_REJECTED`, `REPLY_CREATE_VALIDATION_FAIL`, `POST_LAST_ID_INVALID`, `REPLY_LAST_ID_INVALID` を解決する形にした

運用改善として後続対応する項目:

- bbox 業務制約違反や semantic cursor invalid など、read 中の `app` 警告イベント
- seek ページング異常兆候の観測強化
- `LOG-04` の JSON 構造化ログ導入後の出力形式最終化

### 7. 例外ログとバリデーションログの粒度見直し

状況:

- `GlobalExceptionHandler` によるログ出力はある
- ただしカテゴリ、出力項目、構造化キーの標準化は未完了

TODO:

- バリデーションエラーのログレベルと出力項目を統一する
- 想定内例外と想定外例外の扱いを明確にする
- `traceId` と `errorCode` を確実にひもづける

進捗:

- `GlobalExceptionHandler` は resource / domain / application / use case 例外で `errorCode` とマスク済み `details` を出す形へ整理した
- `MethodArgumentNotValidException`, `BindException`, `ConstraintViolationException`, `MethodArgumentTypeMismatchException` はサニタイズ済み `errors` をログ出力する形へ整理した
- `MissingServletRequestParameterException`, `MissingServletRequestPartException`, `HttpMessageNotReadableException` も `event={}, errors={}` 形式で出すように整理した
- failure / warning 系 event 名は `FailureEventResolver` が request 文脈の `operation` と例外種別から解決する形へ整理した
- 500 系 `UseCaseExecutionException` は `details.rejectedValue` を持たない運用へ整理した
- `TraceIdFilter` と `logging.pattern.level` により `traceId` を MDC と通常ログ出力へ載せる基盤を追加した
- 想定外例外の 500 ログは `errorCode=UNEXPECTED_ERROR` をメッセージへ明示する形へ整理した
- validation / warning event の命名ルールは `06_log_design.md` に固定し、`GlobalExceptionHandler` の validation ログ出力は helper 化して共通化した

### 8. マスキング方針の接続

対応済み:

- `SensitiveDataMasker` の UT と UT 仕様書を追加した
- `MaskingTarget` を `password`, `token`, `email`, `objectKey`, `fileName` 系まで拡張した
- `GlobalExceptionHandler` の `details` / validation error はマスキング済み値をログへ出す形へ統一した
- `UseCaseExecutionException` は `rejectedValue` を持たない方針へ変更した
- `PostCommandService` cleanup ログ、repository / storage ログなどを棚卸しし、`objectKey`, `originalFilename`, `email` の不要な直接出力を整理した
- seed ログは fixed seed scenario の固定ダミーデータに限って詳細出力を許容する例外ルールを `06_log_design.md` に明記した

運用メモ:

- local / stg の seed ログは、fixed seed scenario の固定ダミーデータに限って詳細出力を許容する
- この例外は seed 専用処理に限定し、本体の業務ログ・例外ログには持ち込まない

## 運用メモ

- `06_log_design.md` は将来設計ではなく、実装目標として扱う
- TODO は「設計差分の吸収」ではなく「実装不足の解消」として管理する
