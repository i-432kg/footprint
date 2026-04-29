# `ApiAuthenticationSuccessHandler` テスト仕様書

## 1. 基本情報

- 対象クラス: `ApiAuthenticationSuccessHandler`
- 対象メソッド: `onAuthenticationSuccess(HttpServletRequest, HttpServletResponse, Authentication)`
- 対象パッケージ: `jp.i432kg.footprint.infrastructure.security`
- 対応するテストクラス: `ApiAuthenticationSuccessHandlerTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をする処理か: API の認証成功時、principal が `UserDetailsImpl` であれば `LastLoginRecorder` に委譲し、レスポンスステータスを `200 OK` に設定する
- 入力: `HttpServletRequest`, `HttpServletResponse`, `Authentication`
- 出力: `void`
- 主な副作用: `LastLoginRecorder.recordSuccessfulLogin(...)` 呼び出し、HTTP ステータス設定

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | principal が `UserDetailsImpl` | `recordSuccessfulLogin(userId)` を呼び、HTTP 200 を設定すること |
| 2 | 正常系 | principal が別型 | `recordSuccessfulLogin` を呼ばず、HTTP 200 は設定すること |
| 3 | 正常系 | auth success ログ | `UserDetailsImpl` の場合に `event`, `userId`, `username` を key-value で出力すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 認証成功時に recorder へ委譲して 200 を返す | `authentication.getPrincipal()` が `UserDetailsImpl` | `recordSuccessfulLogin(userId)` が呼ばれ、`response.status=200` |  |
| 2 | 正常系 | principal が `UserDetailsImpl` でない場合も 200 を返す | `authentication.getPrincipal()` が文字列など | recorder は未呼び出し、`response.status=200` |  |
| 3 | 正常系 | 認証成功ログを出力する | `authentication.getPrincipal()` が `UserDetailsImpl` | `event=AUTH_LOGIN_SUCCESS`, `userId`, `username` を key-value で出力する | 今後追加 |

## 5. 実装メモ

- モック化する依存: `LastLoginRecorder`, `Authentication`
- 固定化が必要な値: `AuthMapper.AuthUserEntity`, `UserId`
- `@DisplayName` 方針: principal の型差による分岐を日本語で記載する
- 備考:
  - `MockHttpServletRequest` / `MockHttpServletResponse` を利用してステータス確認する
  - ログ観点を追加する場合は `footprint.auth` に `ListAppender<ILoggingEvent>` を付与する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_delegateToRecorderAndReturnOk_when_principalIsUserDetailsImpl` | `ApiAuthenticationSuccessHandler.onAuthenticationSuccess は UserDetailsImpl の principal を recorder へ委譲して 200 を返す` |
| 2 | `should_returnOkWithoutDelegation_when_principalIsNotUserDetailsImpl` | `ApiAuthenticationSuccessHandler.onAuthenticationSuccess は principal が UserDetailsImpl でない場合も 200 を返す` |
| 3 | `-` | `ログ観点: 認証成功ログを key-value で確認する（今後追加）` |
