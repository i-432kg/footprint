# `LocalStoragePathResolver` テスト仕様書

## 1. 基本情報

- 対象クラス: `LocalStoragePathResolver`
- 対象メソッド: `resolve(StorageObject)`
- 対象パッケージ: `jp.i432kg.footprint.infrastructure.storage`
- 対応するテストクラス: `LocalStoragePathResolverTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をする処理か: ローカルストレージ用の base dir と `StorageObject` の object key から、正規化済みの保存先 `Path` を解決する
- 入力: `StorageObject`
- 出力: `Path`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | パス解決 | `baseDir.resolve(objectKey).normalize()` の結果を返すこと |
| 2 | 境界値 | 正規化 | `.` や `..` を含む path でも `normalize()` 結果を返すこと |
| 3 | 異常系 | storage type ガード | `LOCAL` 以外の `StorageObject` では `IllegalArgumentException` を送出すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | ローカルストレージパスを解決する | base dir=`storage/local`, `StorageObject.local(objectKey)` | `storage/local/<objectKey>` の正規化済み `Path` を返す |  |
| 2 | 境界値 | 正規化済みパスを返す | base dir に `.` / `..` を含める | `normalize()` 済み `Path` を返す |  |
| 3 | 異常系 | S3 オブジェクトを拒否する | `StorageObject.s3(objectKey)` | `IllegalArgumentException("storageObject is not LOCAL.")` |  |

## 5. 実装メモ

- モック化する依存: なし
- 固定化が必要な値: base dir 文字列, `DomainTestFixtures.objectKey()`
- `@DisplayName` 方針: path 解決 / 正規化 / storage type ガードを日本語で記載する
- 備考: `Path` 比較は `Paths.get(...).normalize()` 同士で行う

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_resolveLocalStoragePath_when_storageObjectIsLocal` | `LocalStoragePathResolver.resolve はローカルストレージの保存先パスを解決する` |
| 2 | `should_returnNormalizedPath_when_baseDirContainsRelativeSegments` | `LocalStoragePathResolver.resolve は相対セグメントを含む base dir を正規化して返す` |
| 3 | `should_throwException_when_storageObjectIsNotLocal` | `LocalStoragePathResolver.resolve は LOCAL でない StorageObject を拒否する` |
