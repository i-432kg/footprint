# `ReplyCommandService` テスト仕様書

## 1. 基本情報

- 対象クラス: `ReplyCommandService`
- 対象メソッド: `createReply(CreateReplyCommand)`
- 対象パッケージ: `jp.i432kg.footprint.application.command`
- 対応するテストクラス: `ReplyCommandServiceTest`
- 作成者: Codex
- 作成日: 2026-04-17

## 2. 対象概要

- 何をする処理か: 投稿・ユーザー・親返信の妥当性を確認し、新しい返信を保存し、必要に応じて親返信の子返信数を更新する
- 入力: `CreateReplyCommand`
- 出力: `void`
- 主な副作用: 返信保存、返信数更新

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | ルート返信作成 | 親返信なしの場合に保存のみ行い、返信数更新をしないこと。固定 `Clock` と固定 `ReplyIdGenerator` に基づく `createdAt` / `ReplyId` が設定されること |
| 2 | 正常系 | 子返信作成 | 親返信ありの場合に親返信の存在確認・投稿整合性確認後、保存と返信数更新を行うこと |
| 3 | 異常系 | 投稿不存在 | `PostNotFoundException` を送出し、後続処理を呼ばないこと |
| 4 | 異常系 | ユーザー不存在 | `UserNotFoundException` を送出し、後続処理を呼ばないこと |
| 5 | 異常系 | 親返信不存在 | `ReplyNotFoundException` を送出し、保存しないこと |
| 6 | 異常系 | 保存失敗 | `DataAccessException` を `ReplyCommandFailedException.saveFailed(...)` に変換すること |
| 7 | 異常系 | 返信数更新失敗 | `increaseReplyCountFailed(...)` に変換すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | ルート返信を作成する | `ParentReply.root()`, 各存在確認成功、固定 `Clock` / 固定 `ReplyIdGenerator` | `saveReply` 呼び出し、`increaseReplyCount` 未呼び出し、保存される `Reply` の `createdAt` と `ReplyId` が固定値になる |  |
| 2 | 正常系 | 子返信を作成する | 親返信あり、親返信取得成功、投稿整合性確認成功 | `saveReply` と `increaseReplyCount` 呼び出し |  |
| 3 | 異常系 | 投稿不存在を拒否する | `isExistPost=false` | `PostNotFoundException` | repository 未呼び出し |
| 4 | 異常系 | ユーザー不存在を拒否する | `isExistUser=false` | `UserNotFoundException` | repository 未呼び出し |
| 5 | 異常系 | 親返信不存在を拒否する | `findReplyById=Optional.empty()` | `ReplyNotFoundException` | `saveReply` 未呼び出し |
| 6 | 異常系 | 保存失敗を変換する | `saveReply` が `DataAccessException` | `ReplyCommandFailedException` |  |
| 7 | 異常系 | 返信数更新失敗を変換する | `increaseReplyCount` が `DataAccessException` | `ReplyCommandFailedException` | 子返信ケース |

## 5. 実装メモ

- モック化する依存: `ReplyRepository`, `PostDomainService`, `ReplyDomainService`, `UserDomainService`
- 固定化が必要な値: `CreateReplyCommand`, 親返信用 `Reply`, `Clock`, `ReplyIdGenerator`
- `@DisplayName` 方針: `createReply の事前検証、保存、返信数更新を記載する`
- 備考:
  - 親返信ありケースでは `validateParentReplyBelongsToPost(...)` が呼ばれることも確認対象とする
  - ルート返信ケースでは固定 `Clock` による `createdAt` と固定 `ReplyIdGenerator` による `ReplyId` を確認する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_createRootReply_when_parentReplyDoesNotExist` | `ReplyCommandService.createReply は親返信がない場合に返信を保存する` |
| 2 | `should_createChildReply_when_parentReplyExists` | `ReplyCommandService.createReply は親返信がある場合に返信保存と返信数更新を行う` |
| 3 | `should_throwPostNotFoundException_when_postDoesNotExist` | `ReplyCommandService.createReply は投稿が存在しない場合に PostNotFoundException を送出する` |
| 4 | `should_throwUserNotFoundException_when_userDoesNotExist` | `ReplyCommandService.createReply はユーザーが存在しない場合に UserNotFoundException を送出する` |
| 5 | `should_throwReplyNotFoundException_when_parentReplyDoesNotExist` | `ReplyCommandService.createReply は親返信が存在しない場合に ReplyNotFoundException を送出する` |
| 6 | `should_throwUsecaseException_when_saveFails` | `ReplyCommandService.createReply は保存失敗を ReplyCommandFailedException に変換する` |
| 7 | `should_throwUsecaseException_when_increaseReplyCountFails` | `ReplyCommandService.createReply は返信数更新失敗を ReplyCommandFailedException に変換する` |
