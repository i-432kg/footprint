# `S3ImageRepositoryImpl` テスト仕様書

## 1. 基本情報

- 対象クラス: `S3ImageRepositoryImpl`
- 対象メソッド: `store(InputStream, FileName, UserId, PostId)`, `extract(StorageObject)`, `delete(StorageObject)`
- 対象パッケージ: `jp.i432kg.footprint.infrastructure.storage.repository`
- 対応するテストクラス: `S3ImageRepositoryImplTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をする処理か: 画像を S3 へアップロードし、S3 オブジェクトから EXIF と画像サイズを抽出し、不要画像を削除する
- 入力: `InputStream`, `FileName`, `UserId`, `PostId`, `StorageObject`
- 出力: `StorageObject`, `ImageMetadata`, `void`
- 主な副作用: `S3Client` の `putObject` / `headObject` / `getObject` / `deleteObject` 呼び出し、`S3ObjectResolver` / `ImageIdGenerator` 呼び出し

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 画像アップロード | 判定した拡張子と生成した `ImageId` から `StorageObject.s3(...)` を返し、bucket / key / content type / content length を指定して `putObject` を呼ぶこと |
| 2 | 正常系 | content type 解決 | 拡張子に応じた content type を `PutObjectRequest` に設定すること |
| 3 | 正常系 | メタデータ抽出 | `headObject` のサイズと `getObject` の画像内容から `ImageMetadata` を組み立てること |
| 4 | 正常系 | メタデータ既定値 | GPS や撮影日時が無い場合に `Location.unknown()` と `LocalDateTime.now(clock)` を使うこと |
| 5 | 正常系 | 削除 | resolver が返す bucket / key に対して `deleteObject` を呼ぶこと |
| 6 | 異常系 | 非対応画像形式 | 許可されない拡張子しか導けない場合に `IOException("サポートされていない画像形式です。")` を送出すること |
| 7 | 異常系 | アップロード失敗 | `putObject` の `S3Exception` を `IOException("S3への画像アップロードに失敗しました。")` へ変換すること |
| 8 | 異常系 | オブジェクト未存在 | `NoSuchKeyException` を `IOException("S3上の画像ファイルが見つかりません: ...")` へ変換すること |
| 9 | 異常系 | S3 参照失敗 | `headObject` / `getObject` / `deleteObject` の `S3Exception` を `IOException` へ変換すること |
| 10 | 異常系 | メタデータ解析失敗 | オブジェクトキー拡張子不正や画像解析失敗時の `IllegalArgumentException` を `ImageProcessingException` へ変換すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | JPEG 画像を S3 へアップロードする | JPEG バイト列, 固定 `ImageId`, resolver が bucket / key を返す | `StorageObject.s3(objectKey)` を返し、`putObject` に正しい bucket / key / `image/jpeg` / byte 長を渡す | `ArgumentCaptor` 使用 |
| 2 | 正常系 | 判定不能でも元ファイル名拡張子でアップロードする | 判定不能バイト列, `originalFilename=sample.webp` | `webp` 拡張子の object key と `image/webp` で `putObject` を呼ぶ | フォールバック確認 |
| 3 | 正常系 | EXIF 付き S3 画像からメタデータを抽出する | `headObject.contentLength=123L`, `getObject` が GPS / 撮影日時付き画像を返す | `ImageMetadata` に幅・高さ・位置情報・撮影日時・拡張子・EXIF 有無・ファイルサイズが設定される | 実画像 fixture 推奨 |
| 4 | 正常系 | GPS と撮影日時が無い画像で既定値を使う | EXIF なし画像, 固定 `Clock`, `contentLength=null` | `location=Location.unknown()`, `takenAt=LocalDateTime.now(clock)`, `fileSize=imageBytes.length` |  |
| 5 | 正常系 | S3 画像を削除する | resolver が bucket / key を返す | `deleteObject` が 1 回呼ばれる |  |
| 6 | 異常系 | 非対応拡張子でアップロードに失敗する | 判定不能バイト列, `originalFilename=sample.txt` | `IOException("サポートされていない画像形式です。")` | 原因例外は `IllegalArgumentException` |
| 7 | 異常系 | アップロード失敗を IOException に変換する | `putObject(...)` が `S3Exception` | `IOException("S3への画像アップロードに失敗しました。")` |  |
| 8 | 異常系 | S3 オブジェクト未存在を IOException に変換する | `headObject` または `getObject` が `NoSuchKeyException` | `IOException("S3上の画像ファイルが見つかりません: ...")` |  |
| 9 | 異常系 | S3 参照失敗を IOException に変換する | `headObject` / `getObject` / `deleteObject` が `S3Exception` | 対応する `IOException` を送出する | 操作別メッセージを確認 |
| 10 | 異常系 | オブジェクトキー拡張子不正で解析失敗する | resolver が拡張子なし key を返す | `ImageProcessingException("画像メタデータの解析に失敗しました: ...")` | `IllegalArgumentException` 変換確認 |

## 5. 実装メモ

- モック化する依存: `S3Client`, `S3ObjectResolver`, `ImageIdGenerator`
- 固定化が必要な値: `Clock.fixed(...)`, `ImageId`, `UserId`, `PostId`, `FileName`, `StorageObject`
- `@DisplayName` 方針: アップロード / 解析 / 削除 / 例外変換が分かる日本語にする
- 備考:
  - `putObject` の `PutObjectRequest` は captor で検証する
  - `getObject` は `ResponseInputStream<GetObjectResponse>` を返すように fixture バイト列を包む
  - `extract` は resolver が返す key 末尾の拡張子と戻り値 `ImageMetadata.fileExtension` の整合も確認する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_uploadJpegImageToS3_when_validImageProvided` | `S3ImageRepositoryImpl.store は JPEG 画像を S3 にアップロードして StorageObject を返す` |
| 2 | `should_fallbackToOriginalExtension_when_fileTypeCannotBeDetected` | `S3ImageRepositoryImpl.store は画像形式判定不能時に元ファイル名の拡張子へフォールバックする` |
| 3 | `should_extractMetadataFromS3Object_when_exifImageProvided` | `S3ImageRepositoryImpl.extract は EXIF 付き S3 画像からメタデータを抽出する` |
| 4 | `should_useFallbackValues_when_gpsAndTakenAtAreMissing` | `S3ImageRepositoryImpl.extract は GPS と撮影日時が無い場合に既定値を使用する` |
| 5 | `should_deleteS3Object_when_storageObjectProvided` | `S3ImageRepositoryImpl.delete は対象 S3 オブジェクトを削除する` |
| 6 | `should_throwIOException_when_imageFormatIsUnsupported` | `S3ImageRepositoryImpl.store は非対応画像形式の場合に IOException を送出する` |
| 7 | `should_throwIOException_when_uploadFails` | `S3ImageRepositoryImpl.store は S3 アップロード失敗時に IOException を送出する` |
| 8 | `should_throwIOException_when_s3ObjectDoesNotExist` | `S3ImageRepositoryImpl.extract は S3 オブジェクト未存在時に IOException を送出する` |
| 9 | `should_throwIOException_when_s3AccessFails` | `S3ImageRepositoryImpl.extract と delete は S3 アクセス失敗時に IOException を送出する` |
| 10 | `should_throwImageProcessingException_when_extensionCannotBeExtractedFromKey` | `S3ImageRepositoryImpl.extract はオブジェクトキーから拡張子を取得できない場合に ImageProcessingException を送出する` |
