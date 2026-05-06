# `LongitudeTypeHandler` テスト仕様書

## 1. 基本情報

- 対象クラス: `LongitudeTypeHandler`
- 対象メソッド: `setNonNullParameter(PreparedStatement, int, Longitude, JdbcType)`, `getNullableResult(ResultSet, String)`, `getNullableResult(ResultSet, int)`, `getNullableResult(CallableStatement, int)`
- 対象パッケージ: `jp.i432kg.footprint.infrastructure.datasource.typehandler`
- 対応するテストクラス: `LongitudeTypeHandlerTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をする処理か: `Longitude` と DB の `BigDecimal` 値を相互変換する
- 入力: `Longitude`, `ResultSet`, `CallableStatement`
- 出力: `void`, `Longitude`
- 主な副作用: `PreparedStatement#setBigDecimal` 呼び出し

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 書き込み変換 | `setBigDecimal` に経度値を渡すこと |
| 2 | 正常系 | 読み込み変換 | `BigDecimal` 値から `Longitude.of(...)` を復元すること |
| 3 | 境界値 | null 透過 | `getBigDecimal(...)=null` の場合に `null` を返すこと |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 経度を `BigDecimal` として設定する | `DomainTestFixtures.longitude()` | `ps.setBigDecimal(index, longitude.getValue())` が呼ばれる |  |
| 2 | 正常系 | DB 数値から経度を復元する | `getBigDecimal(...)=139.767125` | `Longitude.of(...)` を返す | 取得経路ごとに確認 |
| 3 | 境界値 | `null` をそのまま返す | `getBigDecimal(...)=null` | `null` を返す |  |

## 5. 実装メモ

- モック化する依存: `PreparedStatement`, `ResultSet`, `CallableStatement`
- 固定化が必要な値: `DomainTestFixtures.longitude().getValue()`
- `@DisplayName` 方針: 経度変換であることを明示する
- 備考: `LatitudeTypeHandler` と同型

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_setBigDecimalValue_when_settingNonNullParameter` | `LongitudeTypeHandler.setNonNullParameter は経度を BigDecimal として設定する` |
| 2 | `should_returnLongitude_when_gettingNullableResult` | `LongitudeTypeHandler.getNullableResult は DB 数値から Longitude を復元する` |
| 3 | `should_returnNull_when_databaseValueIsNull` | `LongitudeTypeHandler.getNullableResult は DB 値が null の場合に null を返す` |
