# `ImageResponse` テスト仕様書

## 1. 基本情報

- 対象クラス: `ImageResponse`
- 対象パッケージ: `jp.i432kg.footprint.presentation.api.response`
- 対応するテストクラス: `ImageResponseTest`
- 作成者: Codex
- 作成日: 2026-04-18

## 2. 対象概要

- 何をするクラスか: 投稿画像 1 件分を表す API レスポンス DTO
- 主な項目: `id`, `sortOrder`, `url`, `fileExtension`, `sizeBytes`, `width`, `height`

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 保持値 | `of(...)` で渡した画像情報をそのまま保持すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | 画像レスポンスの各項目を保持する | 全項目を指定して生成 | getter で同じ値を取得できる |

## 5. 実装メモ

- DTO 単体なので、`of(...)` と getter の対応確認に絞る

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_holdValues_when_allFieldsAreSet` | `ImageResponse は設定された画像情報を保持する` |
