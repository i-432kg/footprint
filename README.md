# Footprint

Footprint は、写真付きの投稿を「タイムライン」と「地図」の両方から振り返れる Web アプリケーションです。  
旅行先、散歩中に見つけた場所、日常の記録を位置情報と一緒に残し、あとから場所を起点に見返せる体験を目指して作成しています。

このリポジトリは Footprint のバックエンドです。Spring Boot を中心に、認証、投稿・返信 API、画像保存、画像メタデータ処理、DB 永続化、サーバサイドでの画面配信を担当します。ポートフォリオとして、アプリ全体の構成や設計意図もこの README にまとめています。

## Related Repositories

| Repository | Role |
| --- | --- |
| [footprint](https://github.com/i-432kg/footprint) | Backend / API / server-side page delivery |
| [footprint-front](https://github.com/i-432kg/footprint-front) | Frontend / Vue application |
| [footprint-docs](https://github.com/i-432kg/footprint-docs) | Design documents / API and DB specifications |

## Product Overview

Footprint は、匿名 SNS をベースにしつつ、投稿を場所と強く結びつけて扱うアプリです。  
投稿は画像、コメント、位置情報を持ち、ユーザーは以下のような導線でコンテンツを閲覧できます。

- タイムラインで新しい投稿を追う
- キーワードで投稿を検索する
- 地図上で表示範囲内の投稿を探す
- 投稿詳細と返信を確認する
- マイページで自分の投稿や返信を振り返る

ログイン前でも About / Terms / Privacy などの静的ページは閲覧でき、投稿・画像・ユーザー情報を扱う主要機能は認証必須にしています。

## System Architecture

```text
Browser
  |
  | HTML: Spring Boot + Thymeleaf
  | JS/CSS: Vite build assets
  v
Spring Boot Backend
  |
  | REST API / Session Auth / CSRF
  v
Application Layer
  |
  | Command / Query use cases
  v
Domain Layer
  |
  | Repository ports
  v
Infrastructure
  |-- MySQL
  |-- Local file storage
  |-- S3-compatible object storage
```

フロントエンドは別リポジトリで管理しています。ローカル開発では Vite dev server の JS をバックエンドの Thymeleaf から読み込み、デプロイ時は Docker build 内でフロントエンドを build して Spring Boot の static resource に取り込みます。

## Tech Stack

### Backend

- Java 21
- Spring Boot 4.0.5
- Spring Web MVC
- Spring Security
- Spring Validation
- Thymeleaf
- MyBatis
- Flyway
- MySQL 8.4
- AWS SDK for Java 2.x
- metadata-extractor
- Lombok
- JSpecify

### Frontend Integration

- Vue 3
- Vite
- Vuetify
- Docker multi-stage build

### Infrastructure / Tooling

- Docker / Docker Compose
- GitHub Actions
- Railway
- Gradle
- springdoc-openapi

## Backend Responsibilities

- セッションベースのログイン / ログアウト
- CSRF 対応
- ユーザー登録
- 投稿作成、一覧、検索、詳細取得
- 地図表示向けの bbox 検索
- 投稿への返信、ネスト返信
- マイページ向けの自分の投稿 / 返信取得
- 画像保存と画像メタデータ抽出
- ProblemDetail 形式の API エラーレスポンス
- JSON 構造化ログ、access / auth / audit / app ログ
- local / stg / prod の profile 分離

## Design Highlights

### Layered Architecture

`presentation`, `application`, `domain`, `infrastructure` を分け、Controller から DB 実装へ直接依存しない構成にしています。

- `presentation`: REST API、画面ルーティング、リクエスト / レスポンス変換
- `application`: command / query use case、外部 I/O 用 port
- `domain`: entity、value object、domain service、repository port
- `infrastructure`: MyBatis mapper、storage 実装、security 実装、seed 処理
- `config`: Spring Security、storage、frontend asset、profile 別設定

### Command / Query Separation

投稿作成や返信作成などの更新系は command service、一覧や詳細取得などの参照系は query service に分けています。  
無限スクロール向けの一覧取得では offset ではなく `lastId` を使った seek pagination を採用しています。

### Value Object Based Validation

`PostId`, `UserId`, `EmailAddress`, `SearchKeyword`, `Latitude`, `Longitude` などを value object として表現し、presentation 層の Bean Validation だけに寄せず、domain 側でも不正値を防ぐ設計にしています。

### Storage Abstraction

画像保存は local file storage と S3-compatible storage を切り替えられるようにしています。  
local では開発しやすさを優先し、stg / prod では環境変数から S3 設定を注入する構成です。

### Frontend Asset Delivery

local profile では Thymeleaf が `http://localhost:5173` の Vite entry を参照します。  
stg / prod では Vite manifest を読み込み、build 済み JS/CSS を Spring Boot の static resource から配信します。favicon やロゴなどの public asset もフロントリポジトリで管理し、環境ごとに参照 URL を切り替えます。

### Operational Logging

API の access log、認証イベント、監査系イベント、アプリケーションエラーを用途ごとに分け、構造化ログとして出力できるようにしています。  
validation error や domain/application exception は `ProblemDetail` に変換し、フロントエンドが扱いやすいエラー形式に揃えています。

## Local Development

### Prerequisites

- Java 21
- Docker / Docker Compose
- Node.js 24
- npm

フロントエンドも同時に動かす場合は、隣接する `footprint-front` リポジトリを使用します。

```text
footprint/
footprint-front/
footprint-docs/
```

### Backend Setup

```bash
cp .env.example .env
docker compose up -d
./gradlew bootRun --args='--spring.profiles.active=local'
```

Backend runs on:

```text
http://localhost:8080
```

### Frontend Setup

```bash
cd ../footprint-front
npm ci
npm run dev
```

Vite dev server runs on:

```text
http://localhost:5173
```

local profile のバックエンドは、画面テンプレートから Vite dev server の entry JS を読み込みます。そのため、画面を確認する場合は backend と frontend の両方を起動してください。

## Test

```bash
./gradlew test
```

GitHub Actions の stg workflow では、backend test に加えて frontend lint / build も実行します。

## Deployment

デプロイは Railway を前提にしています。

- `develop` branch: stg deploy
- `main` branch: prod deploy
- frontend repository の ref は GitHub Actions variables で指定
- Docker build 内で `frontend/` を build し、`dist/` を Spring Boot static resource にコピー

Dockerfile は以下の multi-stage build です。

1. Node image で frontend を build
2. JDK image で Spring Boot jar を build
3. JRE image に executable jar を配置して起動

## Environment Variables

local profile では `.env.example` をベースに `.env` を作成します。

主な設定:

| Variable | Purpose |
| --- | --- |
| `MYSQL_DATABASE` | local MySQL database |
| `MYSQL_USER` | local MySQL user |
| `MYSQL_PASSWORD` | local MySQL password |
| `MYSQL_ROOT_PASSWORD` | local MySQL root password |
| `SPRING_DATASOURCE_URL` | Spring datasource URL |
| `SPRING_DATASOURCE_USERNAME` | Spring datasource username |
| `SPRING_DATASOURCE_PASSWORD` | Spring datasource password |
| `APP_LOCAL_SEED_TEST_PASSWORD` | local seed user password |

stg / prod の環境変数は Railway と GitHub Actions variables / secrets で管理します。詳細は `docs/ops/env_management.md` を参照してください。

## Directory Structure

```text
footprint/
├── .github/workflows/          # GitHub Actions workflows
├── docs/                       # ADR, UT design, operation notes
├── src/
│   ├── main/
│   │   ├── java/jp/i432kg/footprint/
│   │   │   ├── application/    # command/query use cases and ports
│   │   │   ├── config/         # Spring and application configuration
│   │   │   ├── domain/         # domain model, value objects, domain services
│   │   │   ├── infrastructure/ # datasource, storage, security, seed
│   │   │   ├── logging/        # access/auth/audit/app logging support
│   │   │   └── presentation/   # REST API and web controllers
│   │   └── resources/
│   │       ├── db/migration/   # Flyway migrations
│   │       ├── templates/      # Thymeleaf templates
│   │       └── application*.yml
│   └── test/
├── compose.yaml                # local MySQL
├── Dockerfile                  # frontend + backend build
├── build.gradle.kts
└── README.md
```

## Documentation

詳細な設計資料は docs リポジトリと、このリポジトリ内の `docs/` に分けて管理しています。

- API / DB / screen / deployment design: [footprint-docs](https://github.com/i-432kg/footprint-docs)
- ADR: `docs/adr/`
- UT design notes: `docs/ut/`
- Environment operation notes: `docs/ops/`

## Roadmap

- E2E test の導入
- OpenAPI annotation と API ドキュメントの継続整備
- 監視、アラート、ログ検索を前提にした運用設計の強化
- CloudFront などを含む画像配信構成の見直し
- production 運用を見据えた security / rate limit / backup 方針の整理
