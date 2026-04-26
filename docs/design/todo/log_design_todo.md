# ログ設計 TODO

作成日: 2026-04-24

対象:

- `docs/design/06_log_design.md`
- ログ出力実装全般

## 目的

`06_log_design.md` を正とし、現状実装で不足しているログ設計項目を実装タスクとして整理する。

## 対応状況

| ID | 項目 | ステータス | 対応内容 | 備考 |
|---|---|---|---|---|
| LOG-01 | `traceId` の採番と MDC 格納 | Open | リクエスト単位の相関 ID を生成し、全ログへ付与する | Filter / Interceptor で実装候補 |
| LOG-02 | `X-Trace-Id` レスポンスヘッダ返却 | Open | クライアントが障害時に参照できるようレスポンスへ付与する | `traceId` 実装と同時対応 |
| LOG-03 | access / auth / app / audit のカテゴリ分割 | Open | 用途別ロガー名または appender を整理する | `06_log_design.md` 準拠 |
| LOG-04 | JSON 構造化ログの導入 | Open | 本番運用向けに JSON ログ出力を整備する | local では可読性優先でも可 |
| LOG-05 | 認証/認可イベントのログ整備 | Open | ログイン成功/失敗、401/403、CSRF などを明示的に出力する | auth ログ |
| LOG-06 | 投稿/返信/検索系イベントのログ整備 | Open | 投稿作成、返信作成、bbox 検索、一覧取得などを整理する | app / audit ログ |
| LOG-07 | バリデーション・例外時のログ粒度見直し | In Progress | `GlobalExceptionHandler` はあるが、カテゴリや項目が未整理 | `errorCode`, `details`, `traceId` の整合確認 |
| LOG-08 | 機微情報マスキング運用の継続確認 | In Progress | `SensitiveDataMasker` は存在するが、ログ設計全体との接続が未完了 | password / token / email / IP |

## TODO 詳細

### 1. `traceId` の採番と伝播

状況:

- 設計書では `traceId` を共通キーとする前提
- 現実装ではリクエスト相関 ID の採番実装を確認できていない

TODO:

- リクエスト開始時に `traceId` を採番する
- MDC に格納して下流ログへ渡す
- 非同期処理や例外時でも欠落しないようにする

### 2. `X-Trace-Id` レスポンスヘッダ返却

状況:

- 設計書ではクライアントへ `X-Trace-Id` を返す方針
- 現実装では確認できていない

TODO:

- レスポンスヘッダへ `X-Trace-Id` を付与する
- フロントや障害報告で利用できる形にする

### 3. ログカテゴリ分割

状況:

- 設計では `access` / `auth` / `app` / `audit` を分ける前提
- 現実装では例外ログ中心で、カテゴリ分割は未整備

TODO:

- ロガー名または appender でカテゴリを分ける
- 各イベントがどのカテゴリに属するかを整理する

### 4. JSON 構造化ログの導入

状況:

- 設計では JSON 構造化ログを前提
- 現実装では設定ファイルや出力形式が未整備

TODO:

- 本番・stg で JSON ログを出せる設定を追加する
- 共通キーを最低限そろえる

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

### 5. 認証/認可ログの整備

状況:

- 設計では `AUTH_LOGIN_SUCCESS`, `AUTH_LOGIN_FAILURE`, `AUTH_UNAUTHORIZED`, `AUTH_FORBIDDEN`, `AUTH_CSRF_REJECTED` などを想定
- 現実装ではこの粒度の明示ログは未整備

TODO:

- 認証成功/失敗時のイベントログを追加する
- 401/403/CSRF 失敗時のログ方針を実装へ反映する

### 6. 業務イベントログの整備

状況:

- 設計では投稿、返信、bbox 検索、一覧取得などのイベントログを想定
- 現実装では業務イベントログが十分ではない

TODO:

- 投稿作成
- 返信作成
- タイムライン取得
- マイページ取得
- bbox 検索

などのログ出力ポイントを整理する

### 7. 例外ログとバリデーションログの粒度見直し

状況:

- `GlobalExceptionHandler` によるログ出力はある
- ただしカテゴリ、出力項目、構造化キーの標準化は未完了

TODO:

- バリデーションエラーのログレベルと出力項目を統一する
- 想定内例外と想定外例外の扱いを明確にする
- `traceId` と `errorCode` を確実にひもづける

### 8. マスキング方針の接続

状況:

- `SensitiveDataMasker` は存在する
- ただし access/auth/app/audit すべてへ一貫適用できているとは限らない

TODO:

- 機微情報がログへ直接出ないことを確認する
- 新しいログイベント追加時のマスキングルールを整理する

運用メモ:

- local / stg の seed ログは、fixed seed scenario の固定ダミーデータに限って詳細出力を許容する
- この例外は seed 専用処理に限定し、本体の業務ログ・例外ログには持ち込まない

## 運用メモ

- `06_log_design.md` は将来設計ではなく、実装目標として扱う
- TODO は「設計差分の吸収」ではなく「実装不足の解消」として管理する
