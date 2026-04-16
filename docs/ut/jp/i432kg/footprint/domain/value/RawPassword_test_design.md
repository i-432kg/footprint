# `RawPassword` テスト仕様書

## 1. 基本情報

- 対象クラス: `RawPassword`
- 対象メソッド: `of(String)`, `toString()`
- 対象パッケージ: `jp.i432kg.footprint.domain.value`
- 対応するテストクラス: `RawPasswordTest`
- 作成者: Codex
- 作成日: 2026-04-15

## 2. 対象概要

- 何をする処理か: 生パスワードを長さ・文字種で検証し、文字列化時の露出を防ぐ
- 入力: `String`
- 出力: `RawPassword`, マスク済み `toString()`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 長さ・文字種 | 8 文字以上 72 文字以下の ASCII 可視文字を受け入れること |
| 2 | 異常系 | 必須 | `null`、blank を拒否すること |
| 3 | 異常系 | 長さ | 7文字以下、73文字以上を拒否すること |
| 4 | 異常系 | 文字種 | スペース、全角、制御文字を拒否すること |
| 5 | エッジケース | マスキング | `toString()` が平文を露出しないこと |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 最小長を受け入れる | 8文字の ASCII 可視文字 | 生成成功 |  |
| 2 | 正常系 | 最大長を受け入れる | 72文字の ASCII 可視文字 | 生成成功 |  |
| 3 | 異常系 | `null` を拒否する | `null` | `required("password")` |  |
| 4 | 異常系 | blank を拒否する | `" "` | `blank("password")` |  |
| 5 | 異常系 | 長さ不足を拒否する | 7文字 | `outOfRange(...)` |  |
| 6 | 異常系 | 長さ超過を拒否する | 73文字 | `outOfRange(...)` |  |
| 7 | 異常系 | スペースを含む値を拒否する | `"abc defg"` | `invalidFormat(...)` |  |
| 8 | 異常系 | 全角文字を拒否する | `"あいうえお123"` | `invalidFormat(...)` |  |
| 9 | エッジケース | `toString()` が平文を露出しない | `RawPassword.of("Secret12!")` | `Secret12!` を含まず `********` を含む |  |

## 5. 実装メモ

- `@DisplayName` 方針: `RawPassword.of の制約とマスキングを分けて記載する`

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_createRawPassword_when_valueIsWithinLengthAndAsciiRange` | `RawPassword.of は長さ範囲内の ASCII 可視文字を受け入れる` |
| 2 | `should_throwException_when_rawPasswordIsNullOrBlank` | `RawPassword.of は null または空白のみを拒否する` |
| 3 | `should_throwException_when_rawPasswordLengthIsOutOfRange` | `RawPassword.of は長さ範囲外の値を拒否する` |
| 4 | `should_throwException_when_rawPasswordContainsUnsupportedCharacters` | `RawPassword.of は ASCII 可視文字以外を拒否する` |
| 5 | `should_maskValueInToString_when_rawPasswordIsCreated` | `RawPassword は toString で平文パスワードを露出しない` |
