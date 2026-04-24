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
| DEP-01 | frontend 別リポジトリ依存の失敗時運用整理 | Open | checkout 失敗、PAT 失効、ref 不正時の扱いを決める | backend デプロイ全体に影響 |
| DEP-02 | ロールバック単位の整理 | Open | backend commit と frontend ref を組で戻す運用を明文化する | 単独ロールバック不可 |
| DEP-03 | Flyway migration 実行責務の明確化 | Open | 起動時実行を正式運用とするか、事前実行へ分離するか決める | STG/PROD 起動失敗に直結 |
| DEP-04 | Secrets / Variables の責務分担整理 | Open | GitHub 側と Railway 側で何を持つかを明文化する | 保守性向上 |
| DEP-05 | セッション Cookie 属性の運用固定 | Open | Secure / SameSite / Domain などを環境別に決める | 認証運用に直結 |
| DEP-06 | 監視・可観測性の実装方針整理 | Open | Railway logs, healthcheck, 将来監視基盤の方針を決める | `06_log_design.md` と連動 |
| DEP-07 | presigned URL 暫定運用のデプロイ設計反映 | In Progress | ADR-021 に沿って `08_deployment.md` へ反映済み。運用値の確定が残る | CloudFront 未導入 |
| DEP-08 | frontend ref の追跡性向上 | Open | どの frontend ref を含む成果物か追跡できるようにする | 障害調査向け |

## TODO 詳細

### 1. frontend 別リポジトリ依存の失敗時運用整理

状況:

- STG デプロイは backend workflow 内で frontend repository を checkout している
- frontend 取得に失敗すると backend デプロイも失敗する

TODO:

- checkout 失敗時のリカバリ手順を決める
- PAT のローテーションや失効時の運用を明文化する
- backend 単独変更時の扱いを整理する

### 2. ロールバック単位の整理

状況:

- デプロイ成果物は backend と frontend build 産物を同梱した 1 イメージ

TODO:

- ロールバック時は backend commit と frontend ref を組で戻す運用を定義する
- どの組み合わせをデプロイしたか追跡できるようにする

### 3. Flyway migration 実行責務の明確化

状況:

- 実装では Flyway が有効
- ただし、migration を起動時実行に委ねるかどうかの運用判断が未固定

TODO:

- 現行どおりアプリ起動時 migration を正式運用とするか判断する
- 失敗時の切り戻しや確認手順を整理する

### 4. Secrets / Variables の責務分担整理

状況:

- 現実装では GitHub Actions Variables / Secrets と Railway 側の環境変数を併用している

TODO:

- GitHub 側に置く値
- Railway 側に置く値
- `.env` / local 用に置く値

を整理する

### 5. セッション Cookie 属性の運用固定

状況:

- セッション運用自体は設計済み
- ただし Cookie 属性の環境別方針が未固定

TODO:

- Secure
- SameSite
- Domain
- HTTPS 前提

の扱いを環境ごとに整理する

### 6. 監視・可観測性の実装方針整理

状況:

- `/actuator/health` は存在する
- ただしログ・ヘルスチェック・監視基盤の接続方針は未整理

TODO:

- Railway logs の扱い
- healthcheck の監視方法
- 将来 CloudWatch 等へどう寄せるか

を整理する

### 7. presigned URL 暫定運用のデプロイ設計反映

状況:

- 画像配信は stg / prod で S3 presigned URL を使う
- 将来は CloudFront private content を想定している

TODO:

- presigned URL 有効期限の運用値を決める
- CloudFront 移行条件を整理する

### 8. frontend ref の追跡性向上

状況:

- workflow では `FRONTEND_REF_FOR_DEPLOY` を使って checkout している
- ただし成果物や運用ログにどの frontend ref を含んだか残しきれていない

TODO:

- デプロイログへ ref を残す
- 必要ならアプリメタデータとして公開できるようにする

## 運用メモ

- `08_deployment.md` は現状構成の説明資料として維持する
- 未確定論点はこの TODO で管理する
