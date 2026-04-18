# `UserProfileSummary` テスト仕様書

## 1. 基本情報

- 対象クラス: `UserProfileSummary`
- 対象パッケージ: `jp.i432kg.footprint.application.query.model`
- 対応するテストクラス: `UserProfileSummaryTest`
- 作成者: Codex
- 作成日: 2026-04-18

## 2. 対象概要

- 何をするクラスか: ユーザープロフィール画面用の参照情報を保持する query model
- 主な項目: `id`, `username`, `email`, `postCount`, `replyCount`

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 保持値 | プロフィール情報と件数情報をそのまま保持すること |
| 2 | エッジケース | 既定値 | no-args 再構築時に既定値が設定されること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | ユーザープロフィール情報を保持する | 全フィールドに値を設定する | 各 getter が設定値を返す |
| 2 | エッジケース | 既定値で再構築できる | no-args 生成 | 文字列は `null`、件数は `0` |

## 5. 実装メモ

- `postCount`, `replyCount` は通常の正数で保持確認する
- `@DisplayName` 方針: `保持値` と `既定値` を明示する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_holdValues_when_allFieldsAreSet` | `UserProfileSummary は設定されたプロフィール情報を保持する` |
| 2 | `should_initializeDefaultValues_when_createdWithNoArgsConstructor` | `UserProfileSummary は no-args 生成時に既定値で初期化される` |
