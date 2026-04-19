# `S3ObjectResolver` テスト仕様書

## 1. 基本情報

- 対象クラス: `S3ObjectResolver`
- 対象メソッド: `resolveBucket(StorageObject)`, `resolveKey(StorageObject)`
- 対象パッケージ: `jp.i432kg.footprint.infrastructure.storage`
- 対応するテストクラス: `S3ObjectResolverTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をする処理か: S3 用 `StorageObject` から bucket 名と object key を解決する
- 入力: `StorageObject`
- 出力: `String`
- 主な副作用: `S3StorageProperties#getBucketName()` 呼び出し

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | bucket 解決 | `resolveBucket` が設定済み bucket 名を返すこと |
| 2 | 正常系 | key 解決 | `resolveKey` が `storageObject.getObjectKey().getValue()` を返すこと |
| 3 | 異常系 | storage type ガード | `S3` 以外の `StorageObject` では `IllegalArgumentException` を送出すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | bucket 名を解決する | `StorageObject.s3(objectKey)`, `properties.bucketName=my-bucket` | `my-bucket` を返す |  |
| 2 | 正常系 | object key を解決する | `StorageObject.s3(objectKey)` | object key 文字列を返す |  |
| 3 | 異常系 | ローカルストレージオブジェクトを拒否する | `StorageObject.local(objectKey)` | `IllegalArgumentException("storageObject is not S3.")` | `resolveBucket`, `resolveKey` の双方で確認 |

## 5. 実装メモ

- モック化する依存: `S3StorageProperties`
- 固定化が必要な値: bucket 名, `DomainTestFixtures.objectKey()`
- `@DisplayName` 方針: bucket 解決 / key 解決 / storage type ガードを日本語で記載する
- 備考: `resolveBucket` と `resolveKey` は別メソッドなので正常系・異常系をそれぞれ確認する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_returnBucketName_when_storageObjectIsS3` | `S3ObjectResolver.resolveBucket は S3 オブジェクトの bucket 名を返す` |
| 2 | `should_returnObjectKey_when_storageObjectIsS3` | `S3ObjectResolver.resolveKey は S3 オブジェクトの key を返す` |
| 3 | `should_throwException_when_storageObjectIsNotS3` | `S3ObjectResolver は S3 でない StorageObject を拒否する` |
