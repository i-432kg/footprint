# `SearchKeyword` テスト仕様書

## 1. 基本情報

- 対象クラス: `SearchKeyword`
- 対象メソッド: `of(String)`
- 対象パッケージ: `jp.i432kg.footprint.domain.value`
- 対応するテストクラス: `SearchKeywordTest`
- 作成者: Codex
- 作成日: 2026-04-15

## 2. 対象概要

- 何をする処理か: 検索キーワードを表す値オブジェクトを生成する
- 入力: `String`
- 出力: `SearchKeyword`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 通常入力 | 通常文字列を受け入れること |
| 2 | 異常系 | 必須 | `null`、blank を拒否すること |
| 3 | 境界値 | 長さ | 100文字以下を受け入れ、101文字を拒否すること |
| 4 | 異常系 | 制御文字 | 制御文字や改行を拒否すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 通常文字列を受け入れる | `"camera bag"` | 生成成功 | trim なし |
| 2 | 異常系 | `null` を拒否する | `null` | `required("search_keyword")` |  |
| 3 | 異常系 | blank を拒否する | `" "` | `blank("search_keyword")` |  |
| 4 | 境界値 | 最大長ちょうどを受け入れる | 100文字 | 生成成功 |  |
| 5 | 異常系 | 最大長超過を拒否する | 101文字 | `tooLong(...)` |  |
| 6 | 異常系 | 制御文字を拒否する | `"\n"`, `"\u0000"` | `invalidFormat(...)` |  |

## 5. 実装メモ

- `@DisplayName` 方針: `SearchKeyword.of の入力条件を記載する`

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_createSearchKeyword_when_valueIsValid` | `SearchKeyword.of は通常の検索キーワードを受け入れる` |
| 2 | `should_throwException_when_searchKeywordIsNullOrBlank` | `SearchKeyword.of は null または空白のみを拒否する` |
| 3 | `should_throwException_when_searchKeywordExceedsMaxLength` | `SearchKeyword.of は最大長を超える値を拒否する` |
| 4 | `should_throwException_when_searchKeywordContainsControlCharacters` | `SearchKeyword.of は制御文字を含む値を拒否する` |
