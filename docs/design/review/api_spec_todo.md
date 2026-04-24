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
| API-01 | springdoc 自動生成だけでは不足する項目を明示する | Open | 自動生成 OpenAPI を下書き、`04_api_spec.yaml` を最終仕様として扱う運用を明文化する | `POST /api/login`, `POST /api/logout`, multipart, 認可方針が対象 |
| API-02 | 認可方針と API 仕様の整合を取る | In Progress | API 仕様書は原則認証必須で整理済み。`SecurityConfig` 実装を追随させる | 閲覧系 GET API がまだ `permitAll()` |
| API-03 | ページングの API 仕様と SQL 実装を一致させる | In Progress | 仕様書は `lastId` / `size` へ更新済み。実装の seek 条件見直しが残る | ADR-023 |
| API-04 | bbox 検索の業務制約を実装と仕様でそろえる | Open | `min <= max` や過大範囲の扱いを明確化する | 実装不足の可能性あり |
| API-05 | レスポンス/リクエストの生成品質改善を検討する | Open | 必要箇所だけ OpenAPI annotation 追加を検討する | controller への記述過多に注意 |

## TODO 一覧

### 1. springdoc 自動生成だけでは不足する項目を明示する

状況:

- `POST /api/login` と `POST /api/logout` は `SecurityFilterChain` 由来であり、自動生成 OpenAPI には載らない
- `POST /api/posts` の multipart 仕様は、自動生成結果だけでは崩れることがある
- 認証必須/不要の設計意図は、自動生成だけでは十分に表現されない

TODO:

- 自動生成 OpenAPI は下書きと位置づける
- 最終仕様書は手修正を前提とすることを README か設計資料に明記する
- 必要なら OpenAPI annotation の導入範囲を検討する

### 2. 認可方針と API 仕様の整合を取る

状況:

- API 仕様書は「原則認証必須」方針で整理済み
- ただし `SecurityConfig` 実装では、閲覧系 GET API がまだ `permitAll()` のまま残っている

対象:

- `GET /api/posts`
- `GET /api/posts/search`
- `GET /api/posts/search/map`
- `GET /api/posts/{postId}`
- `GET /api/posts/{postId}/replies`
- `GET /api/replies/{parentReplyId}`

TODO:

- 実装を ADR-021 の方針へ寄せる
- `07_authz_authn.md` も同じ方針で更新する

### 3. ページングの API 仕様と SQL 実装を一致させる

状況:

- API 仕様書は `lastId` / `size` で整理済み
- `page.nextCursor` は返さない方針で整理済み
- ただし SQL は `ORDER BY created_at DESC, id DESC` に対して境界条件が `created_at` 中心であり、同一 timestamp 境界で取りこぼし余地がある

TODO:

- 実装側の seek 条件を ADR-023 に沿って複合条件へ修正する
- 修正後、自動生成 OpenAPI と手修正仕様書の説明を再確認する

### 4. bbox 検索の業務制約を実装と仕様でそろえる

状況:

- API 仕様書では緯度経度の範囲を記載している
- ただし `min <= max` や過大範囲の制御といった業務制約は、現実装では十分に表現されていない可能性がある

TODO:

- `minLat <= maxLat`, `minLng <= maxLng` を実装で検証するか判断する
- 必要なら API 仕様書と `06_log_design.md` にエラー扱いを追記する

### 5. レスポンス/リクエストの生成品質改善を検討する

状況:

- 自動生成 OpenAPI は DTO の大枠は拾える
- 一方で `multipart/form-data`、フォームログイン、セキュリティ要件、エラー応答の説明は手修正が必要

TODO:

- 必要性が高い箇所だけ `@Operation`, `@Parameter`, `@Schema`, `@SecurityRequirement` の追加を検討する
- annotation を入れすぎて controller が説明だらけにならないよう、適用範囲を限定する

## 運用メモ

- `docs/design/generated/openapi.yaml` は自動生成成果物として扱う
- `docs/design/04_api_spec.yaml` はレビュー・設計意図を含む仕様書として扱う
- 両者に差分がある場合、まず「実装差分」か「生成限界」かを切り分ける
