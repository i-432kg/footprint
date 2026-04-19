# `BirthDateTypeHandler` テスト仕様書

## 1. 基本情報

- 対象クラス: `BirthDateTypeHandler`
- 対象メソッド: `setNonNullParameter(PreparedStatement, int, BirthDate, JdbcType)`, `getNullableResult(ResultSet, String)`, `getNullableResult(ResultSet, int)`, `getNullableResult(CallableStatement, int)`
- 対象パッケージ: `jp.i432kg.footprint.infrastructure.datasource.typehandler`
- 対応するテストクラス: `BirthDateTypeHandlerTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をする処理か: `BirthDate` と DB の `LocalDate` 値を相互変換する
- 入力: `BirthDate`, `ResultSet`, `CallableStatement`
- 出力: `void`, `BirthDate`
- 主な副作用: `PreparedStatement#setObject` 呼び出し

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 書き込み変換 | `setObject` に `BirthDate#getValue()` を渡すこと |
| 2 | 正常系 | 読み込み変換 | `LocalDate` 値から `BirthDate.restore(...)` を復元すること |
| 3 | 境界値 | null 透過 | `getObject(..., LocalDate.class)=null` の場合に `null` を返すこと |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 生年月日を `LocalDate` として設定する | `BirthDate.restore(LocalDate.of(...))` | `ps.setObject(index, birthDate.getValue())` が呼ばれる |  |
| 2 | 正常系 | DB 日付から生年月日を復元する | `getObject(..., LocalDate.class)=2000-01-01` | `BirthDate.restore(...)` を返す | 取得経路ごとに確認 |
| 3 | 境界値 | `null` をそのまま返す | `getObject(..., LocalDate.class)=null` | `null` を返す |  |

## 5. 実装メモ

- モック化する依存: `PreparedStatement`, `ResultSet`, `CallableStatement`
- 固定化が必要な値: `LocalDate.of(2000, 1, 1)`
- `@DisplayName` 方針: `LocalDate` 変換と復元を明示する
- 備考: `BirthDate.of(...)` ではなく `restore(...)` を利用する点を確認する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_setLocalDateValue_when_settingNonNullParameter` | `BirthDateTypeHandler.setNonNullParameter は生年月日を LocalDate として設定する` |
| 2 | `should_returnBirthDate_when_gettingNullableResult` | `BirthDateTypeHandler.getNullableResult は DB 日付から BirthDate を復元する` |
| 3 | `should_returnNull_when_databaseValueIsNull` | `BirthDateTypeHandler.getNullableResult は DB 値が null の場合に null を返す` |
