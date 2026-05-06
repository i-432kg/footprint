# `jp.i432kg.footprint.infrastructure.storage.repository` パッケージ UT仕様書サマリー

## 1. 対象概要

- 対象パッケージ: `jp.i432kg.footprint.infrastructure.storage.repository`
- 対象クラス: `LocalImageRepositoryImpl`, `S3ImageRepositoryImpl`
- 個別仕様書: 各クラスごとに `ファイル名_test_design.md` を作成

## 2. 横断観点

| No. | 観点 | 内容 |
|---|---|---|
| 1 | 保存処理 | 画像ストリームと元ファイル名から拡張子を決定し、`ImageIdGenerator` と各 resolver を用いて `StorageObject` を返すこと |
| 2 | メタデータ抽出 | 画像サイズ、位置情報、撮影日時、ファイルサイズ、拡張子、EXIF 有無を取得して `ImageMetadata` を返すこと |
| 3 | フォールバック | GPS や撮影日時が取得できない場合に既定値へフォールバックすること |
| 4 | 削除処理 | resolver で解決した保存先に対して削除処理を実行すること |
| 5 | 例外変換 | 非対応画像形式、画像解析失敗、S3 / ファイル操作失敗を公開 API の例外へ変換すること |

## 3. グルーピング方針

- ローカル保存実装: `LocalImageRepositoryImpl`
  - 一時ファイル経由の保存、ローカルファイルからのメタデータ抽出、削除、例外変換を確認する
- S3 保存実装: `S3ImageRepositoryImpl`
  - S3 put/get/head/delete への委譲、S3 オブジェクトからのメタデータ抽出、例外変換を確認する

## 4. テスト実装メモ

- モック化する依存:
  - `LocalStoragePathResolver`
  - `S3Client`
  - `S3ObjectResolver`
  - `ImageIdGenerator`
- 固定化する値:
  - `Clock.fixed(...)`
  - `ImageId`, `UserId`, `PostId`, `FileName`, `StorageObject`
  - 画像バイト列と EXIF 付き / なしのテスト画像
- ローカル実装は `@TempDir` を利用し、一時ファイル生成と移動結果を確認する
- S3 実装は AWS SDK リクエストを `ArgumentCaptor` で取得し、bucket / key / content type / content length を確認する
- 画像解析ライブラリや `ImageIO` の正常系は、実ファイルベースの fixture を優先して副作用込みで確認する
