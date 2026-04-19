# `ByteTypeHandler` テスト仕様書

## 1. 基本情報

- 対象クラス: `ByteTypeHandler`
- 対象メソッド: `setNonNullParameter(PreparedStatement, int, Byte, JdbcType)`, `getNullableResult(ResultSet, String)`, `getNullableResult(ResultSet, int)`, `getNullableResult(CallableStatement, int)`
- 対象パッケージ: `jp.i432kg.footprint.infrastructure.datasource.typehandler`
- 対応するテストクラス: `ByteTypeHandlerTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をする処理か: `Byte` と DB の `long` 値を相互変換する
- 入力: `Byte`, `ResultSet`, `CallableStatement`
- 出力: `void`, `Byte`
- 主な副作用: `PreparedStatement#setLong` 呼び出し

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 書き込み変換 | `setLong` に `Byte#getValue()` を渡すこと |
| 2 | 正常系 | 読み込み変換 | `long` 値から `Byte.of(...)` を復元すること |
| 3 | 境界値 | wasNull 判定 | `wasNull()` が `true` の場合に `null` を返すこと |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | バイト数を long として設定する | `Byte.of(1024L)` | `ps.setLong(index, 1024L)` が呼ばれる |  |
| 2 | 正常系 | DB 数値からバイト数を復元する | `getLong(...)=1024`, `wasNull()=false` | `Byte.of(1024)` を返す | 取得経路ごとに確認 |
| 3 | 境界値 | `wasNull=true` の場合に null を返す | `getLong(...)=0`, `wasNull()=true` | `null` を返す | `ResultSet` / `CallableStatement` で確認 |

## 5. 実装メモ

- モック化する依存: `PreparedStatement`, `ResultSet`, `CallableStatement`
- 固定化が必要な値: `1024L`
- `@DisplayName` 方針: `wasNull` 条件を明示する
- 備考: 数値系 handler では `wasNull()` を必ず検証する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_setLongValue_when_settingNonNullParameter` | `ByteTypeHandler.setNonNullParameter はバイト数を long として設定する` |
| 2 | `should_returnByteValue_when_gettingNullableResult` | `ByteTypeHandler.getNullableResult は DB 数値から Byte を復元する` |
| 3 | `should_returnNull_when_wasNullIsTrue` | `ByteTypeHandler.getNullableResult は wasNull が true の場合に null を返す` |
