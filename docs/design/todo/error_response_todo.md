# エラーレスポンス TODO

作成日: 2026-04-24

対象:

- `src/main/java/jp/i432kg/footprint/presentation/api/GlobalExceptionHandler.java`
- `src/main/java/jp/i432kg/footprint/infrastructure/security/ApiAuthenticationFailureHandler.java`
- `src/main/java/jp/i432kg/footprint/infrastructure/security/ApiAuthenticationEntryPoint.java`
- `src/main/java/jp/i432kg/footprint/infrastructure/security/ApiAccessDeniedHandler.java`
- `docs/design/04_api_spec.yaml`
- `docs/design/07_authz_authn.md`
- `docs/adr/adr_024_problem_detail_error_response_policy.md`
- `docs/adr/adr_027_problem_detail_details_structure.md`
- `docs/adr/adr_032_api_security_problem_detail_response.md`

## 目的

`ProblemDetail` ベースのエラーレスポンス方針を採用したうえで、今後の改善項目を整理する。

## 対応状況

| ID | 項目 | ステータス | 対応内容 | 備考 |
|---|---|---|---|---|
| ERR-01 | `type` を安定 URI として整備する | On Hold | `errorCode` による機械判定で当面運用し、`type` は外部契約の標準化としてリリース後に整備する | `about:blank` からの移行 |
| ERR-02 | `details` の構造を安定化する | Closed | ADR-027 に基づき、`details` を object、正規項目を `target`, `reason`, `rejectedValue` とする構造へ実装と OpenAPI を更新した | バリデーションは `details.errors[]` とする |
| ERR-03 | `application/problem+json` の扱いを明確化する | Closed | `GlobalExceptionHandler` を `application/problem+json` 明示運用にし、OpenAPI のエラーレスポンス content も統一した | フロントは `Content-Type` 固定前提ではないことを確認済み |
| ERR-04 | `07_authz_authn.md` を `ProblemDetail` 方針へ追随させる | Closed | `07_authz_authn.md` のエラー応答説明を `ProblemDetail` / `errorCode` / `details` 前提へ更新済み | ADR-024 準拠 |
| ERR-05 | 機微情報の露出を継続的に抑制する | Closed | `errorCode` / `details` / `detail` の出力内容を見直し、`SensitiveDataMasker` と 500 系 `UseCaseExecutionException` の運用を反映した | seed 専用ログは固定ダミーデータに限って別ルールで許容 |
| ERR-06 | `/api` の認証系エラーを `ProblemDetail` へ統一する | On Hold | ADR-032 で、Spring Security の認証失敗 / 未認証 / 認可失敗 / CSRF 拒否を共通 writer で `ProblemDetail` 化する方針を決定した。影響範囲が広いため、現リリースには含めずリリース後タスクとする | infrastructure review No.3 |

## TODO 詳細

### 1. `type` を安定 URI として整備する

保留方針:

- `ProblemDetail` を採用しているが、現時点では `errorCode` で機械判定できているため、`type` 未整備はリリース阻害要因としない
- `type` はクライアント向け外部契約の標準化として位置づけ、リリース後に順次整備する

再開時 TODO:

- 問題種別 URI の命名規約を決める
- 主要エラーコードと `type` の対応表を作る
- 必要なら公開ドキュメントの配置場所も決める

### 2. `details` の構造を安定化する

対応済み:

- `GlobalExceptionHandler.validationError(...)` を `target` / `reason` ベースへ更新した
- resource not found 系例外を `target` / `reason` / `resourceId` を持つ構造へ更新した
- `04_api_spec.yaml` の `ProblemDetailError.details` を object スキーマへ修正した
- バリデーションエラーは `details.errors[]`、独自例外は `details` 直下 object のまま扱うように統一した

方針:

- ADR-027 に基づき、`details` は常に object とする
- 正規項目は `target`, `reason`, `rejectedValue` とする
- バリデーションエラーは `details.errors[]` 配下へ正規項目を並べる
- 人間向け文言は `detail` に寄せ、`details.message` は正規項目にしない

- `source` は必要なケース（query / body / multipart）に限定して追加する
- 任意項目 (`min`, `max`, `expectedFormat` など) の使用ルールは ADR-027 を参照する

### 3. `application/problem+json` の扱いを明確化する

対応済み:

- `GlobalExceptionHandler` に `produces = application/problem+json` を付与し、例外レスポンスを明示運用にした
- OpenAPI の `ProblemDetailError` 応答は `application/problem+json` に統一した
- フロントエンドは `Content-Type` 固定前提の実装ではなく、`error.response.data` を参照する構成であることを確認した

### 4. `07_authz_authn.md` を `ProblemDetail` 方針へ追随させる

対応済み:

- `07_authz_authn.md` のエラー応答説明は `ProblemDetail` 前提へ更新済み
- 標準項目、`errorCode`, `details`, `application/problem+json`, ADR-024 参照まで反映済み

- 周辺設計資料の横断確認は、個別 TODO ではなくレビュー資料更新の中で継続的に行う

### 5. 機微情報の露出を継続的に抑制する

対応済み:

- `GlobalExceptionHandler` の `details` / validation error は `SensitiveDataMasker` を通した値を利用する形へ統一した
- `MaskingTarget` に email / password / token / objectKey / fileName 系の判定を集約した
- 500 系 `UseCaseExecutionException` は `details.rejectedValue` を持たず、`target` / `reason` のみ返す運用へ変更した
- `detail` は内部実装や過剰な識別子を含まないメッセージに抑制した

運用メモ:

- seed 専用ログは local / stg の fixed seed scenario に限り、固定ダミーデータの詳細出力を許容する
- 本体の API エラーレスポンスと通常ログでは、引き続き `SensitiveDataMasker` と ADR-008 の方針に従う

### 6. `/api` の認証系エラーを `ProblemDetail` へ統一する

保留方針:

- `GlobalExceptionHandler` 配下の例外応答は `ProblemDetail` 化済みだが、Spring Security filter chain 内で完結する 401 / 403 は未対応のため、別タスクとして扱う
- `ApiAuthenticationFailureHandler`、`ApiAuthenticationEntryPoint`、`ApiAccessDeniedHandler` の 3 経路を同時に置き換える必要があり、現リリースでは影響範囲が大きい
- 実装方針は ADR-032 に固定し、リリース後にまとめて着手する

再開時 TODO:

- auth 系 `ProblemDetail` を書き込む共通 writer を `infrastructure.security` 配下へ追加する
- auth 系 `errorCode` 定数と `details.reason` の外部契約を定義する
- `/api/login` 認証失敗、未認証アクセス、403 / CSRF 拒否の応答を `application/problem+json` へ統一する
- OpenAPI と認証設計書の 401 / 403 記述を実装に合わせて補強する
- 既存の security handler テストを status-only から本文検証付きへ拡張する

## 運用メモ

- 標準形式は `ProblemDetail`
- 機械判定は `errorCode`
- 補足情報は `details`
- 判断根拠は `docs/adr/adr_024_problem_detail_error_response_policy.md` を参照する
