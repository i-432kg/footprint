# `ReplyResponseMapper` テスト仕様書

## 1. 基本情報

- 対象クラス: `ReplyResponseMapper`
- 対象パッケージ: `jp.i432kg.footprint.presentation.api.response.mapper`
- 対応するテストクラス: `ReplyResponseMapperTest`
- 作成者: Codex
- 作成日: 2026-04-18

## 2. 対象概要

- 何をするクラスか: 返信系 query model を API レスポンス DTO に変換する mapper
- 主な入力: `ReplySummary`, `List<ReplySummary>`

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 単体変換 | `ReplySummary` の全項目が `ReplyItemResponse` に正しく写像されること |
| 2 | 正常系 | UTC 化 | `createdAt` が UTC オフセット付き `OffsetDateTime` に変換されること |
| 3 | 正常系 | nullable 親返信 ID | `parentReplyId=null` が `ReplyItemResponse.parentReplyId=null` として引き継がれること |
| 4 | 正常系 | 一覧変換 | `fromList(...)` が要素順を維持して変換すること |
| 5 | 正常系 | 一覧中 null 除外 | 一覧中の `null` 要素を除外すること |
| 6 | 正常系 | null 一覧 | `fromList(null)` が空リストを返すこと |
| 7 | 正常系 | null 入力 | `from(null)` が `null` を返すこと |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | 返信をレスポンスへ変換する | 全項目が入った `ReplySummary` | 全項目が response に写像される |
| 2 | 正常系 | `createdAt` を UTC 化する | `createdAt=2026-04-18T19:15:30` | `2026-04-18T19:15:30Z` |
| 3 | 正常系 | ルート返信を変換する | `parentReplyId=null` | `parentReplyId=null` |
| 4 | 正常系 | 返信一覧を変換する | `ReplySummary` 2 件 | 2 件の response を順序維持で返す |
| 5 | 正常系 | 一覧中の `null` を除外する | `[summary, null]` | response 1 件のみ返す |
| 6 | 正常系 | `null` 一覧を空リストにする | `summaries=null` | 空リスト |
| 7 | 正常系 | `null` 返信を受け取る | `summary=null` | `null` |

## 5. 実装メモ

- `createdAt` の UTC 化は単体変換テストに含めてもよいが、仕様上は独立観点として明示する
- `ReplySummary.userId` は response に含まれないため写像対象外であることに注意する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_mapReplySummaryToResponse_when_summaryHasAllFields` | `ReplyResponseMapper は返信サマリーの全項目をレスポンスへ変換する` |
| 2 | `should_convertCreatedAtToUtcOffsetDateTime_when_mappingReplySummary` | `ReplyResponseMapper は createdAt を UTC オフセット付き日時へ変換する` |
| 3 | `should_allowNullParentReplyId_when_replyIsRoot` | `ReplyResponseMapper はルート返信の null 親返信 ID をそのまま引き継ぐ` |
| 4 | `should_mapReplySummaryList_when_summariesArePresent` | `ReplyResponseMapper は返信一覧を順序を保って変換する` |
| 5 | `should_filterOutNullSummaries_when_mappingReplySummaryList` | `ReplyResponseMapper は一覧中の null 要素を除外して変換する` |
| 6 | `should_returnEmptyList_when_replySummaryListIsNull` | `ReplyResponseMapper は null の一覧入力に対して空リストを返す` |
| 7 | `should_returnNull_when_replySummaryIsNull` | `ReplyResponseMapper は null の返信サマリー入力に対して null を返す` |
