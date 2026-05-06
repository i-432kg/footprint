# `ReplyComment` テスト仕様書

## 1. 基本情報

- 対象クラス: `ReplyComment`
- 対象メソッド: `of(String)`
- 対象パッケージ: `jp.i432kg.footprint.domain.value`
- 対応するテストクラス: `ReplyCommentTest`
- 作成者: Codex
- 作成日: 2026-04-15

## 2. 対象概要

- 何をする処理か: 返信本文を表す値オブジェクトを生成する
- 入力: `String`
- 出力: `ReplyComment`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 通常入力 | 通常文字列と改行を含む文字列を受け入れること |
| 2 | 異常系 | 必須 | `null`、空白のみを拒否すること |
| 3 | 異常系 | 制御文字 | 改行以外の C0 制御文字と DEL を拒否すること |
| 4 | 境界値 | 長さ | 100文字以下を受け入れ、101文字を拒否すること |
| 5 | エッジケース | 差異 | `PostComment` と違い blank を拒否すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 通常文字列を受け入れる | `"reply"` | 生成成功 |  |
| 2 | 正常系 | 改行を含む文字列を受け入れる | `"a\nb"` | 生成成功 |  |
| 3 | 異常系 | `null` を拒否する | `null` | `required("replyComment")` |  |
| 4 | 異常系 | 空白のみを拒否する | `" \n "` | `blank("replyComment")` |  |
| 5 | 異常系 | 制御文字を拒否する | `"\u0000"` | `invalidFormat(...)` |  |
| 6 | 境界値 | 最大長ちょうどを受け入れる | 100文字 | 生成成功 |  |
| 7 | 異常系 | 最大長超過を拒否する | 101文字 | `tooLong(...)` |  |

## 5. 実装メモ

- `@DisplayName` 方針: `ReplyComment.of の blank 拒否を明示する`

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_createReplyComment_when_valueIsNormalText` | `ReplyComment.of は通常の本文を受け入れる` |
| 2 | `should_createReplyComment_when_valueContainsLineBreaks` | `ReplyComment.of は改行を含む本文を受け入れる` |
| 3 | `should_throwException_when_replyCommentIsNull` | `ReplyComment.of は null を拒否する` |
| 4 | `should_throwException_when_replyCommentIsBlank` | `ReplyComment.of は空白のみの本文を拒否する` |
| 5 | `should_throwException_when_replyCommentContainsControlCharacters` | `ReplyComment.of は改行以外の制御文字を拒否する` |
| 6 | `should_throwException_when_replyCommentExceedsMaxLength` | `ReplyComment.of は最大長を超える本文を拒否する` |
