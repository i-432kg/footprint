# 環境変数・Secrets・Variables 対応表

作成日: 2026-04-29

## 目的

local / stg / prod の起動・デプロイに必要な設定値について、どこで管理するかを継続参照できる形で整理する。

## 管理方針

- GitHub Actions Variables:
  - リポジトリ参照先やサービス名など、workflow の分岐・対象選択に使う非機密値を置く
- GitHub Actions Secrets:
  - GitHub から外部サービスへ接続するためのトークンを置く
- Railway / 実行環境変数:
  - アプリ実行時に必要な DB / S3 / seed 設定を置く
- local `.env`:
  - local 起動に必要な DB / seed のみを置く

## アプリ環境別の値

| キー | 用途 | local | stg | prod | 備考 |
|---|---|---|---|---|---|
| `MYSQL_DATABASE` | local MySQL DB 名 | ○ | × | × | `compose.yaml` が参照 |
| `MYSQL_USER` | local MySQL 接続ユーザー | ○ | × | × | `compose.yaml` が参照 |
| `MYSQL_PASSWORD` | local MySQL 接続パスワード | ○ | × | × | `compose.yaml` が参照 |
| `MYSQL_ROOT_PASSWORD` | local MySQL root パスワード | ○ | × | × | `compose.yaml` が参照 |
| `SPRING_DATASOURCE_URL` | Spring DB 接続 URL | ○ | ○ | ○ | local / stg / prod で同名キーを採用 |
| `SPRING_DATASOURCE_USERNAME` | Spring DB 接続ユーザー | ○ | ○ | ○ | local / stg / prod で同名キーを採用 |
| `SPRING_DATASOURCE_PASSWORD` | Spring DB 接続パスワード | ○ | ○ | ○ | local / stg / prod で同名キーを採用 |
| `APP_OGP_SITE_BASE_URL` | OGP の絶対 URL 生成に使う公開 origin | ○ | ○ | ○ | local は `http://localhost:8080`、stg / prod は公開 URL |
| `APP_STORAGE_TYPE` | ストレージ種別 | ○ | ○ | ○ | local は `LOCAL`、stg / prod は既定 `S3` |
| `APP_STORAGE_IMAGE_CSP_ALLOW_ORIGINS` | CSP の画像許可 origin | × | ○ | ○ | 必要時のみ設定 |
| `APP_STORAGE_IMAGE_BASE_URL` | 画像 base URL | ○ | × | × | local 既定 `/images/`。`WebMvcConfig` / `LocalImageUrlResolver` は default 付き |
| `APP_STORAGE_S3_ENDPOINT` | S3 endpoint | × | ○ | ○ | MinIO 等の互換 endpoint 含む |
| `APP_STORAGE_S3_BUCKET_NAME` | S3 bucket 名 | × | ○ | ○ | 必須 |
| `APP_STORAGE_S3_REGION` | S3 region | × | ○ | ○ | 既定値 `ap-northeast-1` |
| `APP_STORAGE_S3_ACCESS_KEY` | S3 access key | × | ○ | ○ | 機微情報 |
| `APP_STORAGE_S3_SECRET_KEY` | S3 secret key | × | ○ | ○ | 機微情報 |
| `APP_STORAGE_S3_PATH_STYLE` | path-style 利用有無 | × | ○ | ○ | 既定値 `false` |
| `APP_STORAGE_S3_PRESIGNED_GET_EXPIRE_MINUTES` | presigned URL 有効期限 | × | ○ | ○ | 現行採用値は `1` 分 |
| `APP_LOCAL_SEED_TEST_PASSWORD` | local seed 用テストパスワード | ○ | × | × | local 専用 |
| `APP_STG_SEED_ENABLED` | stg seed 実行可否 | × | ○ | × | 検証時のみ `true` |
| `APP_STG_SEED_CLEANUP_ONLY` | stg seed cleanup のみ実行 | × | ○ | × | 既定値 `false` |
| `APP_STG_SEED_CLEANUP_BEFORE_SEED` | stg seed 前 cleanup | × | ○ | × | 既定値 `false` |
| `APP_STG_SEED_TEST_PASSWORD` | stg seed 用テストパスワード | × | ○ | × | 機微情報 |
| `APP_STG_SEED_EMAIL_PREFIX` | stg seed メール prefix | × | ○ | × | 既定値あり |
| `APP_STG_SEED_SOURCE_BUCKET_NAME` | stg seed 元画像 bucket | × | ○ | × | seed 実行時必須 |
| `APP_STG_SEED_MANIFEST_OBJECT_KEY` | stg seed manifest key | × | ○ | × | 既定値あり |

## GitHub Actions の値

現状は stg 向け workflow のみ存在する。prod も初回リリース時は Railway を利用し、stg と同系統の構成で運用する前提とする。

| キー | 種別 | 用途 | `deploy-stg.yml` | 備考 |
|---|---|---|---|---|
| `FRONTEND_REPO` | Variable | STG build 時の frontend repository | ○ | checkout 対象 |
| `FRONTEND_REF` | Variable | STG build 時の既定 frontend ref | ○ | `repository_dispatch` payload があれば上書き |
| `RAILWAY_SERVICE` | Variable | `railway up` 対象サービス名 | ○ | STG デプロイ workflow が参照 |
| `FRONTEND_REPO_PAT` | Secret | frontend repository checkout token | ○ | GitHub Secrets 管理 |
| `RAILWAY_TOKEN` | Secret | Railway CLI 認証トークン | ○ | GitHub Secrets 管理 |

## Railway / 実行環境変数の値

| キー | 用途 | stg | prod | 備考 |
|---|---|---|---|---|
| `SPRING_DATASOURCE_URL` | Spring DB 接続 URL | ○ | ○ | stg / prod で利用 |
| `SPRING_DATASOURCE_USERNAME` | Spring DB 接続ユーザー | ○ | ○ | stg / prod で利用 |
| `SPRING_DATASOURCE_PASSWORD` | Spring DB 接続パスワード | ○ | ○ | stg / prod で利用 |
| `APP_OGP_SITE_BASE_URL` | OGP の絶対 URL 生成に使う公開 origin | ○ | ○ | `https://...` 形式で設定 |
| `APP_STORAGE_TYPE` | ストレージ種別 | ○ | ○ | 既定値は `S3` |
| `APP_STORAGE_IMAGE_CSP_ALLOW_ORIGINS` | CSP の画像許可 origin | ○ | ○ | 必要時のみ設定 |
| `APP_STORAGE_S3_ENDPOINT` | S3 endpoint | ○ | ○ | 必須 |
| `APP_STORAGE_S3_BUCKET_NAME` | S3 bucket 名 | ○ | ○ | 必須 |
| `APP_STORAGE_S3_REGION` | S3 region | ○ | ○ | 既定値あり |
| `APP_STORAGE_S3_ACCESS_KEY` | S3 access key | ○ | ○ | 機微情報 |
| `APP_STORAGE_S3_SECRET_KEY` | S3 secret key | ○ | ○ | 機微情報 |
| `APP_STORAGE_S3_PATH_STYLE` | path-style 利用有無 | ○ | ○ | 任意設定 |
| `APP_STORAGE_S3_PRESIGNED_GET_EXPIRE_MINUTES` | presigned URL 有効期限 | ○ | ○ | 現行採用値は `1` 分 |
| `APP_STG_SEED_ENABLED` | stg seed 実行可否 | ○ | × | 検証時のみ `true` |
| `APP_STG_SEED_CLEANUP_ONLY` | stg seed cleanup のみ実行 | ○ | × | 既定値 `false` |
| `APP_STG_SEED_CLEANUP_BEFORE_SEED` | stg seed 前 cleanup | ○ | × | 既定値 `false` |
| `APP_STG_SEED_TEST_PASSWORD` | stg seed 用テストパスワード | ○ | × | 機微情報 |
| `APP_STG_SEED_EMAIL_PREFIX` | stg seed メール prefix | ○ | × | 既定値あり |
| `APP_STG_SEED_SOURCE_BUCKET_NAME` | stg seed 元画像 bucket | ○ | × | seed 実行時必須 |
| `APP_STG_SEED_MANIFEST_OBJECT_KEY` | stg seed manifest key | ○ | × | 既定値あり |
