# `InvalidValueException` テスト仕様書

## 1. 基本情報

- 対象クラス: `InvalidValueException`
- 対象メソッド: `required(...)`, `blank(...)`, `tooLong(...)`, `tooShort(...)`, `outOfRange(...)`, `invalidFormat(...)`, `invalid(...)`
- 対象パッケージ: `jp.i432kg.footprint.domain.exception`
- 対応するテストクラス: `InvalidValueExceptionTest`
- 作成者: Codex
- 作成日: 2026-04-16

## 2. 対象概要

- 何をする処理か: 値オブジェクトの入力値バリデーション失敗を表す例外を factory ごとに生成する
- 入力: `target`, `rejectedValue`, `min`, `max`, `expectedFormat`, `reason`
- 出力: `InvalidValueException`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | required | `required(...)` が `reason=required` の例外を生成すること |
| 2 | 正常系 | blank | `blank(...)` が空文字 rejectedValue を含む例外を生成すること |
| 3 | 正常系 | tooLong | `maxLength` を details に含むこと |
| 4 | 正常系 | tooShort | `minLength` を details に含むこと |
| 5 | 正常系 | outOfRange | `min`, `max`, `rejectedValue` を含むこと |
| 6 | 正常系 | invalidFormat | `expectedFormat` を含むこと |
| 7 | 正常系 | invalid | 任意 reason をそのまま message / details に反映すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | required 例外を生成する | `target=email` | `DOMAIN_INVALID_VALUE`, `reason=required` |  |
| 2 | 正常系 | blank 例外を生成する | `target=username` | `rejectedValue=\"\"` を含む |  |
| 3 | 正常系 | tooLong 例外を生成する | `target=comment`, `rejectedValue=abcdef`, `maxLength=5` | `maxLength=5` を含む |  |
| 4 | 正常系 | tooShort 例外を生成する | `target=password`, `rejectedValue=abc`, `minLength=8` | `minLength=8` を含む |  |
| 5 | 正常系 | outOfRange 例外を生成する | `target=pixel`, `rejectedValue=100001`, `min=0`, `max=100000` | `min`, `max` を含む |  |
| 6 | 正常系 | invalidFormat 例外を生成する | `target=email`, `rejectedValue=invalid`, `expectedFormat=@` | `expectedFormat=@` を含む |  |
| 7 | 正常系 | invalid 例外を生成する | `target=object_key`, `rejectedValue=../secret`, `reason=cannot contain ".."` | reason をそのまま反映 |  |

## 5. 実装メモ

- モック化する依存: なし
- 固定化が必要な値: `target`, `rejectedValue`, 各追加 details
- `@DisplayName` 方針: `factory メソッドごとの details と message を記載する`
- 備考:
  - すべて `ErrorCode.DOMAIN_INVALID_VALUE` を返す前提で確認する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_createRequiredException_withExpectedDetails` | `InvalidValueException.required は required の details を持つ例外を生成する` |
| 2 | `should_createBlankException_withExpectedDetails` | `InvalidValueException.blank は blank の details を持つ例外を生成する` |
| 3 | `should_createTooLongException_withExpectedDetails` | `InvalidValueException.tooLong は maxLength を含む例外を生成する` |
| 4 | `should_createTooShortException_withExpectedDetails` | `InvalidValueException.tooShort は minLength を含む例外を生成する` |
| 5 | `should_createOutOfRangeException_withExpectedDetails` | `InvalidValueException.outOfRange は範囲情報を含む例外を生成する` |
| 6 | `should_createInvalidFormatException_withExpectedDetails` | `InvalidValueException.invalidFormat は expectedFormat を含む例外を生成する` |
| 7 | `should_createInvalidException_withExpectedDetails` | `InvalidValueException.invalid は任意 reason を含む例外を生成する` |
