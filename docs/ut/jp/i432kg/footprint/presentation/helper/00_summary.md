# `jp.i432kg.footprint.presentation.helper` パッケージ UT仕様書サマリー

## 1. 対象概要

- 対象パッケージ: `jp.i432kg.footprint.presentation.helper`
- 対象クラス: `ImageUrlResolver`, `LocalImageUrlResolver`, `S3ImageUrlResolver`
- 個別仕様書: 各クラスごとに `ファイル名_test_design.md` を作成

## 2. 横断観点

| No. | 観点 | 内容 |
|---|---|---|
| 1 | URL 解決 | `StorageObject` からブラウザ表示用 URL を正しく解決すること |
| 2 | 保存種別検証 | resolver が担当外の `StorageType` を受けた場合に `IllegalArgumentException` を送出すること |
| 3 | `@NullMarked` 契約 | `null` 吸収は行わず、non-null 前提の入力で動作すること |
| 4 | LOCAL/S3 差異 | LOCAL はベース URL 連結、S3 は presigned URL 発行という実装差異を確認すること |

## 3. グルーピング方針

- interface 契約: `ImageUrlResolver`
  - 実装共通の責務をサマリとして扱う
- LOCAL resolver: `LocalImageUrlResolver`
  - ベース URL と object key の連結、LOCAL 以外拒否を確認する
- S3 resolver: `S3ImageUrlResolver`
  - bucket/key 解決、presign request 組み立て、S3 以外拒否を確認する

## 4. テスト実装メモ

- `ImageUrlResolver` 自体は interface なので、実装共通の観点整理が主で個別テストは持たない
- `LocalImageUrlResolver` は `imageBaseUrl` をテストから設定して URL を検証する
- `S3ImageUrlResolver` は `S3ObjectResolver`, `S3StorageProperties`, `S3Presigner` をモックし、presign request の中身まで確認する
