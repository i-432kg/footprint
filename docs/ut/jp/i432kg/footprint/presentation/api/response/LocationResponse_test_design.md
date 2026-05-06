# `LocationResponse` テスト仕様書

## 1. 基本情報

- 対象クラス: `LocationResponse`
- 対象パッケージ: `jp.i432kg.footprint.presentation.api.response`
- 対応するテストクラス: `LocationResponseTest`
- 作成者: Codex
- 作成日: 2026-04-18

## 2. 対象概要

- 何をするクラスか: 投稿に紐づく位置情報を表す API レスポンス DTO
- 主な項目: `lat`, `lng`

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 保持値 | 緯度経度をそのまま保持すること |
| 2 | 正常系 | nullable 座標 | `lat/lng=null` を保持できること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | 緯度経度を保持する | `lat/lng=正常値` | getter で同じ値を取得できる |
| 2 | 正常系 | `null` 座標を保持する | `lat=null`, `lng=null` | `lat/lng` が `null` |

## 5. 実装メモ

- `@Nullable` が付いた 2 項目だけ `null` ケースを持つ

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_holdValues_when_latAndLngArePresent` | `LocationResponse は設定された緯度経度を保持する` |
| 2 | `should_allowNullCoordinates_when_latAndLngAreNull` | `LocationResponse は null の緯度経度を保持できる` |
