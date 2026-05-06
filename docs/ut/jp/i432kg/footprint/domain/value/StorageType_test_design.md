# `StorageType` テスト仕様書

## 1. 基本情報

- 対象クラス: `StorageType`
- 対象メソッド: `of(String)`
- 対象パッケージ: `jp.i432kg.footprint.domain.value`
- 対応するテストクラス: `StorageTypeTest`
- 作成者: Codex
- 作成日: 2026-04-15

## 2. 対象概要

- 何をする処理か: 文字列から保存先種別 enum を解釈する
- 入力: `String`
- 出力: `StorageType`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 列挙解釈 | `LOCAL`, `local`, `S3`, `s3` を受け入れること |
| 2 | 異常系 | 必須 | `null` を拒否すること |
| 3 | 異常系 | 未知値 | 列挙外文字列を拒否すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | `LOCAL` を解釈する | `"LOCAL"` | `StorageType.LOCAL` |  |
| 2 | 正常系 | 小文字 `local` を解釈する | `"local"` | `StorageType.LOCAL` |  |
| 3 | 正常系 | `S3` を解釈する | `"S3"` | `StorageType.S3` |  |
| 4 | 正常系 | 小文字 `s3` を解釈する | `"s3"` | `StorageType.S3` |  |
| 5 | 異常系 | `null` を拒否する | `null` | `InvalidValueException.required("storage_type")` |  |
| 6 | 異常系 | 未知値を拒否する | `"gcs"` | `InvalidValueException.invalid(...)` |  |

## 5. 実装メモ

- `@DisplayName` 方針: `StorageType.of の解釈結果と拒否条件を記載する`

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_returnLocal_when_valueIsLocalIgnoringCase` | `StorageType.of は LOCAL を大文字小文字を区別せず解釈する` |
| 2 | `should_returnS3_when_valueIsS3IgnoringCase` | `StorageType.of は S3 を大文字小文字を区別せず解釈する` |
| 3 | `should_throwException_when_storageTypeIsNull` | `StorageType.of は null を拒否する` |
| 4 | `should_throwException_when_storageTypeIsUnknown` | `StorageType.of は未知の値を拒否する` |
