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

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | 未認証アクセス時に 401 を返す | `InsufficientAuthenticationException` | `response.status=401` |
