# `ApiAuthenticationFailureHandler` テスト仕様書

## 1. 基本情報

- 対象クラス: `ApiAuthenticationFailureHandler`
- 対象メソッド: `onAuthenticationFailure(HttpServletRequest, HttpServletResponse, AuthenticationException)`
- 対象パッケージ: `jp.i432kg.footprint.infrastructure.security`
- 対応するテストクラス: `ApiAuthenticationFailureHandlerTest`

## 2. 対象概要

- 何をする処理か: API の認証失敗時に auth ログを出し、`401 Unauthorized` を返す
- 主な副作用: HTTP 401 設定、エラーメッセージ設定

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 認証失敗時の応答 | 401 と `Authentication Failed` を返すこと |
| 2 | 正常系 | auth failure ログ | `event`, `reason`, `method`, `path` を key-value で出力すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | 認証失敗時に 401 を返す | `BadCredentialsException` | `response.status=401`, `errorMessage=Authentication Failed` |
| 2 | 正常系 | 認証失敗ログを出力する | `POST /api/login`, `BadCredentialsException` | `event=AUTH_LOGIN_FAILURE`, `reason=INVALID_CREDENTIALS`, `method`, `path` を key-value で出力する |

## 5. 実装メモ

- ログ観点を追加する場合は `footprint.auth` に `ListAppender<ILoggingEvent>` を付与する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_returnUnauthorized_when_authenticationFails` | `ApiAuthenticationFailureHandler は認証失敗時に 401 を返す` |
| 2 | `-` | `ログ観点: 認証失敗ログを key-value で確認する（今後追加）` |
