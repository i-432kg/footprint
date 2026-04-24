# エラーレスポンス TODO

作成日: 2026-04-24

対象:

- `src/main/java/jp/i432kg/footprint/presentation/api/GlobalExceptionHandler.java`
- `docs/design/04_api_spec.yaml`
- `docs/design/07_authz_authn.md`
- `docs/adr/adr_024_problem_detail_error_response_policy.md`

## 目的

`ProblemDetail` ベースのエラーレスポンス方針を採用したうえで、今後の改善項目を整理する。

## 対応状況

| ID | 項目 | ステータス | 対応内容 | 備考 |
|---|---|---|---|---|
| ERR-01 | `type` を安定 URI として整備する | Open | 問題種別ごとの URI 命名規約と一覧を決める | `about:blank` からの移行 |
| ERR-02 | `details` の構造を安定化する | Open | バリデーションエラー等の JSON 構造を固定する | `field`, `reason`, `rejectedValue` など |
| ERR-03 | `application/problem+json` の扱いを明確化する | Open | Content-Type を明示運用するか判断する | 既存クライアント影響を確認 |
| ERR-04 | `07_authz_authn.md` を `ProblemDetail` 方針へ追随させる | Open | 独自 `ErrorResponse` 前提の記述を修正する | ADR-024 準拠 |
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

状況:

- `details` は拡張プロパティとして有効だが、構造が揺れるとクライアントが扱いづらい

TODO:

- バリデーションエラーの最小共通項目を決める
- `field`, `reason`, `rejectedValue` などの項目を固定するか判断する
- エラー種別ごとに過度なばらつきが出ないようにする

### 3. `application/problem+json` の扱いを明確化する

状況:

- 実装は `ProblemDetail` を返している
- ただし media type の運用を明示できていない

TODO:

- `application/problem+json` を返す方針にするか判断する
- 既存クライアントへの影響を確認する
- API 仕様書へ反映する

### 4. `07_authz_authn.md` を `ProblemDetail` 方針へ追随させる

状況:

- API 仕様書は更新済み
- ただし周辺設計資料には独自 `ErrorResponse` 前提の記述が残る可能性がある

TODO:

- `07_authz_authn.md` のエラー応答説明を更新する
- 必要なら他の設計書も横断確認する

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
