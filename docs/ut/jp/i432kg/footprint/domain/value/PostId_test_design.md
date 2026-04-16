# `PostId` テスト仕様書

## 1. 基本情報

- 対象クラス: `PostId`
- 対象メソッド: `of(String)`
- 対象パッケージ: `jp.i432kg.footprint.domain.value`
- 対応するテストクラス: `PostIdTest`
- 作成者: Codex
- 作成日: 2026-04-15

## 2. 対象概要

- 何をする処理か: 投稿 ID として妥当な ULID を表す値オブジェクトを生成する
- 入力: `String`
- 出力: `PostId`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | ULID妥当 | 妥当な ULID を受け入れること |
| 2 | 異常系 | 必須 | `null`、blank を拒否すること |
| 3 | 異常系 | 形式 | 長さ不正、許可外文字、小文字を拒否すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 妥当な ULID を受け入れる | 26文字の妥当な ULID | 生成成功 |  |
| 2 | 異常系 | `null` を拒否する | `null` | `required("post_id")` |  |
| 3 | 異常系 | blank を拒否する | `" "` | `blank("post_id")` |  |
| 4 | 異常系 | 長さ不正を拒否する | 25文字, 27文字 | `invalidFormat(...)` |  |
| 5 | 異常系 | 許可外文字を拒否する | `I`, `L`, `O`, 小文字を含む値 | `invalidFormat(...)` |  |

## 5. 実装メモ

- 固定化が必要な値: 妥当な ULID 文字列
- `@DisplayName` 方針: `PostId.of の ULID 条件を記載する`

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_createPostId_when_valueIsValidUlid` | `PostId.of は妥当な ULID を受け入れる` |
| 2 | `should_throwException_when_postIdIsNullOrBlank` | `PostId.of は null または空白のみを拒否する` |
| 3 | `should_throwException_when_postIdFormatIsInvalid` | `PostId.of は ULID 形式でない値を拒否する` |
