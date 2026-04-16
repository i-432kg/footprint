# `jp.i432kg.footprint.domain.value` パッケージ UT仕様書サマリー

## 1. 対象概要

- 対象パッケージ: `jp.i432kg.footprint.domain.value`
- 対象クラス: `BirthDate`, `Byte`, `EmailAddress`, `FileExtension`, `FileName`, `HashedPassword`, `ImageId`, `Latitude`, `Longitude`, `ObjectKey`, `Pixel`, `PostComment`, `PostId`, `RawPassword`, `ReplyComment`, `ReplyId`, `SearchKeyword`, `StorageObject`, `StorageType`, `UserId`, `UserName`
- 個別仕様書: 各クラスごとに `ファイル名_test_design.md` を作成

## 2. 横断観点

| No. | 観点 | 内容 |
|---|---|---|
| 1 | 必須チェック | `null`、blank、空白のみ入力の扱いを各値オブジェクトで確認する |
| 2 | 境界値 | 最小値、最大値、直前値、直後値を確認する |
| 3 | 正規化 | `strip()`、小文字化、先頭ドット除去、座標丸めなどの正規化結果を確認する |
| 4 | 形式検証 | 正規表現、許可文字、列挙値、ULID 形式などの妥当性を確認する |
| 5 | 補助メソッド | `withDot()`, `from(...)`, `local()`, `s3()`, `isLocal()`, `isS3()` の補助振る舞いを確認する |
| 6 | セキュリティ | `ObjectKey`, `FileName`, `RawPassword` で危険入力や情報露出防止を確認する |
| 7 | 実装差異 | `PostComment` と `ReplyComment` の blank 判定差を明示する |

## 3. グルーピング方針

- ID 系: `ImageId`, `PostId`, `ReplyId`, `UserId`
  - `UlidValidation` による `null`、blank、形式不正、妥当な ULID の受け入れを共通観点で確認する
- 座標系: `Latitude`, `Longitude`
  - `CoordinateValidation` による `null`、丸め、範囲境界、範囲外を共通観点で確認する
- 文字列系: `EmailAddress`, `FileExtension`, `FileName`, `PostComment`, `ReplyComment`, `RawPassword`, `SearchKeyword`, `UserName`
  - 長さ、許可文字、制御文字、正規化の有無を確認する
- 数値系: `Byte`, `Pixel`, `Latitude`, `Longitude`
  - `Pixel` は汎用化し、0 未満のみを拒否する。画像表示品質に関わる制約は `Image` モデル側で確認する
- ストレージ系: `ObjectKey`, `StorageObject`, `StorageType`
  - パス安全性、保存先組み合わせ、列挙解釈を確認する

## 4. テスト実装メモ

- `BirthDate` は `LocalDate.now()` 依存があるため、テストデータ作成時に当日基準を明示する
- `HashedPassword.from(...)` は `Hasher` をスタブ化して検証する
- `RawPassword.toString()` は平文露出防止を必ず確認する
- `StorageType.of(null)` は他の値オブジェクトと同様に `InvalidValueException.required("storage_type")` を返す前提で実装・テストする
- 各テストメソッドには `@DisplayName` を付与し、日本語の見出しで観点を明示する
