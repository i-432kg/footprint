# Footprint 設計資料レビュー

作成日: 2026-04-23

## 1. 目的

`docs/design` 配下の初期設計資料と、現状の実装との差分を整理する。

前提:

- 初期設計資料は作成当初から更新されておらず、現在は実装を正とみなすべき箇所が多い
- 本リポジトリはバックエンド中心であり、画面 UI の詳細は別フロントエンドリポジトリ実装に依存する
- そのため画面仕様は、Thymeleaf テンプレート、ルーティング、API 契約、Security 設定を主な比較対象とする

## 2. 結論サマリ

大きな差分は以下の 5 点に集約される。

1. 認可仕様と実装、新たに採用した認証必須方針がずれていた
2. API エンドポイントとリクエスト/レスポンス仕様が大きく変わっていた
3. ページング仕様は `lastId` ベースへ整理できるが、SQL の seek 条件に改善余地があった
4. DB 設計が `id` 中心から `public_id` 中心へ変わっていた
5. 初期設計にない検索画面、検索 API、S3/フロント別リポジトリ連携などの実装が追加されていた

2026-04-25 時点の対応状況:

- 認可仕様、API エンドポイント、ページング実装、DB 定義の主要差分は設計・実装へ反映済み
- 未解消の主論点は、ログ設計、位置情報入力元、画像アップロード制約、ログイン成功後挙動、seed 運用である
- 詳細なクローズ状況は `docs/design/review/design_review_status.md` を参照する

## 3. 主要差分

### 3.1 認可仕様

初期設計では以下の方針になっている。

- `/timeline`, `/map` は未ログインでも閲覧可能
- `/mypage` はログイン必須
- API は「閲覧は公開、作成系はログイン必須」

一方、現状の `SecurityConfig` では、画面側の `permitAll` は `/login`, `/signup` と静的リソースのみであり、`/`, `/timeline`, `/map`, `/search`, `/mypage` を含むその他画面は `.anyRequest().authenticated()` の対象になっている。

現在の `SecurityConfig` では、未認証公開は `/login`、サインアップ、静的リソース、ヘルスチェック、local OpenAPI のみであり、`/api/**` は原則認証必須へ整理済みである。

local 画像配信用の `/images/**` は ResourceHandler としては残るが、`permitAll()` ではなく認証必須として扱う。stg / prod では API レスポンスで S3 presigned URL を返す実装になっている。

レビュー後の採用方針:

- 画面・API・画像配信は原則認証必須とする
- `/login`、サインアップ、静的アセット、ヘルスチェックのみ未認証アクセスを許可する
- CloudFront private content はすぐには導入せず、暫定的に S3 presigned URL の有効期限を 1〜5 分程度へ短縮する
- 将来は CloudFront signed cookies を第一候補として画像配信を private content 化する

関連実装:

- `src/main/java/jp/i432kg/footprint/config/SecurityConfig.java`
- `src/main/java/jp/i432kg/footprint/presentation/web/RootController.java`
- `src/main/java/jp/i432kg/footprint/presentation/helper/S3ImageUrlResolver.java`
- `src/main/resources/application-stg.yml`
- `src/main/resources/application-prod.yml`
- `docs/adr/adr_021_auth_required_and_temporary_presigned_image_url.md`

判断:

- 初期設計の「閲覧は公開、操作はログイン必須」は採用しない
- 画面・API・画像配信は原則認証必須とする
- `SecurityConfig` は新方針へ追随済みであり、`07_authz_authn.md` も整合済み
- stg / prod の presigned URL は暫定策として継続するが、既定 30 分は短縮対象

### 3.2 API 仕様

初期設計や `04_api_spec.yaml` では、以下の前提が多い。

- 地図検索 API は `GET /api/map/posts`
- 返信投稿 API は `POST /api/posts/{postId}/replies`
- マイページ API は `GET /api/me/posts`, `GET /api/me/replies`
- ログインは Spring Security 標準フォーム `POST /login`
- ページングは `cursor` / `limit` + `page.nextCursor`

現状実装は以下になっている。

- 投稿一覧: `GET /api/posts`
- 投稿検索: `GET /api/posts/search`
- 地図検索: `GET /api/posts/search/map`
- 投稿詳細: `GET /api/posts/{postId}`
- 投稿配下のトップレベル返信一覧: `GET /api/posts/{postId}/replies`
- 子返信一覧: `GET /api/replies/{parentReplyId}`
- 返信投稿: `POST /api/replies/{postId}/reply`
- 自分のプロフィール: `GET /api/users/me`
- 自分の投稿一覧: `GET /api/users/me/posts`
- 自分の返信一覧: `GET /api/users/me/replies`
- サインアップ: `POST /api/users`
- ログイン: `POST /api/login`
- ログアウト: `POST /api/logout`

関連実装:

- `src/main/java/jp/i432kg/footprint/presentation/api/PostRestController.java`
- `src/main/java/jp/i432kg/footprint/presentation/api/ReplyRestController.java`
- `src/main/java/jp/i432kg/footprint/presentation/api/UserRestController.java`
- `src/main/java/jp/i432kg/footprint/config/SecurityConfig.java`
- `docs/design/generated/openapi.yaml`
- `docs/design/todo/api_spec_todo.md`

判断:

- `04_api_spec.yaml` は実装準拠で更新済み
- 自動生成 OpenAPI は下書き、`04_api_spec.yaml` を最終仕様書とする運用を採る
- springdoc 自動生成だけではフォームログイン、multipart、認可方針、ProblemDetail 説明が不足するため、手修正は継続して必要
- API 仕様書まわりの残課題は `docs/design/todo/api_spec_todo.md` で管理する

### 3.3 ページング仕様

初期設計は以下を前提としている。

- opaque cursor を `cursor` パラメータで渡す
- `limit` を指定する
- レスポンスに `page.nextCursor` を含める

現状実装は以下になっている。

- `lastId` をクエリパラメータで受け取る
- 件数は `size`
- レスポンスは `List<...>` であり、ページ情報は返さない
- SQL は `ORDER BY created_at DESC, id DESC` に対して複合 seek 条件を採用し、初回表示用と継続取得用に分割している

関連実装:

- `src/main/java/jp/i432kg/footprint/presentation/api/PostRestController.java`
- `src/main/java/jp/i432kg/footprint/presentation/api/UserRestController.java`
- `src/main/resources/jp/i432kg/footprint/infrastructure/datasource/mapper/query/PostQueryMapper.xml`
- `src/main/resources/jp/i432kg/footprint/infrastructure/datasource/mapper/query/ReplyQueryMapper.xml`
- `docs/adr/adr_023_seek_pagination_boundary.md`
- `docs/adr/adr_025_seek_pagination_query_split.md`

判断:

- `cursor` / `limit` と `lastId` / `size` の差分は、仕様書を実装に合わせて読み替えれば大きな問題ではない
- `page.nextCursor` を返さない点も、現行フロントエンドの無限スクロール成立を踏まえると追加不要と判断する
- seek 条件は `ORDER BY created_at DESC, id DESC` と整合する複合条件へ修正済みである
- 可読性を優先し、初回表示用と継続取得用で SQL を分割する方針を採用した

### 3.4 DB 設計

初期設計では、各テーブルは主に `BIGINT id` ベースの参照を前提としている。

現状実装では、以下の方針へ変わっている。

- 各公開対象エンティティに `public_id CHAR(26)` を持つ
- FK も `public_id` を利用する設計になっている
- `users` に `birthdate`, `is_active`, `disabled`, `disabled_at` が存在する
- `replies` に `child_count` が存在する
- `post_images` は `path` ではなく `object_key`, `file_extension` を保持する

関連実装:

- `src/main/resources/db/migration/V1__init.sql`

判断:

- `03_database.md` は現状実装ベースへ更新済みである
- `id` と `public_id` の役割分担、FK に `public_id` を使う意図、`users` の状態系カラム、`replies.child_count` の整合性責務まで反映した

### 3.5 画面仕様

初期設計では画面は以下の 4 画面として整理されている。

- `/login`
- `/timeline`
- `/map`
- `/mypage`

現状実装では追加で `/search` が存在する。

関連実装:

- `src/main/java/jp/i432kg/footprint/presentation/web/RootController.java`
- `src/main/resources/templates/login.html`
- `src/main/resources/templates/timeline.html`
- `src/main/resources/templates/map.html`
- `src/main/resources/templates/mypage.html`
- `src/main/resources/templates/search.html`

判断:

- `05_screen_spec.md` には検索画面を追加すべき
- ただしモーダル構成や画面内 UI 詳細は別フロントエンドリポジトリ依存のため、このバックエンドリポジトリのみでは断定しない方がよい

### 3.6 サインアップ仕様

初期設計の新規登録項目は以下だった。

- username
- email
- password

現状実装では以下になっている。

- userName
- email
- password
- birthDate

また、登録成功後は `request.login(...)` により自動ログインする。

関連実装:

- `src/main/java/jp/i432kg/footprint/presentation/api/request/SignUpRequest.java`
- `src/main/java/jp/i432kg/footprint/presentation/api/UserRestController.java`

判断:

- `05_screen_spec.md` と `04_api_spec.yaml` の登録仕様は更新が必要

### 3.7 エラーレスポンス仕様

初期設計では `/api` のエラー応答を `ErrorResponse` 形式で統一するとしている。

現状実装では `GlobalExceptionHandler` により `ProblemDetail` を返し、`errorCode` と `details` を property として付与している。

関連実装:

- `src/main/java/jp/i432kg/footprint/presentation/api/GlobalExceptionHandler.java`

判断:

- 「統一エラー形式」という意図は維持されている
- ただし実際のレスポンス形式は初期設計の独自 `ErrorResponse` とは異なるため、設計資料の表現は `ProblemDetail` ベースへ更新した方がよい

### 3.8 ログ設計

初期設計では以下が想定されている。

- JSON 構造化ログ
- `traceId` の発番
- `X-Trace-Id` レスポンスヘッダ返却
- access/auth/app/audit のカテゴリ分割

現状実装で確認できたのは以下である。

- 例外処理時のアプリケーションログ
- 機微情報マスキング用の `SensitiveDataMasker`

一方で、以下は本リポジトリ上では確認できなかった。

- `traceId` 発番フィルタ
- `X-Trace-Id` レスポンス付与
- access/auth/audit の専用ロガー設定
- JSON ログ出力設定ファイル

関連実装:

- `src/main/java/jp/i432kg/footprint/presentation/api/GlobalExceptionHandler.java`
- `src/main/java/jp/i432kg/footprint/logging/masking/SensitiveDataMasker.java`

判断:

- `06_log_design.md` は現状実装に対して先行しすぎている
- 「将来設計」か「未実装項目」と明示するか、現状ベースに書き換えるべき

### 3.9 デプロイと構成

初期設計の Railway + S3 方針自体は大筋で維持されている。

現状実装で追加で明確になっていること:

- STG デプロイは GitHub Actions から Railway を実行する
- フロントエンドは別リポジトリを checkout して Docker build 時に取り込む
- `local` は `LOCAL` ストレージ、`stg` / `prod` は既定で `S3` を利用する

関連実装:

- `.github/workflows/deploy-stg.yml`
- `Dockerfile`
- `src/main/resources/application-local.yml`
- `src/main/resources/application-stg.yml`
- `src/main/resources/application-prod.yml`

判断:

- `08_deployment.md` は方向性としては近い
- ただし「フロント別リポジトリを build に取り込む」構成は反映した方がよい

## 4. 実装を正として設計へ反映すべき事項

以下は「設計漏れ」より「実装確定事項」とみなして設計資料に取り込むべき内容である。

- 画面として `/search` が存在する
- API は `/api/posts/search` と `/api/posts/search/map` を持つ
- 返信はトップレベルとネストで取得経路が分かれている
- ユーザー API は `/api/users/me/**` に寄せている
- ログインは `POST /api/login`、ログアウトは `POST /api/logout`
- サインアップ時に `birthDate` が必須
- サインアップ成功後は自動ログインする
- 公開 ID は `public_id` / ULID を採用している
- `stg` / `prod` は S3 保存前提
- 画面・API・画像配信は原則認証必須とする
- stg / prod の S3 presigned URL は CloudFront 導入までの暫定策とし、有効期限を短縮する
- フロントエンドは別リポジトリで管理し、ビルド成果物をバックエンドへ取り込む

## 5. 設計資料ごとの更新方針

### 5.1 `01_overview.md`

大枠は維持可能。以下だけ追記候補。

- 検索機能の追加
- フロントエンド別リポジトリ構成

### 5.2 `02_architecture.md`

以下を反映すると現状に近づく。

- フロントエンド別リポジトリからの build artifact 取り込み
- `/search` 画面の存在
- 返信 API の分岐構造

### 5.3 `03_database.md`

全面更新が望ましい。

- `public_id` ベースの設計
- 実カラム名への追随
- `users.birthdate` と状態系カラム
- `replies.child_count`
- `post_images.object_key` / `file_extension`

### 5.4 `04_api_spec.yaml`

最優先で再作成レベルの更新が必要。

- エンドポイント一覧
- リクエスト DTO
- レスポンス DTO
- `ProblemDetail` ベースのエラー
- `lastId` / `size` のページング仕様

### 5.5 `05_screen_spec.md`

以下の更新が必要。

- 画面一覧へ `/search` を追加
- API パスを実装へ合わせる
- 公開範囲は ADR-021 の「原則認証必須」方針と整合させる

### 5.6 `06_log_design.md`

二択。

1. 現状実装ベースへ縮小する
2. 将来設計として残し、未実装項目を明示する

現状のままだと「実装済み」と誤読されやすい。

### 5.7 `07_authz_authn.md`

以下を更新。

- `/api/login`, `/api/logout`
- `loginId` パラメータ
- `/api/users/me/**` 系 API
- 画面公開範囲
- API と画像配信の認証必須方針
- S3 presigned URL は CloudFront 導入までの短命な暫定 URL として扱う点
- CSRF の実装方式が `CookieCsrfTokenRepository.withHttpOnlyFalse()` である点

### 5.8 `08_deployment.md`

大枠は維持しつつ、以下を補足。

- GitHub Actions から Railway への STG デプロイ
- フロントエンド別リポジトリ checkout
- Docker build 内でフロント資産を static 配下へ組み込む方式

## 6. 優先更新順

1. `docs/design/04_api_spec.yaml`
2. `docs/design/05_screen_spec.md`
3. `docs/design/07_authz_authn.md`
4. `docs/design/03_database.md`
5. `docs/design/06_log_design.md`
6. `docs/design/02_architecture.md`
7. `docs/design/08_deployment.md`
8. `docs/design/01_overview.md`

## 7. 補足

画面 UI のモーダル詳細、一覧の見せ方、地図上の詳細挙動などは本バックエンドリポジトリのみでは確定できない。
そのため、画面仕様書を更新する場合はフロントエンドリポジトリと併せて最終確認するのが望ましい。
