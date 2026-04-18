# `UserProfileResponseMapper` テスト仕様書

## 1. 基本情報

- 対象クラス: `UserProfileResponseMapper`
- 対象パッケージ: `jp.i432kg.footprint.presentation.api.response.mapper`
- 対応するテストクラス: `UserProfileResponseMapperTest`
- 作成者: Codex
- 作成日: 2026-04-18

## 2. 対象概要

- 何をするクラスか: ユーザープロフィール query model を API レスポンス DTO に変換する mapper
- 主な入力: `UserProfileSummary`

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 単体変換 | `UserProfileSummary` の全項目が `UserProfileItemResponse` に正しく写像されること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | ユーザープロフィールをレスポンスへ変換する | 全項目が入った `UserProfileSummary` | 全項目が response に写像される |

## 5. 実装メモ

- 単純な項目写像のみなので、各 getter の値がそのまま response に入ることを確認する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_mapUserProfileSummaryToResponse_when_summaryHasAllFields` | `UserProfileResponseMapper はプロフィールサマリーの全項目をレスポンスへ変換する` |
