# `LastLoginUpdatingAuthenticationSuccessHandler` テスト仕様書

## 1. 基本情報

- 対象クラス: `LastLoginUpdatingAuthenticationSuccessHandler`
- 対象メソッド: `onAuthenticationSuccess(HttpServletRequest, HttpServletResponse, Authentication)`
- 対象パッケージ: `jp.i432kg.footprint.infrastructure.security`
- 対応するテストクラス: `LastLoginUpdatingAuthenticationSuccessHandlerTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をする処理か: 認証成功時、principal が `UserDetailsImpl` であれば `LastLoginRecorder` に委譲し、レスポンスステータスを `200 OK` に設定する
- 入力: `HttpServletRequest`, `HttpServletResponse`, `Authentication`
- 出力: `void`
- 主な副作用: `LastLoginRecorder.recordSuccessfulLogin(...)` 呼び出し、HTTP ステータス設定

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | principal が `UserDetailsImpl` | `recordSuccessfulLogin(userId)` を呼び、HTTP 200 を設定すること |
| 2 | 正常系 | principal が別型 | `recordSuccessfulLogin` を呼ばず、HTTP 200 は設定すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 認証成功時に recorder へ委譲して 200 を返す | `authentication.getPrincipal()` が `UserDetailsImpl` | `recordSuccessfulLogin(userId)` が呼ばれ、`response.status=200` |  |
| 2 | 正常系 | principal が `UserDetailsImpl` でない場合も 200 を返す | `authentication.getPrincipal()` が文字列など | recorder は未呼び出し、`response.status=200` |  |

## 5. 実装メモ

- モック化する依存: `LastLoginRecorder`, `Authentication`
- 固定化が必要な値: `AuthMapper.AuthUserEntity`, `UserId`
- `@DisplayName` 方針: principal の型差による分岐を日本語で記載する
- 備考: `MockHttpServletRequest` / `MockHttpServletResponse` を利用してステータス確認する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_delegateToRecorderAndReturnOk_when_principalIsUserDetailsImpl` | `LastLoginUpdatingAuthenticationSuccessHandler.onAuthenticationSuccess は UserDetailsImpl の principal を recorder へ委譲して 200 を返す` |
| 2 | `should_returnOkWithoutDelegation_when_principalIsNotUserDetailsImpl` | `LastLoginUpdatingAuthenticationSuccessHandler.onAuthenticationSuccess は principal が UserDetailsImpl でない場合も 200 を返す` |
