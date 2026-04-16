# `Latitude` テスト仕様書

## 1. 基本情報

- 対象クラス: `Latitude`
- 対象メソッド: `of(BigDecimal)`
- 対象パッケージ: `jp.i432kg.footprint.domain.value`
- 対応するテストクラス: `LatitudeTest`
- 作成者: Codex
- 作成日: 2026-04-15

## 2. 対象概要

- 何をする処理か: 緯度を 6 桁小数に丸めたうえで範囲検証し、値オブジェクトを生成する
- 入力: `BigDecimal`
- 出力: `Latitude`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 範囲内 | -90 以上 90 以下を受け入れること |
| 2 | 正常系 | 丸め | 小数 6 桁へ `HALF_UP` で丸めること |
| 3 | 異常系 | 必須 | `null` を拒否すること |
| 4 | 境界値 | 上下限 | -90, 90 を受け入れること |
| 5 | 異常系 | 範囲外 | 丸め後に範囲外となる値を拒否すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 範囲内値を受け入れる | `35.123456` | 生成成功 |  |
| 2 | 正常系 | 6 桁に丸める | `35.1234564`, `35.1234565` | `35.123456`, `35.123457` |  |
| 3 | 異常系 | `null` を拒否する | `null` | `required("latitude")` |  |
| 4 | 境界値 | 下限値を受け入れる | `-90` | 生成成功 |  |
| 5 | 境界値 | 上限値を受け入れる | `90` | 生成成功 |  |
| 6 | 異常系 | 範囲外を拒否する | `90.000001`, `-90.000001` | `outOfRange(...)` |  |

## 5. 実装メモ

- 固定化が必要な値: 丸め確認用 `BigDecimal`
- `@DisplayName` 方針: `Latitude.of の丸めと範囲条件を記載する`

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_createLatitude_when_valueIsWithinRange` | `Latitude.of は範囲内の値を受け入れる` |
| 2 | `should_roundLatitudeHalfUp_when_valueHasMoreThanSixDecimals` | `Latitude.of は小数 6 桁へ四捨五入する` |
| 3 | `should_throwException_when_latitudeIsNull` | `Latitude.of は null を拒否する` |
| 4 | `should_createLatitude_when_valueIsBoundary` | `Latitude.of は上下限値を受け入れる` |
| 5 | `should_throwException_when_latitudeIsOutOfRange` | `Latitude.of は範囲外の値を拒否する` |
