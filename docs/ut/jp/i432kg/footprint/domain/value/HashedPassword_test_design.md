# `HashedPassword` テスト仕様書

## 1. 基本情報

- 対象クラス: `HashedPassword`
- 対象メソッド: `of(String)`, `from(RawPassword, Hasher)`
- 対象パッケージ: `jp.i432kg.footprint.domain.value`
- 対応するテストクラス: `HashedPasswordTest`
- 作成者: Codex
- 作成日: 2026-04-15

## 2. 対象概要

- 何をする処理か: ハッシュ化済みパスワード文字列を値オブジェクト化し、必要に応じて生パスワードから生成する
- 入力: `String`, `RawPassword`, `Hasher`
- 出力: `HashedPassword`
- 主な副作用: `Hasher.encode(...)` 呼び出し

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 直接生成 | 非空文字列から生成できること |
| 2 | 正常系 | ハッシュ生成 | `Hasher` の戻り値で生成できること |
| 3 | 異常系 | 必須 | `null`、blank を拒否すること |
| 4 | 異常系 | `from` 失敗 | `Hasher` の戻り値が不正なら失敗すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 非空文字列から生成する | `"$2a$10$..."` | 生成成功 |  |
| 2 | 異常系 | `null` を拒否する | `null` | `required("hashed_password")` |  |
| 3 | 異常系 | blank を拒否する | `" "` | `blank("hashed_password")` |  |
| 4 | 正常系 | `Hasher` の戻り値から生成する | `RawPassword`, `Hasher` スタブ | 生成成功し戻り値を保持 |  |
| 5 | 異常系 | `Hasher` が blank を返すと失敗する | `Hasher -> ""` | 例外 |  |
| 6 | 異常系 | `Hasher` が `null` を返すと失敗する | `Hasher -> null` | 例外 | 実装追従 |

## 5. 実装メモ

- モック化する依存: `Hasher`
- `@DisplayName` 方針: `HashedPassword.of / from の責務を分けて記載する`

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_createHashedPassword_when_valueIsNonBlank` | `HashedPassword.of は非空のハッシュ文字列を受け入れる` |
| 2 | `should_throwException_when_hashedPasswordIsNullOrBlank` | `HashedPassword.of は null または空白のみを拒否する` |
| 3 | `should_createHashedPassword_when_createdFromRawPassword` | `HashedPassword.from は Hasher の戻り値から生成する` |
| 4 | `should_throwException_when_hasherReturnsInvalidValue` | `HashedPassword.from は Hasher の戻り値が不正な場合に失敗する` |
