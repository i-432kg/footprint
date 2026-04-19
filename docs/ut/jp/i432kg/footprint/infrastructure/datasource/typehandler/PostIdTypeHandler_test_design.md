# `PostIdTypeHandler` テスト仕様書

## 1. 基本情報

- 対象クラス: `PostIdTypeHandler`
- 対象メソッド: `setNonNullParameter(PreparedStatement, int, PostId, JdbcType)`, `getNullableResult(ResultSet, String)`, `getNullableResult(ResultSet, int)`, `getNullableResult(CallableStatement, int)`
- 対象パッケージ: `jp.i432kg.footprint.infrastructure.datasource.typehandler`
- 対応するテストクラス: `PostIdTypeHandlerTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をする処理か: `PostId` と DB の `String` 値を相互変換する
- 入力: `PostId`, `ResultSet`, `CallableStatement`
- 出力: `void`, `PostId`
- 主な副作用: `PreparedStatement#setString` 呼び出し

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 書き込み変換 | `setString` に `PostId#getValue()` を渡すこと |
| 2 | 正常系 | 列名取得 | `String` 値から `PostId` を復元すること |
| 3 | 正常系 | 列番号取得 | `String` 値から `PostId` を復元すること |
| 4 | 正常系 | CallableStatement 取得 | `String` 値から `PostId` を復元すること |
| 5 | 境界値 | null 透過 | DB 値が `null` の場合に `null` を返すこと |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | `PostId` を文字列として設定する | `PostId.of(...)` | `ps.setString(index, postId.getValue())` が呼ばれる |  |
| 2 | 正常系 | 列名指定で `PostId` を復元する | `rs.getString(columnName)=POST_ID` | `PostId.of(POST_ID)` を返す |  |
| 3 | 正常系 | 列番号指定で `PostId` を復元する | `rs.getString(columnIndex)=POST_ID` | `PostId.of(POST_ID)` を返す |  |
| 4 | 正常系 | `CallableStatement` から `PostId` を復元する | `cs.getString(columnIndex)=POST_ID` | `PostId.of(POST_ID)` を返す |  |
| 5 | 境界値 | `null` をそのまま返す | `getString(...)=null` | `null` を返す | 各取得メソッド代表確認 |

## 5. 実装メモ

- モック化する依存: `PreparedStatement`, `ResultSet`, `CallableStatement`
- 固定化が必要な値: `DomainTestFixtures.POST_ID`
- `@DisplayName` 方針: 変換方向と取得方法を日本語で明記する
- 備考: 文字列変換系 handler の代表として同一パターンで実装する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_setStringValue_when_settingNonNullParameter` | `PostIdTypeHandler.setNonNullParameter は PostId の値を文字列として設定する` |
| 2 | `should_returnPostId_when_gettingNullableResultByColumnName` | `PostIdTypeHandler.getNullableResult は列名指定で PostId を復元する` |
| 3 | `should_returnPostId_when_gettingNullableResultByColumnIndex` | `PostIdTypeHandler.getNullableResult は列番号指定で PostId を復元する` |
| 4 | `should_returnPostId_when_gettingNullableResultFromCallableStatement` | `PostIdTypeHandler.getNullableResult は CallableStatement から PostId を復元する` |
| 5 | `should_returnNull_when_databaseValueIsNull` | `PostIdTypeHandler.getNullableResult は DB 値が null の場合に null を返す` |
