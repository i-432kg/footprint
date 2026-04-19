# `SearchKeywordTypeHandler` テスト仕様書

## 1. 基本情報

- 対象クラス: `SearchKeywordTypeHandler`
- 対象メソッド: `setNonNullParameter(PreparedStatement, int, SearchKeyword, JdbcType)`, `getNullableResult(ResultSet, String)`, `getNullableResult(ResultSet, int)`, `getNullableResult(CallableStatement, int)`
- 対象パッケージ: `jp.i432kg.footprint.infrastructure.datasource.typehandler`
- 対応するテストクラス: `SearchKeywordTypeHandlerTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をする処理か: `SearchKeyword` と DB の `String` 値を相互変換する
- 入力: `SearchKeyword`, `ResultSet`, `CallableStatement`
- 出力: `void`, `SearchKeyword`
- 主な副作用: `PreparedStatement#setString` 呼び出し

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 書き込み変換 | `setString` に検索キーワード文字列を渡すこと |
| 2 | 正常系 | 読み込み変換 | 各取得メソッドで `SearchKeyword.of(...)` により復元すること |
| 3 | 境界値 | null 透過 | DB 値が `null` の場合に `null` を返すこと |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 検索キーワードを文字列として設定する | `SearchKeyword.of("hello")` | `ps.setString(index, "hello")` が呼ばれる |  |
| 2 | 正常系 | DB 文字列から検索キーワードを復元する | `getString(...)=hello` | `SearchKeyword.of("hello")` を返す |  |
| 3 | 境界値 | `null` をそのまま返す | `getString(...)=null` | `null` を返す |  |

## 5. 実装メモ

- モック化する依存: `PreparedStatement`, `ResultSet`, `CallableStatement`
- 固定化が必要な値: `"hello"`
- `@DisplayName` 方針: 検索キーワード変換であることを明示する
- 備考: 文字列値オブジェクト系 handler の共通パターン

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_setStringValue_when_settingNonNullParameter` | `SearchKeywordTypeHandler.setNonNullParameter は検索キーワードを文字列として設定する` |
| 2 | `should_returnSearchKeyword_when_gettingNullableResult` | `SearchKeywordTypeHandler.getNullableResult は DB 文字列から SearchKeyword を復元する` |
| 3 | `should_returnNull_when_databaseValueIsNull` | `SearchKeywordTypeHandler.getNullableResult は DB 値が null の場合に null を返す` |
