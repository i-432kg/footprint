# `ReplyPostMismatchException` テスト仕様書

## 1. 基本情報

- 対象クラス: `ReplyPostMismatchException`
- 対象メソッド: コンストラクタ
- 対象パッケージ: `jp.i432kg.footprint.domain.exception`
- 対応するテストクラス: `ReplyPostMismatchExceptionTest`
- 作成者: Codex
- 作成日: 2026-04-16

## 2. 対象概要

- 何をする処理か: 返信が属する投稿と期待する投稿が一致しない場合の業務例外を生成する
- 入力: `expectedPostId`, `actualPostId`
- 出力: `ReplyPostMismatchException`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 固定フォーマット message | `expectedPostId`, `actualPostId` を埋め込んだ message を生成すること |
| 2 | 正常系 | details | `target=reply.postId`, `reason=post_mismatch`, `expectedPostId`, `actualPostId` を保持すること |
| 3 | 正常系 | エラーコード | `REPLY_POST_MISMATCH` を保持すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 例外情報を保持する | `expectedPostId=otherPostId`, `actualPostId=postId` | `REPLY_POST_MISMATCH`, 固定 format message, details 保持 |  |

## 5. 実装メモ

- モック化する依存: なし
- 固定化が必要な値: `expectedPostId`, `actualPostId`
- `@DisplayName` 方針: `ReplyPostMismatchException の保持内容を記載する`
- 備考:
  - `details(...)` ヘルパーを使うため、`target` と `reason` も確認対象とする

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_setMessageErrorCodeAndDetails_when_constructed` | `ReplyPostMismatchException は message と errorCode と details を保持する` |
