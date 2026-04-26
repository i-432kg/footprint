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

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | 認証失敗時に 401 を返す | `BadCredentialsException` | `response.status=401`, `errorMessage=Authentication Failed` |
