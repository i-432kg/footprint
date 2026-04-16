# `FileName` テスト仕様書

## 1. 基本情報

- 対象クラス: `FileName`
- 対象メソッド: `of(String)`
- 対象パッケージ: `jp.i432kg.footprint.domain.value`
- 対応するテストクラス: `FileNameTest`
- 作成者: Codex
- 作成日: 2026-04-15

## 2. 対象概要

- 何をする処理か: 危険文字やパストラバーサルを含まないファイル名を表す値オブジェクトを生成する
- 入力: `String`
- 出力: `FileName`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 妥当な値 | 通常のファイル名を受け入れること |
| 2 | 異常系 | 必須 | `null`、blank を拒否すること |
| 3 | 境界値 | 長さ | 254文字以下を受け入れ、255文字超を拒否すること |
| 4 | 異常系 | 危険文字 | OS/URL危険文字を拒否すること |
| 5 | 異常系 | パストラバーサル | `..` を含む値を拒否すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 妥当なファイル名を受け入れる | `photo_01.jpg` | 生成成功 |  |
| 2 | 異常系 | `null` を拒否する | `null` | `required("filename")` |  |
| 3 | 異常系 | blank を拒否する | `" "` | `blank("filename")` |  |
| 4 | 境界値 | 最大長ちょうどを受け入れる | 254文字 | 生成成功 |  |
| 5 | 異常系 | 最大長超過を拒否する | 255文字 | `tooLong(...)` |  |
| 6 | 異常系 | 危険文字を拒否する | `/`, `\\`, `:`, `*`, `?`, `"`, `<`, `>`, `|` を含む値 | `invalidFormat(...)` |  |
| 7 | 異常系 | `..` を拒否する | `abc..jpg` | `invalid(...)` |  |

## 5. 実装メモ

- `@DisplayName` 方針: `FileName.of の安全性観点を見出しで示す`

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_createFileName_when_valueIsValid` | `FileName.of は妥当なファイル名を受け入れる` |
| 2 | `should_throwException_when_fileNameIsNullOrBlank` | `FileName.of は null または空白のみを拒否する` |
| 3 | `should_createFileName_when_valueLengthIsMaxBoundary` | `FileName.of は最大長ちょうどの値を受け入れる` |
| 4 | `should_throwException_when_fileNameExceedsMaxLength` | `FileName.of は最大長を超える値を拒否する` |
| 5 | `should_throwException_when_fileNameContainsInvalidCharacters` | `FileName.of は危険文字を含む値を拒否する` |
| 6 | `should_throwException_when_fileNameContainsTraversalPattern` | `FileName.of は .. を含む値を拒否する` |
