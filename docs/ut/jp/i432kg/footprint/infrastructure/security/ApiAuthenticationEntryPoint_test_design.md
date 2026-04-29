# `ApiAuthenticationEntryPoint` テスト仕様書

## 1. 基本情報

- 対象クラス: `ApiAuthenticationEntryPoint`
- 対象メソッド: `commence(HttpServletRequest, HttpServletResponse, AuthenticationException)`
- 対象パッケージ: `jp.i432kg.footprint.infrastructure.security`
- 対応するテストクラス: `ApiAuthenticationEntryPointTest`

## 2. 対象概要

- 何をする処理か: API の未認証アクセス時に auth ログを出し、`401 Unauthorized` を返す

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 未認証時の応答 | 401 を返すこと |
| 2 | 正常系 | auth unauthorized ログ | `event`, `method`, `path` を key-value で出力すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | 未認証アクセス時に 401 を返す | `InsufficientAuthenticationException` | `response.status=401` |
| 2 | 正常系 | 未認証アクセスログを出力する | `GET /api/posts` | `event=AUTH_UNAUTHORIZED`, `method`, `path` を key-value で出力する |

## 5. 実装メモ

- ログ観点を追加する場合は `footprint.auth` に `ListAppender<ILoggingEvent>` を付与する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_returnUnauthorized_when_authenticationIsRequired` | `ApiAuthenticationEntryPoint は未認証アクセス時に 401 を返す` |
| 2 | `-` | `ログ観点: 未認証アクセスログを key-value で確認する（今後追加）` |
