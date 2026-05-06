# API仕様書 TODO

作成日: 2026-04-24

対象:

- `docs/design/04_api_spec.yaml`
- `docs/design/generated/openapi.yaml`

## 目的

自動生成した OpenAPI と手修正した API 仕様書の差分、および今後の保守上の注意点を整理する。

## 対応状況

| ID | 項目 | ステータス | 対応内容 | 備考 |
|---|---|---|---|---|
| API-01 | springdoc 自動生成だけでは不足する項目を明示する | Closed | 自動生成 OpenAPI を下書き、`04_api_spec.yaml` を最終仕様として扱う運用を設計資料へ明文化した | `POST /api/login`, `POST /api/logout`, multipart, 認可方針が対象 |
| API-02 | 認可方針と API 仕様の整合を取る | Closed | `SecurityConfig` を更新し、未認証公開をログイン・サインアップ・静的資産・ヘルスチェックに限定した | API 仕様書の「原則認証必須」と整合済み |
| API-03 | ページングの API 仕様と SQL 実装を一致させる | Closed | seek 条件を `ORDER BY created_at DESC, id DESC` と整合する複合条件へ修正し、初回表示用 / seek 継続取得用 SQL に分割した | ADR-023, ADR-025 |
| API-04 | bbox 検索の業務制約を実装と仕様でそろえる | Closed | `BoundingBox` ドメインモデルを導入し、`minLat <= maxLat`, `minLng <= maxLng` を業務ルールとして実装へ反映した | 過大範囲制約は現時点では採用保留 |
| API-05 | レスポンス/リクエストの生成品質改善を検討する | On Hold | OpenAPI annotation の追加はアプリ機能へ直接影響しないため、当面は後回しとする | controller への記述過多に注意 |
| API-06 | 画像アップロード API のレート制限 / タイムアウトを整理する | On Hold | サイズ上限と許可形式は実装準拠へ更新済み。レート制限 / タイムアウトは実装負荷が高いため将来対応とする | アップロード API の非機能要件 |

## TODO 一覧

### 1. springdoc 自動生成だけでは不足する項目を明示する

状況:

- `POST /api/login` と `POST /api/logout` は `SecurityFilterChain` 由来であり、自動生成 OpenAPI には載らない
- `POST /api/posts` の multipart 仕様は、自動生成結果だけでは崩れることがある
- 認証必須/不要の設計意図は、自動生成だけでは十分に表現されない

対応済み:

- 自動生成 OpenAPI は下書きと位置づける
- 最終仕様書は手修正を前提とすることを設計資料へ明記した
- `04_api_spec.yaml` の `info.description` に運用方針を追記した

### 2. 認可方針と API 仕様の整合を取る

状況:

- API 仕様書は「原則認証必須」方針で整理済み
- `SecurityConfig` 実装も API 仕様へ追随し、閲覧系 GET API を含む `/api/**` を認証必須として整理した

対応済み:

- 実装を ADR-021 の方針へ寄せた
- 未認証公開は `POST /api/login`, `POST /api/users`, 静的資産, ヘルスチェックに限定した

### 3. ページングの API 仕様と SQL 実装を一致させる

状況:

- API 仕様書は `lastId` / `size` で整理済み
- `page.nextCursor` は返さない方針で整理済み
- SQL は `ORDER BY created_at DESC, id DESC` に対して複合 seek 条件へ修正済み
- 可読性を優先し、初回表示用と seek 継続取得用で SQL を分割した

対応済み:

- 実装側の seek 条件を ADR-023 に沿って複合条件へ修正した
- 初回表示用 / seek 継続取得用の SQL 分割方針を ADR-025 に整理した

### 4. bbox 検索の業務制約を実装と仕様でそろえる

状況:

- API 仕様書では緯度経度の範囲を記載している
- `BoundingBox` ドメインモデルを導入し、`minLat <= maxLat`, `minLng <= maxLng` を業務ルールとして実装した
- 過大範囲の制約値は、他サービスの設計も踏まえて現時点では採用保留とした

対応済み:

- bbox の最小成立条件を Domain で検証するようにした
- 方針を ADR-026 に整理した

### 5. レスポンス/リクエストの生成品質改善を検討する

状況:

- 自動生成 OpenAPI は DTO の大枠は拾える
- 一方で `multipart/form-data`、フォームログイン、セキュリティ要件、エラー応答の説明は手修正が必要

方針:

- OpenAPI annotation の追加はアプリ機能への影響がないため、一旦保留とする
- 必要になった時点で、必要性が高い箇所だけ `@Operation`, `@Parameter`, `@Schema`, `@SecurityRequirement` を追加する
- controller が説明だらけにならないよう、適用範囲は限定する

### 6. 画像アップロード API のレート制限 / タイムアウトを整理する

状況:

- サイズ上限 10MB と許可形式 `JPEG / PNG / GIF` は実装準拠へ反映済み
- 一方で、アップロード API のレート制限 / タイムアウトは未実装である
- 適用単位、しきい値、実装責務（アプリ / PaaS / リバースプロキシ）も未確定である

方針:

- 対応内容が重いため、現時点では将来対応とする
- 実装着手時に以下を整理する
  - レート制限の単位（ユーザー / IP / セッション）
  - タイムアウトの対象（接続 / 読み込み / アプリ処理全体）
  - 超過時の応答 (`429`, `408` など)
  - 実装責務（Spring / インフラ / CDN）

## 運用メモ

- `docs/design/generated/openapi.yaml` は自動生成成果物として扱う
- `docs/design/04_api_spec.yaml` はレビュー・設計意図を含む仕様書として扱う
- 両者に差分がある場合、まず「実装差分」か「生成限界」かを切り分ける
