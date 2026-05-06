# `EmailAddress` テスト仕様書

## 1. 基本情報

- 対象クラス: `EmailAddress`
- 対象メソッド: `of(String)`
- 対象パッケージ: `jp.i432kg.footprint.domain.value`
- 対応するテストクラス: `EmailAddressTest`
- 作成者: Codex
- 作成日: 2026-04-15

## 2. 対象概要

- 何をする処理か: メールアドレスを検証し、前後空白除去と小文字化後の値を保持する
- 入力: `String`
- 出力: `EmailAddress`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 正規化 | 前後空白除去と小文字化を行うこと |
| 2 | 正常系 | 形式妥当 | 妥当なローカル部・ドメイン部を受け入れること |
| 3 | 異常系 | 必須 | `null`、blank を拒否すること |
| 4 | 異常系 | 全体長 | 254 文字超を拒否すること |
| 5 | 異常系 | `@` 個数 | `@` の欠落や複数を拒否すること |
| 6 | 異常系 | ローカル部 | blank、長さ超過、許可外文字、ドット位置不正を拒否すること |
| 7 | 異常系 | ドメイン部 | ドット不足、許可外文字、ドット位置不正を拒否すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 正規化した値を保持する | `"  Foo.Bar+1@Example.COM  "` | `foo.bar+1@example.com` を保持 |  |
| 2 | 正常系 | 妥当な形式を受け入れる | `"user.name@example.co.jp"` | 生成成功 |  |
| 3 | 異常系 | `null` を拒否する | `null` | `required("email")` |  |
| 4 | 異常系 | blank を拒否する | `"   "` | `blank(...)` |  |
| 5 | 異常系 | 全体長超過を拒否する | 255文字超 | `tooLong(...)` |  |
| 6 | 異常系 | `@` なしを拒否する | `"abc.example.com"` | `invalidFormat(...)` |  |
| 7 | 異常系 | `@` 複数を拒否する | `"a@b@c.com"` | `invalidFormat(...)` |  |
| 8 | 異常系 | ローカル部 blank を拒否する | `"@example.com"` | `blank("email_local_part")` |  |
| 9 | 異常系 | ローカル部先頭ドットを拒否する | `".user@example.com"` | 例外 |  |
| 10 | 異常系 | ローカル部末尾ドットを拒否する | `"user.@example.com"` | 例外 |  |
| 11 | 異常系 | ローカル部連続ドットを拒否する | `"us..er@example.com"` | 例外 |  |
| 12 | 異常系 | ローカル部長さ超過を拒否する | 65文字超のローカル部 | 例外 |  |
| 13 | 異常系 | ドメイン部ドットなしを拒否する | `"user@example"` | 例外 |  |
| 14 | 異常系 | ドメイン部連続ドットを拒否する | `"user@example..com"` | 例外 |  |
| 15 | 異常系 | ドメイン部先頭末尾ドットを拒否する | `"user@.example.com"`, `"user@example.com."` | 例外 |  |

## 5. 実装メモ

- 固定化が必要な値: 全体長 254、ローカル部長 64
- `@DisplayName` 方針: `EmailAddress.of の正規化と不正形式を観点別に記載する`

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_normalizeEmailAddress_when_valueHasSpacesAndUpperCase` | `EmailAddress.of は前後空白を除去し小文字化した値を保持する` |
| 2 | `should_createEmailAddress_when_valueHasValidFormat` | `EmailAddress.of は妥当なメールアドレス形式を受け入れる` |
| 3 | `should_throwException_when_emailAddressIsNull` | `EmailAddress.of は null を拒否する` |
| 4 | `should_throwException_when_emailAddressIsBlank` | `EmailAddress.of は空白のみを拒否する` |
| 5 | `should_throwException_when_emailAddressExceedsMaxLength` | `EmailAddress.of は最大長を超える値を拒否する` |
| 6 | `should_throwException_when_emailAddressHasInvalidAtCount` | `EmailAddress.of は @ の個数が不正な値を拒否する` |
| 7 | `should_throwException_when_emailLocalPartIsInvalid` | `EmailAddress.of は不正なローカル部を拒否する` |
| 8 | `should_throwException_when_emailDomainPartIsInvalid` | `EmailAddress.of は不正なドメイン部を拒否する` |
