# `CoordinateValidation` テスト仕様書

## 1. 基本情報

- 対象クラス: `CoordinateValidation`
- 対象メソッド: `validate(String, BigDecimal, BigDecimal, BigDecimal, int)`
- 対象パッケージ: `jp.i432kg.footprint.domain.helper`
- 対応するテストクラス: `CoordinateValidationTest`
- 作成者: Codex
- 作成日: 2026-04-16

## 2. 対象概要

- 何をする処理か: 緯度・経度などの座標値に対して必須チェック、丸め、範囲検証を行い、正規化済みの値を返す
- 入力: `fieldName`, `BigDecimal`, `min`, `max`, `scale`
- 出力: 正規化済み `BigDecimal`
- 主な副作用: 条件に応じて `InvalidValueException` を送出する

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 妥当値 | 範囲内の値を指定した場合に指定 scale で丸めた値を返すこと |
| 2 | 正常系 | 境界値 | 最小値・最大値ちょうどを受け入れること |
| 3 | 異常系 | 必須 | `value == null` の場合に `required(...)` を送出すること |
| 4 | 異常系 | 範囲外 | 丸め後の値が最小値未満または最大値超過の場合に `outOfRange(...)` を送出すること |
| 5 | エッジケース | 丸め起因の範囲外 | 入力値自体は近傍でも、丸め後に上限超過となる場合は拒否すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | scale に従って丸めた値を返す | `value=35.1234567`, `min=-90`, `max=90`, `scale=6` | `35.123457` を返す | `HALF_UP` |
| 2 | 正常系 | 最小値と最大値を受け入れる | `value=-90`, `90` | 生成成功 | 境界値 |
| 3 | 異常系 | `null` を拒否する | `value=null` | `required(fieldName)` |  |
| 4 | 異常系 | 下限未満を拒否する | `value=-90.000001`, `scale=6` | `outOfRange(...)` | 丸め後も下限未満 |
| 5 | 異常系 | 上限超過を拒否する | `value=90.000001`, `scale=6` | `outOfRange(...)` | 丸め後も上限超過 |
| 6 | エッジケース | 丸め後に上限超過となる値を拒否する | `value=90.0000006`, `scale=6` | `outOfRange(...)` | 丸め前は近傍、丸め後は `90.000001` |

## 5. 実装メモ

- モック化する依存: なし
- 固定化が必要な値: `min`, `max`, `scale`
- `@DisplayName` 方針: `CoordinateValidation.validate の丸めと範囲判定を日本語で記載する`
- 備考:
  - 範囲判定は入力値そのものではなく、`setScale(scale, HALF_UP)` 後の値で行われる
  - 例外時の `rejectedValue` も丸め後の値になる前提で確認する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_returnRoundedValue_when_valueIsWithinRange` | `CoordinateValidation.validate は範囲内の値を指定 scale で丸めて返す` |
| 2 | `should_returnValue_when_valueIsAtBoundary` | `CoordinateValidation.validate は最小値と最大値を受け入れる` |
| 3 | `should_throwException_when_valueIsNull` | `CoordinateValidation.validate は null を拒否する` |
| 4 | `should_throwException_when_valueIsLessThanMinimum` | `CoordinateValidation.validate は下限未満を拒否する` |
| 5 | `should_throwException_when_valueExceedsMaximum` | `CoordinateValidation.validate は上限超過を拒否する` |
| 6 | `should_throwException_when_roundedValueExceedsMaximum` | `CoordinateValidation.validate は丸め後に上限超過となる値を拒否する` |
