# `PostItemResponse` テスト仕様書

## 1. 基本情報

- 対象クラス: `PostItemResponse`
- 対象パッケージ: `jp.i432kg.footprint.presentation.api.response`
- 対応するテストクラス: `PostItemResponseTest`
- 作成者: Codex
- 作成日: 2026-04-18

## 2. 対象概要

- 何をするクラスか: 投稿一覧および投稿詳細で返却する投稿レスポンス DTO
- 主な項目: `id`, `caption`, `images`, `hasLocation`, `location`, `createdAt`

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 保持値 | 投稿参照情報をそのまま保持すること |
| 2 | 正常系 | nullable 位置情報 | `location=null` を保持できること |
| 3 | 正常系 | コレクション順序 | `images` を順序どおり保持すること |
| 4 | 正常系 | 日時保持 | `createdAt` をそのまま保持すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | 投稿レスポンスの各項目を保持する | 画像一覧あり、位置情報あり、`createdAt` 指定 | getter で同じ値を取得できる |
| 2 | 正常系 | 位置情報なし投稿を保持する | `location=null`, `hasLocation=false` | `location=null` を保持する |

## 5. 実装メモ

- `images` は 2 件程度入れて順序も確認する
- `location` の nullable は明示的に別ケースで確認する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_holdValues_when_allFieldsAreSet` | `PostItemResponse は設定された投稿情報を保持する` |
| 2 | `should_allowNullLocation_when_postHasNoLocation` | `PostItemResponse は位置情報なし投稿として null の location を保持できる` |
