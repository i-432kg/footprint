# `ReplyRequest` テスト仕様書

## 1. 基本情報

- 対象クラス: `ReplyRequest`
- 対象パッケージ: `jp.i432kg.footprint.presentation.api.request`
- 対応するテストクラス: `ReplyRequestTest`
- 作成者: Codex
- 作成日: 2026-04-18

## 2. 対象概要

- 何をするクラスか: 返信作成 API の JSON リクエスト DTO
- 主な項目: `parentReplyId`, `message`

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | ルート返信 | `parentReplyId=null` で validation を通過すること |
| 2 | 正常系 | ネスト返信 | `parentReplyId` が ULID 形式なら validation を通過すること |
| 3 | 異常系 | 親返信ID形式 | `parentReplyId` が ULID 形式でない場合に拒否すること |
| 4 | 異常系 | 本文必須 | `message` が blank の場合に拒否すること |
| 5 | 正常系 | 本文境界 | `message` 100 文字ちょうどを受け入れること |
| 6 | 異常系 | 本文長過ぎ | `message` 101 文字を拒否すること |
| 7 | 異常系 | 制御文字 | `message` に禁止制御文字を含む場合に拒否すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | ルート返信を受け入れる | `parentReplyId=null`, `message=正常値` | violation なし |
| 2 | 正常系 | ULID 形式の親返信 ID を受け入れる | `parentReplyId=ULID` | violation なし |
| 3 | 異常系 | 不正な親返信 ID を拒否する | `parentReplyId=invalid` | `parentReplyId` に violation |
| 4 | 異常系 | blank 本文を拒否する | `message=\"   \"` | `message` に violation |
| 5 | 正常系 | 本文 100 文字を受け入れる | `message=100 文字` | violation なし |
| 6 | 異常系 | 本文 101 文字を拒否する | `message=101 文字` | `message` に violation |
| 7 | 異常系 | 本文の制御文字を拒否する | `message=\"abc\\u0000\"` | `message` に violation |

## 5. 実装メモ

- `parentReplyId` は `null` 許容なので、未指定ケースを正常系に含める
- `message` の blank は `@NotBlank`、長さは `@Size`、制御文字は `@Pattern` で検証する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_passValidation_when_parentReplyIdIsNull` | `ReplyRequest は parentReplyId 未指定のルート返信で検証を通過する` |
| 2 | `should_passValidation_when_parentReplyIdIsUlid` | `ReplyRequest は ULID 形式の parentReplyId の場合に検証を通過する` |
| 3 | `should_failValidation_when_parentReplyIdIsInvalidFormat` | `ReplyRequest は parentReplyId が不正形式の場合に検証エラーとなる` |
| 4 | `should_failValidation_when_messageIsBlank` | `ReplyRequest は message が blank の場合に検証エラーとなる` |
| 5 | `should_passValidation_when_messageLengthIsMaxBoundary` | `ReplyRequest は message が100文字ちょうどの場合に検証を通過する` |
| 6 | `should_failValidation_when_messageIsTooLong` | `ReplyRequest は message が101文字の場合に検証エラーとなる` |
| 7 | `should_failValidation_when_messageContainsControlCharacters` | `ReplyRequest は message に制御文字を含む場合に検証エラーとなる` |
