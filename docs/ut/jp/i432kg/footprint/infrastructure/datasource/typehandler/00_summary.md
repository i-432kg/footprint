# `jp.i432kg.footprint.infrastructure.datasource.typehandler` パッケージ UT仕様書サマリー

## 1. 対象概要

- 対象パッケージ: `jp.i432kg.footprint.infrastructure.datasource.typehandler`
- 対象クラス:
  - `BirthDateTypeHandler`
  - `ByteTypeHandler`
  - `EmailAddressTypeHandler`
  - `FileExtensionTypeHandler`
  - `FileNameTypeHandler`
  - `HashedPasswordTypeHandler`
  - `LatitudeTypeHandler`
  - `LongitudeTypeHandler`
  - `ObjectKeyTypeHandler`
  - `PixelTypeHandler`
  - `PostCommentTypeHandler`
  - `PostIdTypeHandler`
  - `ReplyCommentTypeHandler`
  - `ReplyIdTypeHandler`
  - `SearchKeywordTypeHandler`
  - `StorageTypeTypeHandler`
  - `UserIdTypeHandler`
  - `UserNameTypeHandler`
- 個別仕様書: 各クラスごとに `ファイル名_test_design.md` を作成

## 2. 横断観点

| No. | 観点 | 内容 |
|---|---|---|
| 1 | 書き込み変換 | `setNonNullParameter` が値オブジェクトの内部値を `PreparedStatement` へ正しい JDBC 型で設定すること |
| 2 | 列名取得 | `getNullableResult(ResultSet, String)` が DB 値を値オブジェクトへ復元すること |
| 3 | 列番号取得 | `getNullableResult(ResultSet, int)` が DB 値を値オブジェクトへ復元すること |
| 4 | CallableStatement 取得 | `getNullableResult(CallableStatement, int)` が DB 値を値オブジェクトへ復元すること |
| 5 | null 透過 | DB 値が `null` または `wasNull()` の場合に `null` を返すこと |
| 6 | 値オブジェクト復元 | `of(...)` / `restore(...)` を用いて domain ルールに従って復元すること |

## 3. グルーピング方針

- 文字列変換系:
  - `EmailAddressTypeHandler`, `FileExtensionTypeHandler`, `FileNameTypeHandler`, `HashedPasswordTypeHandler`, `ObjectKeyTypeHandler`, `PostCommentTypeHandler`, `PostIdTypeHandler`, `ReplyCommentTypeHandler`, `ReplyIdTypeHandler`, `SearchKeywordTypeHandler`, `StorageTypeTypeHandler`, `UserIdTypeHandler`, `UserNameTypeHandler`
  - `setString` / `getString` と値オブジェクト復元を確認する
- 数値変換系:
  - `ByteTypeHandler`, `PixelTypeHandler`, `LatitudeTypeHandler`, `LongitudeTypeHandler`
  - `setLong` / `setInt` / `setBigDecimal` と `wasNull()` も含めて確認する
- 日付変換系:
  - `BirthDateTypeHandler`
  - `LocalDate` への変換と `BirthDate.restore(...)` を確認する

## 4. テスト実装メモ

- `PreparedStatement`, `ResultSet`, `CallableStatement` は Mockito でモック化する
- 値オブジェクトは `DomainTestFixtures` または `of(...)` / `restore(...)` で生成する
- `wasNull()` 判定を持つ handler は `getXxx()` の戻り値と `wasNull()` の組み合わせを明示的に検証する
- `BirthDateTypeHandler` は `getObject(..., LocalDate.class)` / `setObject(...)` の呼び出しを確認する
- 文字列変換系は空文字や不正値の検証までは handler の責務外とし、正常値変換と `null` 透過を優先する
