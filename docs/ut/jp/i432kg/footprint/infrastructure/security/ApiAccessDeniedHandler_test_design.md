# `ApiAccessDeniedHandler` テスト仕様書

## 1. 基本情報

- 対象クラス: `ApiAccessDeniedHandler`
- 対象メソッド: `handle(HttpServletRequest, HttpServletResponse, AccessDeniedException)`
- 対象パッケージ: `jp.i432kg.footprint.infrastructure.security`
- 対応するテストクラス: `ApiAccessDeniedHandlerTest`

## 2. 対象概要

- 何をする処理か: API の認可失敗時に auth ログを出し、`403 Forbidden` を返す
- 補足: `CsrfException` の場合は `AUTH_CSRF_REJECTED` として扱う

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 一般的な認可失敗 | 403 を返すこと |
| 2 | 正常系 | CSRF 欠落 | 403 を返すこと |
| 3 | 正常系 | CSRF 不正 | 403 を返すこと |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | 認可失敗時に 403 を返す | `AccessDeniedException` | `response.status=403` |
| 2 | 正常系 | CSRF 欠落時に 403 を返す | `MissingCsrfTokenException` | `response.status=403` |
| 3 | 正常系 | CSRF 不正時に 403 を返す | `InvalidCsrfTokenException` | `response.status=403` |
