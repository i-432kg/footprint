# `UserDetailsServiceImpl` テスト仕様書

## 1. 基本情報

- 対象クラス: `UserDetailsServiceImpl`
- 対象メソッド: `loadUserByUsername(String)`
- 対象パッケージ: `jp.i432kg.footprint.infrastructure.security`
- 対応するテストクラス: `UserDetailsServiceImplTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をする処理か: login ID の email 文字列を `EmailAddress` 値オブジェクトへ変換し、`AuthMapper` から認証ユーザーを取得して `UserDetailsImpl` へ変換する。不在時は `UsernameNotFoundException` を送出する
- 入力: `username`（メールアドレス文字列）
- 出力: `UserDetailsImpl`
- 主な副作用: `AuthMapper.findAuthUserByLoginId(...)` 呼び出し

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 認証ユーザー取得 | `EmailAddress.of(username)` へ変換して mapper に委譲し、`UserDetailsImpl` を返すこと |
| 2 | 異常系 | ユーザー不存在 | mapper が空を返した場合に `UsernameNotFoundException` を送出すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | email から認証ユーザーを取得する | `findAuthUserByLoginId(EmailAddress.of(username))` が entity を返す | `UserDetailsImpl` が返り、entity の情報を保持する | mapper 引数も確認 |
| 2 | 異常系 | ユーザー不存在時に `UsernameNotFoundException` を送出する | mapper が `Optional.empty()` | `UsernameNotFoundException` | メッセージに username を含む |

## 5. 実装メモ

- モック化する依存: `AuthMapper`
- 固定化が必要な値: username, `AuthMapper.AuthUserEntity`
- `@DisplayName` 方針: 正常取得 / 不存在変換を日本語で記載する
- 備考: username 自体のバリデーション失敗は `EmailAddress.of(...)` の責務なのでここでは主対象にしない

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_returnUserDetails_when_userExists` | `UserDetailsServiceImpl.loadUserByUsername は email から認証ユーザーを取得して UserDetailsImpl を返す` |
| 2 | `should_throwUsernameNotFoundException_when_userDoesNotExist` | `UserDetailsServiceImpl.loadUserByUsername はユーザーが存在しない場合に UsernameNotFoundException を送出する` |
