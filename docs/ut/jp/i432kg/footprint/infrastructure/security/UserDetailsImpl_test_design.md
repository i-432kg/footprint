# `UserDetailsImpl` テスト仕様書

## 1. 基本情報

- 対象クラス: `UserDetailsImpl`
- 対象メソッド: `fromEntity(AuthMapper.AuthUserEntity)`, `getAuthorities()`, `getPassword()`, `getUsername()`, `getUserId()`, `getDisplayUsername()`
- 対象パッケージ: `jp.i432kg.footprint.infrastructure.security`
- 対応するテストクラス: `UserDetailsImplTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をする処理か: `AuthMapper.AuthUserEntity` から Spring Security の `UserDetails` 実装を構築し、認証に必要な情報を提供する
- 入力: `AuthMapper.AuthUserEntity`
- 出力: `UserDetailsImpl`, 各種認証情報
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | entity 変換 | `fromEntity` が `userId`, email, displayUsername, password を保持すること |
| 2 | 正常系 | 認証情報取得 | `getUsername` が email、`getPassword` が password を返すこと |
| 3 | 正常系 | 権限 | `getAuthorities` が空リストを返すこと |
| 4 | 正常系 | 追加 getter | `getUserId`, `getDisplayUsername` が entity 値を返すこと |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | entity から `UserDetailsImpl` を生成する | `AuthUserEntity(userId, email, displayUsername, password)` | 各フィールドが保持される |  |
| 2 | 正常系 | 認証用 username と password を返す | 生成済み `UserDetailsImpl` | `getUsername=email`, `getPassword=password` |  |
| 3 | 正常系 | 権限なしの空リストを返す | 生成済み `UserDetailsImpl` | `getAuthorities().isEmpty()` |  |

## 5. 実装メモ

- モック化する依存: なし
- 固定化が必要な値: `AuthMapper.AuthUserEntity`
- `@DisplayName` 方針: Security 上の役割が分かる日本語見出しにする
- 備考: `isAccountNonExpired` などデフォルト実装委譲メソッドは必要に応じて追加確認する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_createUserDetailsFromEntity_when_fromEntityCalled` | `UserDetailsImpl.fromEntity は認証 entity から UserDetailsImpl を生成する` |
| 2 | `should_returnAuthenticationFields_when_accessorsCalled` | `UserDetailsImpl は認証に必要な username と password を返す` |
| 3 | `should_returnEmptyAuthorities_when_getAuthoritiesCalled` | `UserDetailsImpl.getAuthorities は空の権限リストを返す` |
