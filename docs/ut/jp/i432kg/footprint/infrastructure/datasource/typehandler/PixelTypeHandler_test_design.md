# `PixelTypeHandler` テスト仕様書

## 1. 基本情報

- 対象クラス: `PixelTypeHandler`
- 対象メソッド: `setNonNullParameter(PreparedStatement, int, Pixel, JdbcType)`, `getNullableResult(ResultSet, String)`, `getNullableResult(ResultSet, int)`, `getNullableResult(CallableStatement, int)`
- 対象パッケージ: `jp.i432kg.footprint.infrastructure.datasource.typehandler`
- 対応するテストクラス: `PixelTypeHandlerTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をする処理か: `Pixel` と DB の `int` 値を相互変換する
- 入力: `Pixel`, `ResultSet`, `CallableStatement`
- 出力: `void`, `Pixel`
- 主な副作用: `PreparedStatement#setInt` 呼び出し

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 書き込み変換 | `setInt` に `Pixel#getValue()` を渡すこと |
| 2 | 正常系 | 読み込み変換 | `int` 値から `Pixel.of(...)` を復元すること |
| 3 | 境界値 | wasNull 判定 | `wasNull()` が `true` の場合に `null` を返すこと |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | ピクセル数を int として設定する | `Pixel.of(640)` | `ps.setInt(index, 640)` が呼ばれる |  |
| 2 | 正常系 | DB 数値からピクセル数を復元する | `getInt(...)=640`, `wasNull()=false` | `Pixel.of(640)` を返す | 取得経路ごとに確認 |
| 3 | 境界値 | `wasNull=true` の場合に null を返す | `getInt(...)=0`, `wasNull()=true` | `null` を返す |  |

## 5. 実装メモ

- モック化する依存: `PreparedStatement`, `ResultSet`, `CallableStatement`
- 固定化が必要な値: `640`
- `@DisplayName` 方針: ピクセル変換と `wasNull` 判定を明示する
- 備考: 数値系 handler の共通パターン

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_setIntValue_when_settingNonNullParameter` | `PixelTypeHandler.setNonNullParameter はピクセル数を int として設定する` |
| 2 | `should_returnPixel_when_gettingNullableResult` | `PixelTypeHandler.getNullableResult は DB 数値から Pixel を復元する` |
| 3 | `should_returnNull_when_wasNullIsTrue` | `PixelTypeHandler.getNullableResult は wasNull が true の場合に null を返す` |
