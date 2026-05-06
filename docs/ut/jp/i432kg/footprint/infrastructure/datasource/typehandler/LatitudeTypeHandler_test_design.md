# `LatitudeTypeHandler` テスト仕様書

## 1. 基本情報

- 対象クラス: `LatitudeTypeHandler`
- 対象メソッド: `setNonNullParameter(PreparedStatement, int, Latitude, JdbcType)`, `getNullableResult(ResultSet, String)`, `getNullableResult(ResultSet, int)`, `getNullableResult(CallableStatement, int)`
- 対象パッケージ: `jp.i432kg.footprint.infrastructure.datasource.typehandler`
- 対応するテストクラス: `LatitudeTypeHandlerTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をする処理か: `Latitude` と DB の `BigDecimal` 値を相互変換する
- 入力: `Latitude`, `ResultSet`, `CallableStatement`
- 出力: `void`, `Latitude`
- 主な副作用: `PreparedStatement#setBigDecimal` 呼び出し

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 書き込み変換 | `setBigDecimal` に緯度値を渡すこと |
| 2 | 正常系 | 読み込み変換 | `BigDecimal` 値から `Latitude.of(...)` を復元すること |
| 3 | 境界値 | null 透過 | `getBigDecimal(...)=null` の場合に `null` を返すこと |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 緯度を `BigDecimal` として設定する | `DomainTestFixtures.latitude()` | `ps.setBigDecimal(index, latitude.getValue())` が呼ばれる |  |
| 2 | 正常系 | DB 数値から緯度を復元する | `getBigDecimal(...)=35.681236` | `Latitude.of(...)` を返す | 取得経路ごとに確認 |
| 3 | 境界値 | `null` をそのまま返す | `getBigDecimal(...)=null` | `null` を返す |  |

## 5. 実装メモ

- モック化する依存: `PreparedStatement`, `ResultSet`, `CallableStatement`
- 固定化が必要な値: `DomainTestFixtures.latitude().getValue()`
- `@DisplayName` 方針: 緯度変換であることを明示する
- 備考: `LongitudeTypeHandler` と同型

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_setBigDecimalValue_when_settingNonNullParameter` | `LatitudeTypeHandler.setNonNullParameter は緯度を BigDecimal として設定する` |
| 2 | `should_returnLatitude_when_gettingNullableResult` | `LatitudeTypeHandler.getNullableResult は DB 数値から Latitude を復元する` |
| 3 | `should_returnNull_when_databaseValueIsNull` | `LatitudeTypeHandler.getNullableResult は DB 値が null の場合に null を返す` |
