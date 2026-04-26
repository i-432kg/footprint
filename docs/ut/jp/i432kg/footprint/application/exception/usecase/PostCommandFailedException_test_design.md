# `PostCommandFailedException` テスト仕様書

## 1. 基本情報

- 対象クラス: `PostCommandFailedException`
- 対象パッケージ: `jp.i432kg.footprint.application.exception.usecase`
- 対応するテストクラス: `PostCommandFailedExceptionTest`
- 作成者: Codex
- 作成日: 2026-04-18

## 2. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | imageSaveFailed | `target=image.fileName`, `reason=image_save_failed`, `rejectedValue`, `cause` を設定すること |
| 2 | 正常系 | imageMetadataExtractFailed | `target=image.objectKey`, `reason=image_metadata_extract_failed`, `rejectedValue`, `cause` を設定すること |
| 3 | 正常系 | persistenceFailed | `target=post`, `reason=persistence_error`, `rejectedValue`, `cause` を設定すること |

## 3. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | 画像保存失敗を表現する | `rejectedValue`, `cause` を指定 | `details` と `cause` が一致する |
| 2 | 正常系 | メタデータ抽出失敗を表現する | `rejectedValue`, `cause` を指定 | `details` と `cause` が一致する |
| 3 | 正常系 | 永続化失敗を表現する | `rejectedValue`, `cause` を指定 | `details` と `cause` が一致する |

## 4. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_buildImageSaveFailedDetails_when_factoryIsUsed` | `PostCommandFailedException.imageSaveFailed は画像保存失敗情報を組み立てる` |
| 2 | `should_buildImageMetadataExtractFailedDetails_when_factoryIsUsed` | `PostCommandFailedException.imageMetadataExtractFailed はメタデータ抽出失敗情報を組み立てる` |
| 3 | `should_buildPersistenceFailedDetails_when_factoryIsUsed` | `PostCommandFailedException.persistenceFailed は永続化失敗情報を組み立てる` |
