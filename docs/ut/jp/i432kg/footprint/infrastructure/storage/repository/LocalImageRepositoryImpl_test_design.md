# `LocalImageRepositoryImpl` テスト仕様書

## 1. 基本情報

- 対象クラス: `LocalImageRepositoryImpl`
- 対象メソッド: `store(InputStream, FileName, UserId, PostId)`, `extract(StorageObject)`, `delete(StorageObject)`
- 対象パッケージ: `jp.i432kg.footprint.infrastructure.storage.repository`
- 対応するテストクラス: `LocalImageRepositoryImplTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をする処理か: 画像をローカルストレージへ保存し、保存済み画像から EXIF と画像サイズを抽出し、不要画像を削除する
- 入力: `InputStream`, `FileName`, `UserId`, `PostId`, `StorageObject`
- 出力: `StorageObject`, `ImageMetadata`, `void`
- 主な副作用: 一時ファイル作成、画像ファイル移動、ファイル削除、`LocalStoragePathResolver` / `ImageIdGenerator` 呼び出し

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 画像保存 | 判定した拡張子と生成した `ImageId` から `StorageObject.local(...)` を返し、最終保存先へファイルを移動すること |
| 2 | 正常系 | 拡張子フォールバック | `FileTypeDetector` で特定できない場合でも元ファイル名の拡張子が許可されていれば保存できること |
| 3 | 正常系 | メタデータ抽出 | 画像サイズ、位置情報、撮影日時、ファイルサイズ、拡張子、EXIF 有無を `ImageMetadata` へ詰め替えること |
| 4 | 正常系 | メタデータ既定値 | GPS や撮影日時が無い場合に `Location.unknown()` と `LocalDateTime.now(clock)` を使うこと |
| 5 | 正常系 | 削除 | resolver が返す `Path` に対して `Files.deleteIfExists` 相当の削除を行うこと |
| 6 | 異常系 | 非対応画像形式 | 許可されない拡張子しか導けない場合に `IOException("サポートされていない画像形式です。")` を送出すること |
| 7 | 異常系 | 保存失敗 | `Files.copy` / `Files.move` などの `IOException` をそのまま再送出すること |
| 8 | 異常系 | メタデータ解析失敗 | 画像解析時の `IllegalArgumentException` を `ImageProcessingException` へ変換すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | JPEG 画像をローカル保存する | JPEG バイト列, `originalFilename=sample.png`, 固定 `ImageId` | `StorageObject.local(objectKey)` を返し、最終保存先にファイルが存在する | 判定拡張子は `jpg` を優先 |
| 2 | 正常系 | 判定不能でも元ファイル名拡張子で保存する | 判定不能バイト列, `originalFilename=sample.png` | `png` 拡張子の object key で保存する | フォールバック確認 |
| 3 | 正常系 | EXIF 付き画像からメタデータを抽出する | GPS / 撮影日時を持つ画像, resolver が実ファイル `Path` を返す | `ImageMetadata` に幅・高さ・位置情報・撮影日時・拡張子・EXIF 有無・ファイルサイズが設定される | 実画像 fixture 推奨 |
| 4 | 正常系 | GPS と撮影日時が無い画像で既定値を使う | EXIF なし画像, 固定 `Clock` | `location=Location.unknown()`, `takenAt=LocalDateTime.now(clock)`, `hasEXIF=false` |  |
| 5 | 正常系 | 保存済み画像を削除する | resolver が既存ファイル `Path` を返す | 対象ファイルが削除される | `deleteIfExists` のため未存在でも例外なし |
| 6 | 異常系 | 非対応拡張子で保存に失敗する | 判定不能バイト列, `originalFilename=sample.txt` | `IOException("サポートされていない画像形式です。")` | 原因例外は `IllegalArgumentException` |
| 7 | 異常系 | 最終保存先への移動失敗を再送出する | resolver が書き込み不能先を返すなど | `IOException` をそのまま送出する | 一時ファイル生成後の失敗 |
| 8 | 異常系 | 解析対象が画像でなくメタデータ抽出に失敗する | resolver が不正ファイル `Path` を返す | `ImageProcessingException("画像メタデータの解析に失敗しました: ...")` | `IllegalArgumentException` 変換確認 |

## 5. 実装メモ

- モック化する依存: `LocalStoragePathResolver`, `ImageIdGenerator`
- 固定化が必要な値: `@TempDir`, `Clock.fixed(...)`, `ImageId`, `UserId`, `PostId`, `FileName`
- `@DisplayName` 方針: 保存 / 解析 / 削除 / 例外変換が分かる日本語にする
- 備考:
  - 保存系は resolver に返させる最終 `Path` を `@TempDir` 配下へ向ける
  - `extract` は実ファイルを用意して `ImageIO` と metadata-extractor を通した結果を確認する
  - `store` の object key 全体一致に加えて、拡張子と `ImageId` が期待値どおりかも確認する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_storeJpegImageToLocalPath_when_validImageProvided` | `LocalImageRepositoryImpl.store は JPEG 画像をローカル保存して StorageObject を返す` |
| 2 | `should_fallbackToOriginalExtension_when_fileTypeCannotBeDetected` | `LocalImageRepositoryImpl.store は画像形式判定不能時に元ファイル名の拡張子へフォールバックする` |
| 3 | `should_extractMetadataFromLocalImage_when_exifImageProvided` | `LocalImageRepositoryImpl.extract は EXIF 付きローカル画像からメタデータを抽出する` |
| 4 | `should_useFallbackValues_when_gpsAndTakenAtAreMissing` | `LocalImageRepositoryImpl.extract は GPS と撮影日時が無い場合に既定値を使用する` |
| 5 | `should_deleteLocalFile_when_storageObjectExists` | `LocalImageRepositoryImpl.delete は対象ローカルファイルを削除する` |
| 6 | `should_throwIOException_when_imageFormatIsUnsupported` | `LocalImageRepositoryImpl.store は非対応画像形式の場合に IOException を送出する` |
| 7 | `should_rethrowIOException_when_moveFails` | `LocalImageRepositoryImpl.store はファイル移動失敗時に IOException を再送出する` |
| 8 | `should_throwImageProcessingException_when_metadataExtractionFails` | `LocalImageRepositoryImpl.extract はメタデータ解析失敗時に ImageProcessingException を送出する` |
