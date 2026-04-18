# `ReplySummary` テスト仕様書

## 1. 基本情報

- 対象クラス: `ReplySummary`
- 対象パッケージ: `jp.i432kg.footprint.application.query.model`
- 対応するテストクラス: `ReplySummaryTest`
- 作成者: Codex
- 作成日: 2026-04-18

## 2. 対象概要

- 何をするクラスか: 返信一覧・詳細表示用の参照情報を保持する query model
- 主な項目: `id`, `postId`, `userId`, `parentReplyId`, `message`, `childCount`, `createdAt`

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 保持値 | 親返信ありの返信情報を保持すること |
| 2 | 異常系相当 | nullable 項目 | ルート返信として `parentReplyId=null` を保持できること |
| 3 | エッジケース | 既定値 | no-args 再構築時に既定値が設定されること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | 親返信ありの返信情報を保持する | 全フィールドに値を設定し、`parentReplyId` を非 `null` にする | 各 getter が設定値を返す |
| 2 | 異常系相当 | ルート返信を保持する | `parentReplyId=null` | `parentReplyId` が `null` を返す |
| 3 | エッジケース | 既定値で再構築できる | no-args 生成 | 参照型は `null` |

## 5. 実装メモ

- `childCount` は `0` 以上の通常値で保持確認する
- `@DisplayName` 方針: `親返信あり`、`ルート返信`、`既定値` を明示する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_holdValues_when_parentReplyExists` | `ReplySummary は親返信ありの返信情報を保持する` |
| 2 | `should_allowNullParentReplyId_when_replyIsTopLevel` | `ReplySummary はルート返信として null の parentReplyId を保持できる` |
| 3 | `should_initializeDefaultValues_when_createdWithNoArgsConstructor` | `ReplySummary は no-args 生成時に既定値で初期化される` |
