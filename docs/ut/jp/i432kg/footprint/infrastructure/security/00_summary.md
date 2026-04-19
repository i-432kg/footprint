# `jp.i432kg.footprint.infrastructure.security` パッケージ UT仕様書サマリー

## 1. 対象概要

- 対象パッケージ: `jp.i432kg.footprint.infrastructure.security`
- 対象クラス: `LastLoginRecorder`, `LastLoginUpdatingAuthenticationSuccessHandler`, `UserDetailsImpl`, `UserDetailsServiceImpl`
- 個別仕様書: 各クラスごとに `ファイル名_test_design.md` を作成

## 2. 横断観点

| No. | 観点 | 内容 |
|---|---|---|
| 1 | 認証情報変換 | `AuthMapper.AuthUserEntity` から Spring Security が扱う情報へ正しく変換すること |
| 2 | 認証成功後処理 | 認証成功時に最終ログイン更新が適切に委譲されること |
| 3 | 例外時継続 | 監査目的の更新失敗が認証成功フローを妨げないこと |
| 4 | 認証ユーザー取得 | `UserDetailsServiceImpl` が login ID から認証ユーザーを取得し、不在時は `UsernameNotFoundException` に変換すること |
| 5 | Spring Security 契約 | `UserDetails` / `AuthenticationSuccessHandler` / `UserDetailsService` の契約に従うこと |

## 3. グルーピング方針

- 認証情報保持: `UserDetailsImpl`
  - `AuthUserEntity` からの変換と `UserDetails` 契約メソッドを確認する
- 認証情報取得: `UserDetailsServiceImpl`
  - `AuthMapper` への委譲と `UsernameNotFoundException` 変換を確認する
- 認証成功監査: `LastLoginRecorder`
  - 固定 `Clock` を使った更新時刻と例外握りつぶしを確認する
- 認証成功ハンドラ: `LastLoginUpdatingAuthenticationSuccessHandler`
  - principal 型判定、recorder 委譲、HTTP 200 設定を確認する

## 4. テスト実装メモ

- モック化する依存:
  - `AuthMapper`
  - `LastLoginRecorder`
  - `Clock`
- 固定化する値:
  - `Clock.fixed(...)` による最終ログイン更新時刻
  - `AuthMapper.AuthUserEntity` の `UserId`, email, displayUsername, password
- `UserDetailsImpl` は Spring Security デフォルト実装に委譲している `isAccountNonExpired` なども必要に応じて確認する
- `LastLoginUpdatingAuthenticationSuccessHandler` は principal が `UserDetailsImpl` でないケースも確認する
- `UserDetailsServiceImpl` は `EmailAddress.of(username)` への変換と mapper 呼び出し引数を確認する
