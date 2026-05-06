# `FileExtension` テスト仕様書

## 1. 基本情報

- 対象クラス: `FileExtension`
- 対象メソッド: `of(String)`, `withDot()`
- 対象パッケージ: `jp.i432kg.footprint.domain.value`
- 対応するテストクラス: `FileExtensionTest`
- 作成者: Codex
- 作成日: 2026-04-15

## 2. 対象概要

- 何をする処理か: ファイル拡張子を検証し、正規化後の許可拡張子を保持する
- 入力: `String`
- 出力: `FileExtension`, ドット付き文字列表現
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 正規化 | trim、小文字化、先頭ドット除去を行うこと |
| 2 | 正常系 | 許可値 | `jpg`, `jpeg`, `png`, `gif` を受け入れること |
| 3 | 正常系 | 補助メソッド | `withDot()` がドット付き表現を返すこと |
| 4 | 異常系 | 必須 | `null`、blank を拒否すること |
| 5 | 異常系 | 長さ・形式 | 長すぎる値、許可外文字、未対応拡張子を拒否すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 正規化して保持する | `" .JpG "` | `jpg` を保持 |  |
| 2 | 正常系 | 許可拡張子を受け入れる | `jpeg`, `png`, `gif` | 生成成功 |  |
| 3 | 正常系 | ドット付き表現を返す | `jpg` | `.jpg` |  |
| 4 | 異常系 | `null` を拒否する | `null` | `required("extension")` |  |
| 5 | 異常系 | blank を拒否する | `" "` | `blank("extension")` |  |
| 6 | 異常系 | 最大長超過を拒否する | 11文字超 | `tooLong(...)` |  |
| 7 | 異常系 | 許可外文字を拒否する | `"jp-g"` | `invalidFormat(...)` |  |
| 8 | 異常系 | 未対応拡張子を拒否する | `"bmp"` | `invalid(...)` |  |

## 5. 実装メモ

- `@DisplayName` 方針: `FileExtension.of の正規化と拒否条件を簡潔に記載する`

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_normalizeFileExtension_when_valueHasDotSpacesAndUpperCase` | `FileExtension.of は先頭ドットを除去し小文字化した拡張子を保持する` |
| 2 | `should_createFileExtension_when_valueIsAllowed` | `FileExtension.of は許可された拡張子を受け入れる` |
| 3 | `should_returnExtensionWithDot_when_withDotIsCalled` | `FileExtension.withDot はドット付きの拡張子表現を返す` |
| 4 | `should_throwException_when_fileExtensionIsNullOrBlank` | `FileExtension.of は null または空白のみを拒否する` |
| 5 | `should_throwException_when_fileExtensionIsTooLong` | `FileExtension.of は最大長を超える値を拒否する` |
| 6 | `should_throwException_when_fileExtensionHasInvalidFormat` | `FileExtension.of は許可外文字を含む値を拒否する` |
| 7 | `should_throwException_when_fileExtensionIsUnsupported` | `FileExtension.of は未対応の拡張子を拒否する` |
