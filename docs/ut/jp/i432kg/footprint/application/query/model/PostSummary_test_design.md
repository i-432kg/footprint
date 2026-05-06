# `PostSummary` テスト仕様書

## 1. 基本情報

- 対象クラス: `PostSummary`
- 対象パッケージ: `jp.i432kg.footprint.application.query.model`
- 対応するテストクラス: `PostSummaryTest`
- 作成者: Codex
- 作成日: 2026-04-18

## 2. 対象概要

- 何をするクラスか: 投稿一覧・詳細表示用の参照情報を保持する query model
- 主な項目: `id`, `caption`, `images`, `hasLocation`, `location`, `createdAt`

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 保持値 | 投稿情報、画像一覧、位置情報、作成日時を保持すること |
| 2 | 異常系相当 | nullable 項目 | `location` が `null` を保持できること |
| 3 | エッジケース | hasLocation と location | `hasLocation=false` かつ `location=null` の組み合わせを保持できること |
| 4 | エッジケース | 既定値 | no-args 再構築時に既定値が設定されること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | 投稿参照情報を保持する | 全フィールドに値を設定する | 各 getter が設定値を返す |
| 2 | 異常系相当 | 位置情報なし投稿を保持する | `hasLocation=false`, `location=null` | `location` が `null`、`hasLocation` が `false` |
| 3 | エッジケース | 既定値で再構築できる | no-args 生成 | 参照型は `null`、`hasLocation` は `false` |

## 5. 実装メモ

- `images` には `ImageSummary` を 1 件以上設定したケースを使う
- `location` あり / なしの両方を確認する
- `@DisplayName` 方針: `保持値`、`位置情報なし`、`既定値` を明示する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_holdValues_when_allFieldsAreSet` | `PostSummary は設定された投稿参照情報を保持する` |
| 2 | `should_allowNullLocation_when_postHasNoLocation` | `PostSummary は位置情報なし投稿として null の location を保持できる` |
| 3 | `should_initializeDefaultValues_when_createdWithNoArgsConstructor` | `PostSummary は no-args 生成時に既定値で初期化される` |
