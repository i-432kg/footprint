# ADR: `/api` の認証系エラーは Security Handler で `ProblemDetail` を返し、共通 writer へ委譲する

## ステータス
Accepted

## 背景

ADR-024 で、Footprint の API エラーレスポンスは `ProblemDetail` を基本形式とし、機械判定用に `errorCode` と `details` を追加する方針を採用した。

`GlobalExceptionHandler` では application / domain / validation 例外に対してこの方針を実装済みであり、`application/problem+json` も明示運用している。

一方で、`/api` の認証系エラーは Spring Security の filter chain 内で処理されるため、`GlobalExceptionHandler` を通らない。

現状は次の入口が `response.sendError(...)` を直接返している。

- `ApiAuthenticationFailureHandler` (`POST /api/login` の認証失敗)
- `ApiAuthenticationEntryPoint` (保護された `/api/**` への未認証アクセス)
- `ApiAccessDeniedHandler` (403 / CSRF 拒否)

この構成では、`/api` の認証系エラーだけが `ProblemDetail`、`application/problem+json`、`errorCode`、`details` の契約から外れる。

設計書 `docs/design/tmp/07_authz_authn.md` と OpenAPI `docs/design/tmp/04_api_spec.yaml` は `/api` のエラー応答を `ProblemDetail` 前提で記述しており、実装との乖離がある。

ただし、この修正は Spring Security の応答経路、エラーコード設計、OpenAPI、テスト拡張を横断して見直す必要があり、現リリースでは影響範囲が大きい。

## 決定

`/api` の認証系エラーは、リリース後の追加タスクとして `ProblemDetail` へ統一する。

実装方針は次のとおりとする。

### 1. Security Handler 側で `ProblemDetail` を正規生成する

`GlobalExceptionHandler` に寄せず、Spring Security の filter chain 内で完結する 401 / 403 応答として整形する。

### 2. 生成処理は共通 writer へ集約する

`ApiAuthenticationFailureHandler`、`ApiAuthenticationEntryPoint`、`ApiAccessDeniedHandler` は入口クラスとして薄く保ち、`ProblemDetail` の body 書き込みは専用 collaborator へ委譲する。

これは ADR-029 の「入口クラスは役割名で保ち、具体的な副作用は委譲先へ分離する」方針に従う。

### 3. auth 系の `errorCode` は専用定数として管理する

認証系エラーは `BaseException` / `ErrorCode` enum を経由させない。

少なくとも次の分類を持てるようにする。

- `AUTH_LOGIN_FAILED`
- `AUTH_UNAUTHORIZED`
- `AUTH_FORBIDDEN`
- `AUTH_CSRF_REJECTED`

これらは security 由来の応答分類であり、application / domain の独自例外分類とは責務を分ける。

### 4. `details` は最小限かつ機微情報非依存とする

`details` にはクライアント分岐に必要な最小限の補足情報のみを入れる。

例:

- 認証失敗: `reason=invalid_credentials`
- 未認証: `reason=authentication_required`
- 認可失敗: `reason=access_denied`
- CSRF 拒否: `reason=csrf_rejected`

資格情報、セッションID、CSRFトークン、内部例外メッセージは外部へ出さない。

### 5. テストは status だけでなく契約項目まで確認する

次の観点をテストで担保する。

- HTTP status
- `Content-Type: application/problem+json`
- `errorCode`
- `details.reason`

対象経路は少なくとも次の 3 系統とする。

- `POST /api/login` の認証失敗
- 保護 API への未認証アクセス
- CSRF 拒否を含む 403 応答

## 理由

- Security 例外は `GlobalExceptionHandler` ではなく filter chain 内で完結するため、責務境界に沿って直す必要がある
- 3 つの入口クラスで JSON 組み立てを重複させると、将来の契約変更でずれやすい
- auth 系 `errorCode` を application / domain 例外の enum に混在させると、分類責務が曖昧になる
- `details` を最小限に固定することで、認証系エラーでも情報露出を抑制しやすい
- 既存テストは status のみで契約逸脱を検出できないため、再発防止にはレスポンス本文まで含めた確認が必要である

## 影響

### 良い影響

- `/api` 全体で `ProblemDetail` 契約を一貫させられる
- フロントエンドが認証系エラーでも `errorCode` / `details` ベースで分岐できる
- auth 系レスポンスの整形ロジックを 1 箇所へ集約できる

### 注意点

- Spring Security の応答経路をまたぐため、既存の認証失敗時挙動を回帰させない確認が必要になる
- OpenAPI とテストの更新を伴う
- auth 系 `errorCode` の命名規約は、今後の認証拡張でも破綻しない粒度で維持する必要がある

## 実装方針

1. `infrastructure.security` 配下に auth 系 `ProblemDetail` の共通 writer を追加する
2. 必要に応じて auth 系 `errorCode` 定数を同パッケージへ追加する
3. `ApiAuthenticationFailureHandler`、`ApiAuthenticationEntryPoint`、`ApiAccessDeniedHandler` を writer 呼び出しへ置き換える
4. OpenAPI と認証設計書の 401 / 403 応答例を実装へ合わせて補強する
5. 既存の handler テストを本文検証付きへ拡張し、必要なら `MockMvc` ベースの統合寄りテストを追加する

## 実施タイミング

本 ADR の実装は現リリースには含めず、リリース後の追加タスクとして扱う。

リリース後に着手する際は、`docs/design/todo/error_response_todo.md` の該当タスクと infrastructure review の No.3 を起点に進める。

## 既存 ADR との関係

- [adr_024_problem_detail_error_response_policy.md](./adr_024_problem_detail_error_response_policy.md) の `ProblemDetail` 基本方針を、Spring Security 起点の認証系エラーへ拡張する判断である
- [adr_029_api_security_handler_naming.md](./adr_029_api_security_handler_naming.md) の「入口クラスは薄く保ち、具体処理は委譲先へ分離する」方針を、エラーレスポンス生成にも適用する
- [adr_027_problem_detail_details_structure.md](./adr_027_problem_detail_details_structure.md) の `details` 正規化方針と整合するよう、auth 系でも object ベースの最小構造を採る
