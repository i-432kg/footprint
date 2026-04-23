# Footprint 設計資料レビュー 対応状況

作成日: 2026-04-23

関連資料:

- `docs/design/review/implementation_gap_review.md`

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
| R-01 | `05_screen_spec.md`, `07_authz_authn.md` | 画面公開範囲と認可仕様が初期設計・現状 `SecurityConfig`・新方針で不一致 | Partial | ADR-021 に基づき、画面・API・画像配信を原則認証必須として更新する | In Progress | 画面の認証必須は採用。閲覧系 API と `/images/**` の `permitAll()` は実装修正対象 |
| R-02 | `04_api_spec.yaml`, `05_screen_spec.md`, `07_authz_authn.md` | API エンドポイント一覧が現実装と不一致 | Yes | 実装 API を基準に仕様書を更新する | Open | `/api/posts/search`, `/api/posts/search/map`, `/api/replies/{parentReplyId}`, `/api/users/me/**` など |
| R-03 | `03_database.md`, `05_screen_spec.md`, `06_log_design.md` | ページング仕様は `lastId` / `size` へ整理できるが、SQL の seek 条件が厳密でない | Partial | ADR-023 に基づき、設計書は `lastId` / `size` と複合ソートキー前提へ更新し、実装は後続で seek 条件を見直す | In Progress | `page.nextCursor` は不要。残課題は `ORDER BY created_at, id` と整合する境界条件 |
| R-04 | `03_database.md` | DB 設計が `id` 中心で、実装の `public_id` 中心設計に追随していない | Yes | テーブル定義、キー設計、カラム定義を実装準拠に更新する | Open | `public_id`, `birthdate`, `child_count`, `object_key`, `file_extension` など |
| R-05 | `05_screen_spec.md`, `01_overview.md`, `02_architecture.md` | 初期設計にない検索画面 `/search` が存在する | Yes | 検索画面と検索導線を資料へ追加する | Open | UI 詳細はフロントエンド実装確認後に確定する |
| R-06 | `05_screen_spec.md`, `04_api_spec.yaml` | サインアップ仕様に `birthDate` 必須、自動ログインが反映されていない | Yes | 登録項目と登録後挙動を実装準拠で更新する | Open | `userName`, `email`, `password`, `birthDate` |
| R-07 | `04_api_spec.yaml`, `07_authz_authn.md` | エラーレスポンスが独自 `ErrorResponse` 前提のまま | Yes | `ProblemDetail` + `errorCode` + `details` ベースへ更新する | Open | `GlobalExceptionHandler` 準拠 |
| R-08 | `06_log_design.md` | ログ設計が現状実装より先行している | Partial | 現状実装ベースへ縮小するか、将来設計として未実装を明示する | Open | `traceId`, `X-Trace-Id`, JSON access/auth/audit は未確認 |
| R-09 | `08_deployment.md`, `02_architecture.md`, `01_overview.md` | フロントエンド別リポジトリ取り込み構成が反映されていない | Yes | Docker build と STG デプロイフローを資料へ反映する | Open | GitHub Actions + Railway + frontend checkout |
| R-10 | `07_authz_authn.md` | ログイン/ログアウト経路が旧前提のまま | Yes | `POST /api/login`, `POST /api/logout`, `loginId` を明記する | Open | Spring Security 実装準拠 |
| R-11 | `08_deployment.md` | Railway + S3 方針は大筋維持されている | Yes | 差分補足のみでクローズ可否を判断する | Open | 詳細補足後に Closed 候補 |
| R-12 | `01_overview.md` | アプリの目的・コア機能は概ね現状実装と整合している | Yes | 軽微な補足のみでクローズ可否を判断する | Open | 検索機能追記後に Closed 候補 |
| R-13 | `07_authz_authn.md`, `08_deployment.md`, `SecurityConfig` | stg/prod の画像配信が S3 presigned URL 前提で、URL 保有者は期限内に未認証取得できる | Partial | CloudFront 導入までは presigned URL を短命化し、将来は CloudFront private content へ移行する | In Progress | ADR-021。`APP_STORAGE_S3_PRESIGNED_GET_EXPIRE_MINUTES` は 1〜5 分を推奨 |

## クローズ候補

以下は大きな設計破綻ではなく、軽微な追記または補足後にクローズしやすい項目。

| ID | 理由 | 次アクション | ステータス |
|---|---|---|---|
| R-11 | デプロイ方針自体は実装と大筋整合している | フロント別リポジトリ取り込み構成だけ追記する | Open |
| R-12 | 概要レベルでは大きな乖離が少ない | 検索機能などの軽微な追記を行う | Open |

## 更新ログ

| 日付 | 更新内容 | 更新者 |
|---|---|---|
| 2026-04-23 | 初版作成 | Codex |
| 2026-04-23 | ADR-021 に基づく認証必須方針と画像配信暫定方針を反映 | Codex |
| 2026-04-24 | ADR-023 に基づくページング方針と seek 条件の改善方針を反映 | Codex |
