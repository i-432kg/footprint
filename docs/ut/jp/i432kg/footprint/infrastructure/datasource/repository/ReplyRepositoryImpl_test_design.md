# `ReplyRepositoryImpl` テスト仕様書

## 1. 基本情報

- 対象クラス: `ReplyRepositoryImpl`
- 対象メソッド: `findReplyById(ReplyId)`, `saveReply(Reply)`, `increaseReplyCount(ReplyId)`
- 対象パッケージ: `jp.i432kg.footprint.infrastructure.datasource.repository`
- 対応するテストクラス: `ReplyRepositoryImplTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をする処理か: 返信検索・保存・子返信数更新を `ReplyMapper` に委譲し、検索時は mapper 結果を domain model へ変換する
- 入力: `ReplyId`, `Reply`
- 出力: `Optional<Reply>`, `void`
- 主な副作用: `ReplyMapper` の `findReplyById`, `insert`, `incrementChildCount` 呼び出し

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 返信検索 | mapper 戻り値 `ReplyResultEntity` が `Reply` に変換されて返ること |
| 2 | 正常系 | 空検索結果 | mapper が `Optional.empty()` を返した場合にそのまま返ること |
| 3 | 正常系 | 返信保存 | `ReplyInsertEntity` を生成して `insert` に委譲すること |
| 4 | 正常系 | 子返信数更新 | `increaseReplyCount` が `incrementChildCount` に委譲すること |
| 5 | 異常系 | 例外再送出 | 各 mapper 呼び出しの `RuntimeException` を再送出すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 返信検索結果を domain model に変換する | `findReplyById(replyId)` が `Optional.of(resultEntity)` を返す | `Optional<Reply>` が返り、主要項目が一致する | 親返信あり / なしはどちらか一方で代表確認 |
| 2 | 正常系 | 返信が存在しない場合に空を返す | `findReplyById(replyId)` が `Optional.empty()` | `Optional.empty()` を返す |  |
| 3 | 正常系 | 返信を保存する | `Reply` を入力、mapper 正常 | `insert(ReplyInsertEntity)` が呼ばれ、entity の内容が `Reply` と一致する |  |
| 4 | 正常系 | 子返信数を増やす | `replyId` を入力 | `incrementChildCount(replyId)` が呼ばれる |  |
| 5 | 異常系 | 返信検索失敗を再送出する | `findReplyById(replyId)` が `RuntimeException` | 同じ例外を送出する |  |
| 6 | 異常系 | 返信保存失敗を再送出する | `insert(...)` が `RuntimeException` | 同じ例外を送出する |  |
| 7 | 異常系 | 子返信数更新失敗を再送出する | `incrementChildCount(replyId)` が `RuntimeException` | 同じ例外を送出する |  |

## 5. 実装メモ

- モック化する依存: `ReplyMapper`
- 固定化が必要な値: `Reply`, `ReplyId`, `PostId`, `UserId`, `LocalDateTime`
- `@DisplayName` 方針: 検索 / 保存 / カウント更新の振る舞いを日本語で明記する
- 備考:
  - 検索成功ケースは `ReplyMapper.ReplyResultEntity` を直接組み立てる
  - `ReplyInsertEntity` の `childCount=0`、`createdAt=updatedAt` を確認候補に含める

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_returnDomainReply_when_findReplyByIdFindsEntity` | `ReplyRepositoryImpl.findReplyById は検索結果を Reply へ変換して返す` |
| 2 | `should_returnEmptyOptional_when_findReplyByIdFindsNothing` | `ReplyRepositoryImpl.findReplyById は返信が存在しない場合に Optional.empty を返す` |
| 3 | `should_saveReply_when_saveReplyCalled` | `ReplyRepositoryImpl.saveReply は返信を mapper へ保存委譲する` |
| 4 | `should_incrementChildCount_when_increaseReplyCountCalled` | `ReplyRepositoryImpl.increaseReplyCount は子返信数更新を mapper へ委譲する` |
| 5 | `should_rethrowException_when_findReplyByIdFails` | `ReplyRepositoryImpl.findReplyById は mapper 例外を再送出する` |
| 6 | `should_rethrowException_when_saveReplyFails` | `ReplyRepositoryImpl.saveReply は mapper 例外を再送出する` |
| 7 | `should_rethrowException_when_increaseReplyCountFails` | `ReplyRepositoryImpl.increaseReplyCount は mapper 例外を再送出する` |
