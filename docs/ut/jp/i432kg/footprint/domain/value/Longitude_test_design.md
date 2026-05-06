# `Longitude` テスト仕様書

## 1. 基本情報

- 対象クラス: `Longitude`
- 対象メソッド: `of(BigDecimal)`
- 対象パッケージ: `jp.i432kg.footprint.domain.value`
- 対応するテストクラス: `LongitudeTest`
- 作成者: Codex
- 作成日: 2026-04-15

## 2. 対象概要

- 何をする処理か: 経度を 6 桁小数に丸めたうえで範囲検証し、値オブジェクトを生成する
- 入力: `BigDecimal`
- 出力: `Longitude`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 範囲内 | -180 以上 180 以下を受け入れること |
| 2 | 正常系 | 丸め | 小数 6 桁へ `HALF_UP` で丸めること |
| 3 | 異常系 | 必須 | `null` を拒否すること |
| 4 | 境界値 | 上下限 | -180, 180 を受け入れること |
| 5 | 異常系 | 範囲外 | 丸め後に範囲外となる値を拒否すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 範囲内値を受け入れる | `139.123456` | 生成成功 |  |
| 2 | 正常系 | 6 桁に丸める | `139.1234564`, `139.1234565` | `139.123456`, `139.123457` |  |
| 3 | 異常系 | `null` を拒否する | `null` | `required("longitude")` |  |
| 4 | 境界値 | 下限値を受け入れる | `-180` | 生成成功 |  |
| 5 | 境界値 | 上限値を受け入れる | `180` | 生成成功 |  |
| 6 | 異常系 | 範囲外を拒否する | `180.000001`, `-180.000001` | `outOfRange(...)` |  |

## 5. 実装メモ

- 固定化が必要な値: 丸め確認用 `BigDecimal`
- `@DisplayName` 方針: `Longitude.of の丸めと範囲条件を記載する`

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_createLongitude_when_valueIsWithinRange` | `Longitude.of は範囲内の値を受け入れる` |
| 2 | `should_roundLongitudeHalfUp_when_valueHasMoreThanSixDecimals` | `Longitude.of は小数 6 桁へ四捨五入する` |
| 3 | `should_throwException_when_longitudeIsNull` | `Longitude.of は null を拒否する` |
| 4 | `should_createLongitude_when_valueIsBoundary` | `Longitude.of は上下限値を受け入れる` |
| 5 | `should_throwException_when_longitudeIsOutOfRange` | `Longitude.of は範囲外の値を拒否する` |
