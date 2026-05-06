# `jp.i432kg.footprint.infrastructure.storage` パッケージ UT仕様書サマリー

## 1. 対象概要

- 対象パッケージ: `jp.i432kg.footprint.infrastructure.storage`
- 対象クラス: `LocalStoragePathResolver`, `S3ObjectResolver`
- 個別仕様書: 各クラスごとに `ファイル名_test_design.md` を作成

## 2. 横断観点

| No. | 観点 | 内容 |
|---|---|---|
| 1 | storage type 判定 | 対象ストレージ種別でない `StorageObject` を受け取った場合に `IllegalArgumentException` を送出すること |
| 2 | 解決結果 | `StorageObject` と設定値から、永続化先の path / bucket / key を正しく導出すること |
| 3 | 設定値利用 | constructor で受け取る base dir や `S3StorageProperties` の値をそのまま利用すること |

## 3. グルーピング方針

- ローカルストレージ解決: `LocalStoragePathResolver`
  - base dir と object key から正規化済み `Path` を返すことを確認する
- S3 オブジェクト解決: `S3ObjectResolver`
  - bucket 名と key を返すこと、`StorageObject` の種別ガードを確認する

## 4. テスト実装メモ

- `LocalStoragePathResolver` はモック不要でインスタンス生成し、`Path` の正規化結果を確認する
- `S3ObjectResolver` は `S3StorageProperties` をモック化する
- `StorageObject` は `DomainTestFixtures.objectKey()` と `StorageObject.local(...)` / `StorageObject.s3(...)` を利用する
- `IllegalArgumentException` のメッセージも確認して、誤った storage type であることが分かるようにする
