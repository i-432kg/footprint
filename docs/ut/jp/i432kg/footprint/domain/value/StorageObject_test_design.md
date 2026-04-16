# `StorageObject` テスト仕様書

## 1. 基本情報

- 対象クラス: `StorageObject`
- 対象メソッド: `of(StorageType, ObjectKey)`, `local(ObjectKey)`, `s3(ObjectKey)`, `isLocal()`, `isS3()`
- 対象パッケージ: `jp.i432kg.footprint.domain.value`
- 対応するテストクラス: `StorageObjectTest`
- 作成者: Codex
- 作成日: 2026-04-15

## 2. 対象概要

- 何をする処理か: 保存先種別とオブジェクトキーの組み合わせを表す値オブジェクトを生成する
- 入力: `StorageType`, `ObjectKey`
- 出力: `StorageObject`, 判定結果
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 直接生成 | 妥当な組み合わせで生成できること |
| 2 | 異常系 | 必須 | `storageType`、`objectKey` の各 `null` を拒否すること |
| 3 | 正常系 | 補助ファクトリ | `local`, `s3` が適切な種別で生成すること |
| 4 | 正常系 | 判定メソッド | `isLocal`, `isS3` が生成種別と一致すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 直接生成する | `StorageType.LOCAL`, `ObjectKey.of("a/b.jpg")` | 生成成功 |  |
| 2 | 異常系 | `storageType` が `null` の場合を拒否する | `null`, 有効な `ObjectKey` | `required("storage_type")` |  |
| 3 | 異常系 | `objectKey` が `null` の場合を拒否する | 有効な `StorageType`, `null` | `required("object_key")` |  |
| 4 | 正常系 | `local` が LOCAL で生成する | 有効な `ObjectKey` | `storageType=LOCAL` |  |
| 5 | 正常系 | `s3` が S3 で生成する | 有効な `ObjectKey` | `storageType=S3` |  |
| 6 | 正常系 | `isLocal` / `isS3` が一致する | `local(...)`, `s3(...)` | 判定結果が期待どおり |  |

## 5. 実装メモ

- 固定化が必要な値: 妥当な `ObjectKey`
- `@DisplayName` 方針: `StorageObject の組み合わせと判定メソッドを分けて記載する`

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_createStorageObject_when_argumentsAreValid` | `StorageObject.of は妥当な storageType と objectKey の組み合わせを生成する` |
| 2 | `should_throwException_when_storageTypeIsNull` | `StorageObject.of は storageType が null の場合を拒否する` |
| 3 | `should_throwException_when_objectKeyIsNull` | `StorageObject.of は objectKey が null の場合を拒否する` |
| 4 | `should_createLocalStorageObject_when_localFactoryIsUsed` | `StorageObject.local は LOCAL の保存先として生成する` |
| 5 | `should_createS3StorageObject_when_s3FactoryIsUsed` | `StorageObject.s3 は S3 の保存先として生成する` |
| 6 | `should_returnFlagsMatchingStorageType_when_predicatesAreCalled` | `StorageObject の判定メソッドは保存先種別と一致する` |
