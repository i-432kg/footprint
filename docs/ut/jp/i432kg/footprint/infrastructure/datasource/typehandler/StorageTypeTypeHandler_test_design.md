# `StorageTypeTypeHandler` テスト仕様書

## 1. 基本情報

- 対象クラス: `StorageTypeTypeHandler`
- 対象メソッド: `setNonNullParameter(PreparedStatement, int, StorageType, JdbcType)`, `getNullableResult(ResultSet, String)`, `getNullableResult(ResultSet, int)`, `getNullableResult(CallableStatement, int)`
- 対象パッケージ: `jp.i432kg.footprint.infrastructure.datasource.typehandler`
- 対応するテストクラス: `StorageTypeTypeHandlerTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をする処理か: `StorageType` と DB の `String` 値を相互変換する
- 入力: `StorageType`, `ResultSet`, `CallableStatement`
- 出力: `void`, `StorageType`
- 主な副作用: `PreparedStatement#setString` 呼び出し

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 書き込み変換 | `setString` に保存種別文字列を渡すこと |
| 2 | 正常系 | 読み込み変換 | 各取得メソッドで `StorageType.of(...)` により復元すること |
| 3 | 境界値 | null 透過 | DB 値が `null` の場合に `null` を返すこと |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 保存種別を文字列として設定する | `StorageType.LOCAL` など | `ps.setString(index, storageType.getValue())` が呼ばれる |  |
| 2 | 正常系 | DB 文字列から保存種別を復元する | `getString(...)=LOCAL` または `local` | `StorageType.of(...)` を返す |  |
| 3 | 境界値 | `null` をそのまま返す | `getString(...)=null` | `null` を返す |  |

## 5. 実装メモ

- モック化する依存: `PreparedStatement`, `ResultSet`, `CallableStatement`
- 固定化が必要な値: `StorageType.LOCAL`
- `@DisplayName` 方針: 保存種別の変換であることを明示する
- 備考: 文字列 enum 系 handler の代表

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_setStringValue_when_settingNonNullParameter` | `StorageTypeTypeHandler.setNonNullParameter は保存種別を文字列として設定する` |
| 2 | `should_returnStorageType_when_gettingNullableResult` | `StorageTypeTypeHandler.getNullableResult は DB 文字列から StorageType を復元する` |
| 3 | `should_returnNull_when_databaseValueIsNull` | `StorageTypeTypeHandler.getNullableResult は DB 値が null の場合に null を返す` |
