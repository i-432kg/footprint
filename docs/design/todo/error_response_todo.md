# エラーレスポンス TODO

作成日: 2026-04-24

対象:

- `src/main/java/jp/i432kg/footprint/presentation/api/GlobalExceptionHandler.java`
- `docs/design/04_api_spec.yaml`
- `docs/design/07_authz_authn.md`
- `docs/adr/adr_024_problem_detail_error_response_policy.md`
- `docs/adr/adr_027_problem_detail_details_structure.md`

## 目的

`ProblemDetail` ベースのエラーレスポンス方針を採用したうえで、今後の改善項目を整理する。

## 対応状況

| ID | 項目 | ステータス | 対応内容 | 備考 |
|---|---|---|---|---|
| ERR-01 | `type` を安定 URI として整備する | Open | 問題種別ごとの URI 命名規約と一覧を決める | `about:blank` からの移行 |
| ERR-02 | `details` の構造を安定化する | Closed | ADR-027 に基づき、`details` を object、正規項目を `target`, `reason`, `rejectedValue` とする構造へ実装と OpenAPI を更新した | バリデーションは `details.errors[]` とする |
| ERR-03 | `application/problem+json` の扱いを明確化する | Open | Content-Type を明示運用するか判断する | 既存クライアント影響を確認 |
| ERR-04 | `07_authz_authn.md` を `ProblemDetail` 方針へ追随させる | Closed | `07_authz_authn.md` のエラー応答説明を `ProblemDetail` / `errorCode` / `details` 前提へ更新済み | ADR-024 準拠 |
| ERR-05 | 機微情報の露出を継続的に抑制する | In Progress | `errorCode` / `details` / `detail` の出力内容を見直す | `SensitiveDataMasker` と連動 |

## TODO 詳細

### 1. `type` を安定 URI として整備する

状況:

- `ProblemDetail` を採用しているが、問題種別の主識別子である `type` の運用は未整備

TODO:

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

状況:

- 実装は `ProblemDetail` を返している
- ただし media type の運用を明示できていない

TODO:

- `application/problem+json` を返す方針にするか判断する
- 既存クライアントへの影響を確認する
- API 仕様書へ反映する

### 4. `07_authz_authn.md` を `ProblemDetail` 方針へ追随させる

対応済み:

- `07_authz_authn.md` のエラー応答説明は `ProblemDetail` 前提へ更新済み
- 標準項目、`errorCode`, `details`, `application/problem+json`, ADR-024 参照まで反映済み

- 周辺設計資料の横断確認は、個別 TODO ではなくレビュー資料更新の中で継続的に行う

### 5. 機微情報の露出を継続的に抑制する

状況:

- `ProblemDetail` は説明力が高い反面、詳細を載せすぎると漏えいリスクがある

TODO:

- `detail` に内部実装や過剰な説明を入れない
- `details` に含める値のマスキング方針を継続確認する
- `SensitiveDataMasker` と整合する運用を維持する

## 運用メモ

- 標準形式は `ProblemDetail`
- 機械判定は `errorCode`
- 補足情報は `details`
- 判断根拠は `docs/adr/adr_024_problem_detail_error_response_policy.md` を参照する
