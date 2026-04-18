# `PostRequest` テスト仕様書

## 1. 基本情報

- 対象クラス: `PostRequest`
- 対象パッケージ: `jp.i432kg.footprint.presentation.api.request`
- 対応するテストクラス: `PostRequestTest`
- 作成者: Codex
- 作成日: 2026-04-18

## 2. 対象概要

- 何をするクラスか: 投稿作成 API の multipart リクエスト DTO
- 主な項目: `imageFile`, `comment`

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 最小正常系 | 非空ファイルと未指定コメントで validation を通過すること |
| 2 | 異常系 | 画像必須 | `imageFile=null` を拒否すること |
| 3 | 異常系 | 空ファイル | 空ファイルを拒否すること |
| 4 | 正常系 | コメント境界 | `comment` 100 文字ちょうどを受け入れること |
| 5 | 異常系 | コメント長過ぎ | `comment` 101 文字を拒否すること |
| 6 | 異常系 | 制御文字 | `comment` に禁止制御文字を含む場合に拒否すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | 非空ファイルのみで受け入れる | `imageFile=非空`, `comment=null` | violation なし |
| 2 | 異常系 | `imageFile` 必須違反 | `imageFile=null` | `imageFile` に violation |
| 3 | 異常系 | 空ファイル違反 | `imageFile=空` | `imageFile` に violation |
| 4 | 正常系 | コメント 100 文字を受け入れる | `comment=100 文字` | violation なし |
| 5 | 異常系 | コメント 101 文字を拒否する | `comment=101 文字` | `comment` に violation |
| 6 | 異常系 | コメントの制御文字を拒否する | `comment=\"abc\\u0000\"` | `comment` に violation |

## 5. 実装メモ

- `MockMultipartFile` を使って空/非空を切り替える
- `comment` 未指定は許容なので `null` ケースを正常系に含める

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_passValidation_when_imageFileIsPresentAndCommentIsNull` | `PostRequest は非空ファイルがありコメント未指定の場合に検証を通過する` |
| 2 | `should_failValidation_when_imageFileIsNull` | `PostRequest は imageFile が null の場合に検証エラーとなる` |
| 3 | `should_failValidation_when_imageFileIsEmpty` | `PostRequest は空ファイルの場合に検証エラーとなる` |
| 4 | `should_passValidation_when_commentLengthIsMaxBoundary` | `PostRequest は comment が100文字ちょうどの場合に検証を通過する` |
| 5 | `should_failValidation_when_commentIsTooLong` | `PostRequest は comment が101文字の場合に検証エラーとなる` |
| 6 | `should_failValidation_when_commentContainsControlCharacters` | `PostRequest は comment に制御文字を含む場合に検証エラーとなる` |
