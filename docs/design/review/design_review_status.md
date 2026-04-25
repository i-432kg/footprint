# Footprint 設計資料レビュー 対応状況

作成日: 2026-04-23

関連資料:

- `docs/design/review/implementation_gap_review.md`
- `docs/design/todo/api_spec_todo.md`
- `docs/design/todo/db_design_todo.md`
- `docs/design/review/log_design_todo.md`
- `docs/design/review/deployment_todo.md`

## ステータス定義

| ステータス | 意味 |
|---|---|
| Open | 未対応。設計資料の更新要否をこれから判断または反映する |
| In Progress | 対応中。設計資料更新またはレビュー確認を進めている |
| Closed | クローズ済み。実装を正として問題ないと判断し、必要な整理が完了した |
| On Hold | いったん保留。フロントエンドリポジトリ確認など追加前提が必要 |

## 一覧

| ID | 対象資料 | レビュー項目 | 実装を正とする方針 | 対応内容 | ステータス | 備考 |
|---|---|---|---|---|---|---|
| R-01 | `05_screen_spec.md`, `07_authz_authn.md` | 画面公開範囲と認可仕様が初期設計・現状 `SecurityConfig`・新方針で不一致 | Yes | ADR-021 に基づき、画面・API・画像配信を原則認証必須として整理し、`SecurityConfig` を実装方針へ追随させた | Closed | 未認証公開は `POST /api/login`, `POST /api/users`, 静的資産, ヘルスチェック, local OpenAPI に限定 |
| R-02 | `04_api_spec.yaml`, `05_screen_spec.md`, `07_authz_authn.md` | API エンドポイント一覧が現実装と不一致 | Yes | `04_api_spec.yaml`, `05_screen_spec.md`, `07_authz_authn.md` を実装準拠へ更新し、自動生成との差分運用は `api_spec_todo.md` で整理した | Closed | API-05 は annotation 品質改善の保留であり、エンドポイント不一致自体は解消済み |
| R-03 | `03_database.md`, `05_screen_spec.md`, `06_log_design.md` | ページング仕様は `lastId` / `size` へ整理できるが、SQL の seek 条件が厳密でない | Yes | ADR-023 / ADR-025 に基づき、複合 seek 条件へ修正し、初回表示用 / 継続取得用 SQL 分割方針を設計・実装へ反映した | Closed | `page.nextCursor` は不要。`ORDER BY created_at DESC, id DESC` と整合する境界条件へ修正済み |
| R-04 | `03_database.md` | DB 設計が `id` 中心で、実装の `public_id` 中心設計に追随していない | Yes | テーブル定義、キー設計、カラム定義を実装準拠に更新した | Closed | `public_id`, `birthdate`, `child_count`, `object_key`, `file_extension` などを反映済み |
| R-05 | `05_screen_spec.md`, `01_overview.md`, `02_architecture.md` | 初期設計にない検索画面 `/search` が存在する | Yes | 検索画面と検索導線を資料へ追加した | Closed | `/search`, `/api/posts/search`, `/api/posts/search/map` を反映済み |
| R-06 | `05_screen_spec.md`, `04_api_spec.yaml` | サインアップ仕様に `birthDate` 必須、自動ログインが反映されていない | Yes | 登録項目と登録後挙動を実装準拠で更新した | Closed | `userName`, `email`, `password`, `birthDate` を反映済み |
| R-07 | `04_api_spec.yaml`, `07_authz_authn.md` | エラーレスポンスが独自 `ErrorResponse` 前提のまま | Yes | `04_api_spec.yaml` と `07_authz_authn.md` を `ProblemDetail` ベースへ更新した | Closed | ADR-024, `GlobalExceptionHandler` 準拠 |
| R-08 | `06_log_design.md` | ログ設計が現状実装より先行している | No | `06_log_design.md` を正とし、実装不足を TODO 化して追う | In Progress | TODO は `log_design_todo.md` で管理 |
| R-09 | `08_deployment.md`, `02_architecture.md`, `01_overview.md` | フロントエンド別リポジトリ取り込み構成が反映されていない | Partial | `08_deployment.md` は現実装ベースへ更新済み。残論点は TODO で管理する | In Progress | TODO は `deployment_todo.md` で管理 |
| R-10 | `07_authz_authn.md` | ログイン/ログアウト経路が旧前提のまま | Yes | `POST /api/login`, `POST /api/logout`, `loginId` を明記した | Closed | Spring Security 実装準拠 |
| R-11 | `08_deployment.md` | Railway + S3 方針は大筋維持されている | Yes | 現実装ベースの補足を反映した | Closed | frontend 別リポジトリ連携や presigned URL 暫定運用も追記済み |
| R-12 | `01_overview.md` | アプリの目的・コア機能は概ね現状実装と整合している | Yes | 検索機能、認証必須方針、`public_id`、画像配信方針などの補足を反映した | Closed | 概要レベルの主要差分は解消済み |
| R-13 | `07_authz_authn.md`, `08_deployment.md`, `SecurityConfig` | stg/prod の画像配信が S3 presigned URL 前提で、URL 保有者は期限内に未認証取得できる | Partial | CloudFront 導入までは presigned URL を短命化し、将来は CloudFront private content へ移行する | In Progress | ADR-021。`APP_STORAGE_S3_PRESIGNED_GET_EXPIRE_MINUTES` は 1〜5 分を推奨 |
| R-14 | `01_overview.md`, `05_screen_spec.md`, `04_api_spec.yaml` | 位置情報入力元の設計が「EXIF or ユーザー入力」のままだが、現実装は画像 EXIF 抽出のみ | Yes | 投稿作成仕様を実装準拠へ更新し、ユーザー手入力の位置情報は現状スコープ外と明記する | Open | `PostRequest` は `imageFile` と `comment` のみ。位置情報は保存後メタデータ抽出で決定 |
| R-15 | `01_overview.md`, `04_api_spec.yaml` | 画像アップロード制約が設計と実装で不一致（サイズ上限、許可形式、レート制限/タイムアウト） | Partial | 実装準拠の制約値へ更新し、未実装のレート制限/タイムアウトは TODO として切り出す | Open | 設計は 5MB・JPEG/PNG のみ。実装は multipart 10MB、拡張子は GIF/WEBP も許可 |
| R-16 | `05_screen_spec.md`, `07_authz_authn.md` | ログイン成功後の挙動が「タイムラインへリダイレクト」前提のまま | Yes | `POST /api/login` 成功時は `200 OK` を返し、画面遷移はフロント側で制御する前提へ更新する | Open | `LastLoginUpdatingAuthenticationSuccessHandler` は `200 OK` を返却し、サーバーリダイレクトしない |
| R-17 | `03_database.md`, `05_screen_spec.md`, `04_api_spec.yaml` | 返信取得の責務が「post_id で取得してアプリ側でツリー構築」前提のままだが、実装は 1 階層ずつ別 API 取得 | Partial | `03_database.md` にはトップレベル返信 / 子返信の分割取得を反映済み。`05_screen_spec.md` の表現整理と表示順保証の要否は別途整理する | In Progress | `GET /api/posts/{postId}/replies` と `GET /api/replies/{parentReplyId}` はどちらも 1 階層取得 |
| R-18 | `08_deployment.md`, `01_overview.md` | local/stg の seed 実行・cleanup 運用が設計資料に反映されていない | Yes | 起動時 seed runner と関連設定値を運用設計へ追記する | Open | `app.local-seed.*`, `app.stg-seed.*` と `ApplicationRunner` 実装あり |

## 更新ログ

| 日付 | 更新内容 | 更新者 |
|---|---|---|
| 2026-04-23 | 初版作成 | Codex |
| 2026-04-23 | ADR-021 に基づく認証必須方針と画像配信暫定方針を反映 | Codex |
| 2026-04-24 | ADR-023 に基づくページング方針と seek 条件の改善方針を反映 | Codex |
| 2026-04-24 | API仕様書の残課題 TODO を追加し、R-02 / R-07 の進捗を更新 | Codex |
| 2026-04-24 | サインアップ仕様反映済みとして R-06 を Closed へ更新 | Codex |
| 2026-04-24 | `07_authz_authn.md` を ADR-024 に追随させ、R-07 を Closed へ更新 | Codex |
| 2026-04-24 | ログ設計を実装目標として扱う TODO を追加し、R-08 の方針を更新 | Codex |
| 2026-04-24 | デプロイ設計を現実装ベースへ更新し、残論点 TODO を追加 | Codex |
| 2026-04-24 | 検索画面、ログイン/ログアウト経路、概要補足の反映に合わせて R-05 / R-10 / R-12 を Closed へ更新 | Codex |
| 2026-04-25 | 位置情報入力元、画像アップロード制約、ログイン成功後挙動、返信取得責務、seed 運用のレビュー漏れを R-14 〜 R-18 として追加 | Codex |
| 2026-04-25 | API / DB TODO と実装修正の反映に合わせて R-01 / R-02 / R-03 / R-04 を Closed へ更新し、R-17 の進捗を更新 | Codex |
