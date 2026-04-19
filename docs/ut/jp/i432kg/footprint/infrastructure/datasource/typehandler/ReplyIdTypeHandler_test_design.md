# `ReplyIdTypeHandler` テスト仕様書

## 1. 基本情報

- 対象クラス: `ReplyIdTypeHandler`
- 対象メソッド: `setNonNullParameter(PreparedStatement, int, ReplyId, JdbcType)`, `getNullableResult(ResultSet, String)`, `getNullableResult(ResultSet, int)`, `getNullableResult(CallableStatement, int)`
- 対象パッケージ: `jp.i432kg.footprint.infrastructure.datasource.typehandler`
- 対応するテストクラス: `ReplyIdTypeHandlerTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をする処理か: `ReplyId` と DB の `String` 値を相互変換する
- 入力: `ReplyId`, `ResultSet`, `CallableStatement`
- 出力: `void`, `ReplyId`
- 主な副作用: `PreparedStatement#setString` 呼び出し

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 書き込み変換 | `setString` に `ReplyId#getValue()` を渡すこと |
| 2 | 正常系 | 読み込み変換 | 各取得メソッドで `ReplyId.of(...)` により復元すること |
| 3 | 境界値 | null 透過 | DB 値が `null` の場合に `null` を返すこと |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | `ReplyId` を文字列として設定する | `ReplyId.of(...)` | `ps.setString(index, replyId.getValue())` が呼ばれる |  |
| 2 | 正常系 | `ResultSet` / `CallableStatement` から `ReplyId` を復元する | `getString(...)=REPLY_ID` | `ReplyId.of(REPLY_ID)` を返す | 取得経路ごとに確認 |
| 3 | 境界値 | `null` をそのまま返す | `getString(...)=null` | `null` を返す |  |

## 5. 実装メモ

- モック化する依存: `PreparedStatement`, `ResultSet`, `CallableStatement`
- 固定化が必要な値: `DomainTestFixtures.REPLY_ID`
- `@DisplayName` 方針: 取得経路を含めて日本語で記載する
- 備考: `PostIdTypeHandler` と同型パターン

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_setStringValue_when_settingNonNullParameter` | `ReplyIdTypeHandler.setNonNullParameter は ReplyId の値を文字列として設定する` |
| 2 | `should_returnReplyId_when_gettingNullableResult` | `ReplyIdTypeHandler.getNullableResult は DB 文字列から ReplyId を復元する` |
| 3 | `should_returnNull_when_databaseValueIsNull` | `ReplyIdTypeHandler.getNullableResult は DB 値が null の場合に null を返す` |
