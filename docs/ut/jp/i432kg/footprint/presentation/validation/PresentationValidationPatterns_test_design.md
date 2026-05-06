# `PresentationValidationPatterns` テスト仕様書

## 1. 基本情報

- 対象クラス: `PresentationValidationPatterns`
- 対象パッケージ: `jp.i432kg.footprint.presentation.validation`
- 対応するテストクラス: `PresentationValidationPatternsTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をするクラスか: presentation 層の Bean Validation で利用する正規表現定義
- 主な定数: `ULID`, `NO_CONTROL_CHARS`, `ASCII_VISIBLE_NO_SPACE`

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | ULID 形式 | 26 文字の大文字 ULID がマッチすること |
| 2 | 異常系 | ULID 不正 | 小文字や長さ不正がマッチしないこと |
| 3 | 正常系 | 制御文字禁止 | 通常文字列が `NO_CONTROL_CHARS` にマッチすること |
| 4 | 異常系 | 制御文字拒否 | `\\u0000` などの制御文字を含む文字列がマッチしないこと |
| 5 | 正常系 | ASCII 可視文字 | 空白なし ASCII 可視文字が `ASCII_VISIBLE_NO_SPACE` にマッチすること |
| 6 | 異常系 | 空白拒否 | 空白を含む文字列が `ASCII_VISIBLE_NO_SPACE` にマッチしないこと |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | ULID にマッチする | `01ARZ3NDEKTSV4RRFFQ69G5FAV` | `matches=true` |
| 2 | 異常系 | 不正 ULID を拒否する | `01arz3...` または短い値 | `matches=false` |
| 3 | 正常系 | 通常文字列を受け入れる | `hello world` | `NO_CONTROL_CHARS` にマッチ |
| 4 | 異常系 | 制御文字を拒否する | `abc\\u0000` | `NO_CONTROL_CHARS` にマッチしない |
| 5 | 正常系 | ASCII 可視文字を受け入れる | `Secret12!` | `ASCII_VISIBLE_NO_SPACE` にマッチ |
| 6 | 異常系 | 空白を拒否する | `Secret 12` | `ASCII_VISIBLE_NO_SPACE` にマッチしない |

## 5. 実装メモ

- `String.matches(...)` で十分
- 定数文字列そのものより、利用者視点のマッチ/ミスマッチを優先して確認する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_matchUlidPattern_when_valueIsValidUlid` | `PresentationValidationPatterns.ULID は妥当な ULID にマッチする` |
| 2 | `should_notMatchUlidPattern_when_valueIsInvalidUlid` | `PresentationValidationPatterns.ULID は不正な ULID にマッチしない` |
| 3 | `should_matchNoControlCharsPattern_when_valueHasNoControlCharacters` | `PresentationValidationPatterns.NO_CONTROL_CHARS は通常文字列にマッチする` |
| 4 | `should_notMatchNoControlCharsPattern_when_valueContainsControlCharacter` | `PresentationValidationPatterns.NO_CONTROL_CHARS は制御文字を含む文字列にマッチしない` |
| 5 | `should_matchAsciiVisibleNoSpacePattern_when_valueHasVisibleAsciiWithoutSpaces` | `PresentationValidationPatterns.ASCII_VISIBLE_NO_SPACE は空白なし ASCII 可視文字にマッチする` |
| 6 | `should_notMatchAsciiVisibleNoSpacePattern_when_valueContainsSpace` | `PresentationValidationPatterns.ASCII_VISIBLE_NO_SPACE は空白を含む文字列にマッチしない` |
