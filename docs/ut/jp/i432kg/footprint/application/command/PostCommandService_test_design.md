# `PostCommandService` テスト仕様書

## 1. 基本情報

- 対象クラス: `PostCommandService`
- 対象メソッド: `createPost(CreatePostCommand)`
- 対象パッケージ: `jp.i432kg.footprint.application.command.service`
- 対応するテストクラス: `PostCommandServiceTest`
- 作成者: Codex
- 作成日: 2026-04-17

## 2. 対象概要

- 何をする処理か: 投稿ユーザーの存在確認後、画像保存、メタデータ抽出、`Image` / `Post` 生成、投稿保存を行う
- 入力: `CreatePostCommand`
- 出力: `void`
- 主な副作用: 画像保存、画像削除、投稿保存

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 投稿作成成功 | 依存処理が正常な場合に画像保存・メタデータ抽出・投稿保存が行われ、固定 `Clock` と固定 `PostIdGenerator` に基づく `createdAt` / `PostId` が設定されること |
| 2 | 異常系 | ユーザー不存在 | `UserNotFoundException` を送出し、後続処理を呼ばないこと |
| 3 | 異常系 | 画像保存失敗 | `IOException` を `PostCommandFailedException.imageSaveFailed(...)` へ変換すること |
| 4 | 異常系 | メタデータ抽出失敗 | 保存済み画像を削除し、`imageMetadataExtractFailed(...)` へ変換すること |
| 5 | 異常系 | 投稿保存失敗 | 保存済み画像を削除し、`persistenceFailed(...)` へ変換すること |
| 6 | エッジケース | cleanup 失敗 | cleanup 失敗は再送出せず、元の例外を優先すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 投稿を作成する | ユーザー存在、画像保存成功、メタデータ抽出成功、保存成功、固定 `Clock` / 固定 `PostIdGenerator` | 画像保存・メタデータ抽出・投稿保存が呼ばれ、保存される `Post` の `createdAt` と `PostId` が固定値になる |  |
| 2 | 異常系 | ユーザー不存在を拒否する | `isExistUser=false` | `UserNotFoundException` | `imageStorage`, `imageMetadataExtractor`, `postRepository` 未呼び出し |
| 3 | 異常系 | 画像保存失敗を変換する | `imageStorage.store` が `IOException` | `PostCommandFailedException` | cleanup なし |
| 4 | 異常系 | メタデータ抽出失敗時に cleanup する | `extract` が `ImageProcessingException` または `IOException` | `PostCommandFailedException`, `imageStorage.delete` 呼び出し |  |
| 5 | 異常系 | 投稿保存失敗時に cleanup する | `savePost` が `DataAccessException` | `PostCommandFailedException`, `imageStorage.delete` 呼び出し |  |
| 6 | エッジケース | cleanup 自体が失敗しても元例外を優先する | 4 または 5 に加え `delete` が `IOException` | 元の `PostCommandFailedException` を送出 |  |

## 5. 実装メモ

- モック化する依存: `PostRepository`, `ImageStorage`, `ImageMetadataExtractor`, `UserDomainService`
- 固定化が必要な値: `StorageObject`, `ImageMetadata`, `CreatePostCommand`, `Clock`, `PostIdGenerator`
- `@DisplayName` 方針: `createPost の正常系と失敗箇所を記載する`
- 備考:
  - 固定 `Clock` により `createdAt` を `2026-04-18T19:15:30` として確認する
  - 固定 `PostIdGenerator` により `PostId` を明示的に確認する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_createPost_when_dependenciesSucceed` | `PostCommandService.createPost は依存処理が成功した場合に投稿を作成する` |
| 2 | `should_throwUserNotFoundException_when_userDoesNotExist` | `PostCommandService.createPost はユーザーが存在しない場合に UserNotFoundException を送出する` |
| 3 | `should_throwUsecaseException_when_imageStoreFails` | `PostCommandService.createPost は画像保存失敗を PostCommandFailedException に変換する` |
| 4 | `should_cleanupStoredImage_when_metadataExtractionFails` | `PostCommandService.createPost はメタデータ抽出失敗時に保存済み画像を削除する` |
| 5 | `should_cleanupStoredImage_when_persistenceFails` | `PostCommandService.createPost は投稿保存失敗時に保存済み画像を削除する` |
| 6 | `should_prioritizeOriginalException_when_cleanupFails` | `PostCommandService.createPost は cleanup 失敗時も元の例外を優先する` |
