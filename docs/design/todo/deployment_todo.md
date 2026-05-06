# デプロイ設計 TODO

作成日: 2026-04-24

対象:

- `docs/design/08_deployment.md`
- `Dockerfile`
- `.github/workflows/deploy-stg.yml`

## 目的

現実装を正として `08_deployment.md` を更新したうえで、なお設計として詰め切れていない論点を TODO として整理する。

## 対応状況

| ID | 項目 | ステータス | 対応内容 | 備考 |
|---|---|---|---|---|
| DEP-01 | frontend 別リポジトリ依存の失敗時運用整理 | Closed | frontend checkout 失敗、PAT 失効、ref 不正時は GitHub Actions ログを一次確認先とし、デプロイ後のアプリ起動失敗は Railway ログを確認する運用に整理した。特別な自動復旧や追加ハンドリングは設けない | backend デプロイ全体に影響 |
| DEP-02 | ロールバック単位の整理 | Closed | backend commit と frontend ref を 1 セットのリリース単位として扱い、ロールバックも同じ組で戻す運用方針を整理した。`deploy-stg.yml` では backend commit / frontend ref / frontend commit を GitHub Actions summary へ残すようにした | 当面は同梱デプロイを正式運用とし、個別ロールバックは原則行わない |
| DEP-03 | Flyway migration 実行責務の明確化 | Closed | local / stg / prod ともにアプリ起動時の自動実行を正式運用とし、本番投入前に同一 migration を stg で検証する方針を `08_deployment.md` へ反映した | 小規模運用と GitHub Actions ベースの継続デプロイを前提に採用 |
| DEP-04 | Secrets / Variables の責務分担整理 | Closed | GitHub Actions Variables / Secrets、Railway 環境変数、local `.env` の現状対応表を `docs/ops/env_management.md` へ分離し、DB 接続キーは `SPRING_DATASOURCE_*` に統一した。prod も初回リリース時は Railway で stg 同構成とする前提まで反映した | 将来構成変更時は `env_management.md` を更新する |
| DEP-05 | セッション Cookie 属性の運用固定 | Closed | `docs/ops/cookie_management.md` に環境別の Cookie 管理表を追加し、`JSESSIONID` は session cookie 設定、`XSRF-TOKEN` は `CookieCsrfTokenRepository` customizer で `Secure` / `SameSite` を反映した | `Domain` は明示せず host-only cookie を維持 |
| DEP-06 | 監視・可観測性の実装方針整理 | Closed | Railway の deploy healthcheck と Railway logs を一次確認手段とする最小運用方針を `docs/ops/observability.md` に整理した | 外部監視基盤は必要になった段階で検討する |
| DEP-07 | presigned URL 暫定運用のデプロイ設計反映 | Closed | `08_deployment.md` へ反映し、現行採用値として `APP_STORAGE_S3_PRESIGNED_GET_EXPIRE_MINUTES=1` を運用する方針まで確定した | CloudFront 未導入。恒久対策は将来の構成見直しで扱う |
| DEP-08 | frontend ref の追跡性向上 | Closed | `deploy-stg.yml` で backend commit / frontend ref / frontend commit を GitHub Actions summary へ出力し、どの frontend ref を含む成果物か追跡できるようにした | STG デプロイ履歴の一次参照先は GitHub Actions summary |

## TODO 詳細

### 1. frontend 別リポジトリ依存の失敗時運用整理

対応済み:

- STG デプロイは backend workflow 内で frontend repository を checkout している
- frontend 取得に失敗すると backend デプロイも失敗する
- frontend checkout 失敗、PAT 失効、ref 不正時は GitHub Actions ログを一次確認先とする
- デプロイ後のアプリ起動失敗は Railway ログを一次確認先とする
- 特別な自動復旧や追加ハンドリングは設けず、ログ確認を基本対応とする

### 2. ロールバック単位の整理

対応済み:

- デプロイ成果物は backend と frontend build 産物を同梱した 1 イメージ
- 当面は同梱デプロイを正式運用とし、frontend / backend の個別ロールバックは原則行わない
- ロールバック時は backend commit と frontend ref を 1 セットのリリース単位として戻す運用とする
- どの組み合わせをデプロイしたかは GitHub Actions summary を参照する運用とする

### 3. Flyway migration 実行責務の明確化

対応済み:

- 実装では Flyway が有効
- local / stg / prod ともにアプリ起動時の自動実行を正式運用とする
- 継続的なコード変更と GitHub Actions ベースの CI/CD を前提に、migration 実行責務を deploy step へ分離しない
- 本番投入前に同一 migration を stg で検証する前提を `08_deployment.md` に反映した

### 4. Secrets / Variables の責務分担整理

対応済み:

- 現実装では GitHub Actions Variables / Secrets と Railway 側の環境変数を併用している
- local は `.env` と `compose.yaml`、stg は GitHub Actions + Railway、prod は実行環境変数で構成されている
- 継続参照する対応表は [env_management.md](/Users/432kg/IdeaProjects/footprint/docs/ops/env_management.md:1) で管理する


### 5. セッション Cookie 属性の運用固定

対応済み:

- Cookie 属性の運用方針は [cookie_management.md](/Users/432kg/IdeaProjects/footprint/docs/ops/cookie_management.md:1) で管理する
- `JSESSIONID` は `server.servlet.session.cookie.*` で `HttpOnly` / `Secure` / `SameSite` を環境別に固定した
- `XSRF-TOKEN` は `CookieCsrfTokenRepository.withHttpOnlyFalse()` を維持しつつ、customizer で `Secure` / `SameSite=Lax` を明示した
- `Domain` は設定せず host-only cookie を維持する

### 6. 監視・可観測性の実装方針整理

対応済み:

- `/actuator/health` は存在し、Railway の deploy healthcheck を正式な確認手段とする
- ログの一次確認先は Railway のデプロイログ / 実行ログとする
- 最小運用方針は [observability.md](/Users/432kg/IdeaProjects/footprint/docs/ops/observability.md:1) で管理する
- 外部監視基盤は必要になった段階で別途検討する

### 7. presigned URL 暫定運用のデプロイ設計反映

対応済み:

- 画像配信は stg / prod で S3 presigned URL を使う
- 将来は CloudFront private content を想定している
- presigned URL 有効期限は 1 分を採用する
- 期限切れ時はアプリケーションから画像 URL を再取得する運用を前提とする

### 8. frontend ref の追跡性向上

対応済み:

- workflow では `FRONTEND_REF_FOR_DEPLOY` を使って checkout している
- `deploy-stg.yml` で backend commit / frontend ref / frontend commit を GitHub Actions summary へ残すようにした

## 運用メモ

- `08_deployment.md` は現状構成の説明資料として維持する
- 未確定論点はこの TODO で管理する
