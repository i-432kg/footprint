# `ReplyCommandFailedException` テスト仕様書

## 1. 基本情報

- 対象クラス: `ReplyCommandFailedException`
- 対象パッケージ: `jp.i432kg.footprint.application.exception.usecase`
- 対応するテストクラス: `ReplyCommandFailedExceptionTest`
- 作成者: Codex
- 作成日: 2026-04-18

## 2. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | saveFailed | `target=reply`, `reason=save_failed`, `cause` を設定し、`rejectedValue` を持たないこと |
| 2 | 正常系 | increaseReplyCountFailed | `target=reply`, `reason=increase_reply_count_failed`, `cause` を設定し、`rejectedValue` を持たないこと |

## 3. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | 保存失敗を表現する | `cause` を指定 | `details` は `target` / `reason` のみで `cause` が一致する |
| 2 | 正常系 | 返信数更新失敗を表現する | `cause` を指定 | `details` は `target` / `reason` のみで `cause` が一致する |

## 4. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_buildSaveFailedDetails_when_factoryIsUsed` | `ReplyCommandFailedException.saveFailed は保存失敗情報を組み立てる` |
| 2 | `should_buildIncreaseReplyCountFailedDetails_when_factoryIsUsed` | `ReplyCommandFailedException.increaseReplyCountFailed は返信数更新失敗情報を組み立てる` |
