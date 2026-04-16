# `PostComment` テスト仕様書

## 1. 基本情報

- 対象クラス: `PostComment`
- 対象メソッド: `of(String)`
- 対象パッケージ: `jp.i432kg.footprint.domain.value`
- 対応するテストクラス: `PostCommentTest`
- 作成者: Codex
- 作成日: 2026-04-15

## 2. 対象概要

- 何をする処理か: 投稿コメント本文を表す値オブジェクトを生成する
- 入力: `String`
- 出力: `PostComment`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 通常入力 | 通常文字列を受け入れること |
| 2 | 正常系 | 空文字・改行 | 空文字と改行を含む文字列を受け入れること |
| 3 | 異常系 | 必須 | `null` を拒否すること |
| 4 | 異常系 | 制御文字 | 改行以外の C0 制御文字と DEL を拒否すること |
| 5 | 境界値 | 長さ | 100文字以下を受け入れ、101文字を拒否すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 通常文字列を受け入れる | `"comment"` | 生成成功 |  |
| 2 | 正常系 | 空文字を受け入れる | `""` | 生成成功 | `ReplyComment` と差異あり |
| 3 | 正常系 | 改行を含む文字列を受け入れる | `"a\nb\r\nc"` | 生成成功 |  |
| 4 | 異常系 | `null` を拒否する | `null` | `required("postComment")` |  |
| 5 | 異常系 | 制御文字を拒否する | `"\u0000"` | `invalidFormat(...)` |  |
| 6 | 境界値 | 最大長ちょうどを受け入れる | 100文字 | 生成成功 |  |
| 7 | 異常系 | 最大長超過を拒否する | 101文字 | `tooLong(...)` |  |

## 5. 実装メモ

- `@DisplayName` 方針: `PostComment.of の受入条件と ReplyComment との差異を示す`

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_createPostComment_when_valueIsNormalText` | `PostComment.of は通常の本文を受け入れる` |
| 2 | `should_createPostComment_when_valueIsEmptyString` | `PostComment.of は空文字を受け入れる` |
| 3 | `should_createPostComment_when_valueContainsLineBreaks` | `PostComment.of は改行を含む本文を受け入れる` |
| 4 | `should_throwException_when_postCommentIsNull` | `PostComment.of は null を拒否する` |
| 5 | `should_throwException_when_postCommentContainsControlCharacters` | `PostComment.of は改行以外の制御文字を拒否する` |
| 6 | `should_throwException_when_postCommentExceedsMaxLength` | `PostComment.of は最大長を超える本文を拒否する` |
