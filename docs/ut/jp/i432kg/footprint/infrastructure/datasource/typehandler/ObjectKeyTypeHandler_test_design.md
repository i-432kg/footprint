# `ObjectKeyTypeHandler` テスト仕様書

## 1. 基本情報

- 対象クラス: `ObjectKeyTypeHandler`
- 対象メソッド: `setNonNullParameter(PreparedStatement, int, ObjectKey, JdbcType)`, `getNullableResult(ResultSet, String)`, `getNullableResult(ResultSet, int)`, `getNullableResult(CallableStatement, int)`
- 対象パッケージ: `jp.i432kg.footprint.infrastructure.datasource.typehandler`
- 対応するテストクラス: `ObjectKeyTypeHandlerTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をする処理か: `ObjectKey` と DB の `String` 値を相互変換する
- 入力: `ObjectKey`, `ResultSet`, `CallableStatement`
- 出力: `void`, `ObjectKey`
- 主な副作用: `PreparedStatement#setString` 呼び出し

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 書き込み変換 | `setString` にオブジェクトキー文字列を渡すこと |
| 2 | 正常系 | 読み込み変換 | 各取得メソッドで `ObjectKey.of(...)` により復元すること |
| 3 | 境界値 | null 透過 | DB 値が `null` の場合に `null` を返すこと |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | オブジェクトキーを文字列として設定する | `DomainTestFixtures.objectKey()` | `ps.setString(index, objectKey.getValue())` が呼ばれる |  |
| 2 | 正常系 | DB 文字列からオブジェクトキーを復元する | `getString(...)=users/...` | `ObjectKey.of(...)` を返す |  |
| 3 | 境界値 | `null` をそのまま返す | `getString(...)=null` | `null` を返す |  |

## 5. 実装メモ

- モック化する依存: `PreparedStatement`, `ResultSet`, `CallableStatement`
- 固定化が必要な値: `DomainTestFixtures.objectKey().getValue()`
- `@DisplayName` 方針: object key の変換であることを明示する
- 備考: 文字列値オブジェクト系 handler の共通パターン

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_setStringValue_when_settingNonNullParameter` | `ObjectKeyTypeHandler.setNonNullParameter は object key を文字列として設定する` |
| 2 | `should_returnObjectKey_when_gettingNullableResult` | `ObjectKeyTypeHandler.getNullableResult は DB 文字列から ObjectKey を復元する` |
| 3 | `should_returnNull_when_databaseValueIsNull` | `ObjectKeyTypeHandler.getNullableResult は DB 値が null の場合に null を返す` |
