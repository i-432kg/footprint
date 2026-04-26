# `ReplyNotFoundException` テスト仕様書

## 1. 基本情報

- 対象クラス: `ReplyNotFoundException`
- 対象パッケージ: `jp.i432kg.footprint.application.exception.resource`
- 対応するテストクラス: `ReplyNotFoundExceptionTest`
- 作成者: Codex
- 作成日: 2026-04-18

## 2. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | not found 組み立て | `REPLY_NOT_FOUND`, message, `target=reply`, `reason=not_found`, `resourceId` details を組み立てること |

## 3. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | 返信 not found を表現する | `ReplyId` を渡して生成 | `errorCode`, message, `details.target=reply`, `details.reason=not_found`, `details.resourceId` が一致する |

## 4. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_buildReplyNotFoundDetails_when_created` | `ReplyNotFoundException は返信 not found 情報を組み立てる` |
