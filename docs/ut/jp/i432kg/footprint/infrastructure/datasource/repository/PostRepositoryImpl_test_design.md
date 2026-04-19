# `PostRepositoryImpl` テスト仕様書

## 1. 基本情報

- 対象クラス: `PostRepositoryImpl`
- 対象メソッド: `existsById(PostId)`, `savePost(Post)`
- 対象パッケージ: `jp.i432kg.footprint.infrastructure.datasource.repository`
- 対応するテストクラス: `PostRepositoryImplTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をする処理か: 投稿存在確認と投稿永続化を `PostMapper` に委譲し、投稿保存時は投稿本体と画像情報を mapper 用 entity に変換して順に登録する
- 入力: `PostId`, `Post`
- 出力: `boolean`, `void`
- 主な副作用: `PostMapper` の `countByPostId`, `insertPosts`, `insertPostImages` 呼び出し

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 存在確認 | `existsById` が `countByPostId` の結果を `> 0` 判定で返すこと |
| 2 | 正常系 | 投稿保存 | `savePost` が `PostInsertEntity` と `PostImageInsertEntity` を生成して mapper に渡すこと |
| 3 | 正常系 | 保存順序 | `insertPosts` の後に `insertPostImages` を呼ぶこと |
| 4 | 異常系 | 存在確認失敗 | `countByPostId` の `RuntimeException` を再送出すること |
| 5 | 異常系 | 投稿保存失敗 | `insertPosts` または `insertPostImages` の `RuntimeException` を再送出すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 投稿が存在する場合に `true` を返す | `countByPostId(postId)=1` | `existsById(postId)` が `true` |  |
| 2 | 正常系 | 投稿が存在しない場合に `false` を返す | `countByPostId(postId)=0` | `existsById(postId)` が `false` |  |
| 3 | 正常系 | 投稿と画像情報を保存する | `Post` を入力、mapper 正常 | `insertPosts` と `insertPostImages` が順に呼ばれ、生成 entity の内容が `Post` と一致する | `ArgumentCaptor` と `InOrder` 使用 |
| 4 | 異常系 | 存在確認失敗を再送出する | `countByPostId(postId)` が `RuntimeException` | 同じ例外を送出する |  |
| 5 | 異常系 | 投稿本体保存失敗を再送出する | `insertPosts(...)` が `RuntimeException` | 同じ例外を送出し、`insertPostImages` は呼ばれない |  |
| 6 | 異常系 | 投稿画像保存失敗を再送出する | `insertPostImages(...)` が `RuntimeException` | 同じ例外を送出する | 投稿本体保存は実行済み |

## 5. 実装メモ

- モック化する依存: `PostMapper`
- 固定化が必要な値: `Post`, `PostId`, `StorageObject`, `Location`, `LocalDateTime`
- `@DisplayName` 方針: `PostRepositoryImpl` の公開メソッド名と結果を日本語で明記する
- 備考:
  - `PostMapper.PostInsertEntity.from(post)` と同等内容になっていることを captor で確認する
  - `PostMapper.PostImageInsertEntity.from(post)` も同様に主要項目を確認する
  - `id` は insert 前のため `null` を期待する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_returnTrue_when_postExists` | `PostRepositoryImpl.existsById は投稿が存在する場合に true を返す` |
| 2 | `should_returnFalse_when_postDoesNotExist` | `PostRepositoryImpl.existsById は投稿が存在しない場合に false を返す` |
| 3 | `should_savePostAndImage_when_savePostCalled` | `PostRepositoryImpl.savePost は投稿本体と画像情報を順に保存する` |
| 4 | `should_rethrowException_when_existsByIdFails` | `PostRepositoryImpl.existsById は mapper 例外を再送出する` |
| 5 | `should_rethrowExceptionAndSkipImageSave_when_insertPostsFails` | `PostRepositoryImpl.savePost は投稿本体保存失敗時に例外を再送出し画像保存を行わない` |
| 6 | `should_rethrowException_when_insertPostImagesFails` | `PostRepositoryImpl.savePost は画像保存失敗時に例外を再送出する` |
