# `ImageSummary` テスト仕様書

## 1. 基本情報

- 対象クラス: `ImageSummary`
- 対象パッケージ: `jp.i432kg.footprint.application.query.model`
- 対応するテストクラス: `ImageSummaryTest`
- 作成者: Codex
- 作成日: 2026-04-18

## 2. 対象概要

- 何をするクラスか: 投稿画像の参照情報を保持する query model
- 主な項目: `id`, `sortOrder`, `storageType`, `objectKey`, `fileExtension`, `sizeBytes`, `width`, `height`

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 保持値 | 各フィールドを設定した場合に getter で同じ値を返すこと |
| 2 | エッジケース | 既定値 | no-args 再構築時に各フィールドが既定値になること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | 画像参照情報を保持する | 全フィールドに値を設定する | 各 getter が設定値を返す |
| 2 | エッジケース | 既定値で再構築できる | no-args 生成 | 参照型は `null`、数値ラッパーは `null` |

## 5. 実装メモ

- `StorageType`, `ObjectKey` はテスト fixture または正規 factory で生成する
- `@DisplayName` 方針: `保持値` と `既定値` を明示する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_holdValues_when_allFieldsAreSet` | `ImageSummary は設定された画像参照情報を保持する` |
| 2 | `should_initializeDefaultValues_when_createdWithNoArgsConstructor` | `ImageSummary は no-args 生成時に既定値で初期化される` |
