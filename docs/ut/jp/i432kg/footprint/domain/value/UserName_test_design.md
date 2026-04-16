# `UserName` テスト仕様書

## 1. 基本情報

- 対象クラス: `UserName`
- 対象メソッド: `of(String)`
- 対象パッケージ: `jp.i432kg.footprint.domain.value`
- 対応するテストクラス: `UserNameTest`
- 作成者: Codex
- 作成日: 2026-04-15

## 2. 対象概要

- 何をする処理か: ユーザー名を trim 後の長さと文字種で検証し、値オブジェクトを生成する
- 入力: `String`
- 出力: `UserName`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 正規化 | 前後空白を除去して保持すること |
| 2 | 正常系 | 長さ・文字種 | 4 文字以上 15 文字以下の ASCII 可視文字を受け入れること |
| 3 | 異常系 | 必須 | `null` を拒否すること |
| 4 | 異常系 | 長さ | 3文字以下、16文字以上を拒否すること |
| 5 | 異常系 | 文字種 | 空白を含む値、全角文字を拒否すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | trim して保持する | `"  user_01  "` | `user_01` を保持 |  |
| 2 | 正常系 | 最小長を受け入れる | 4文字 | 生成成功 |  |
| 3 | 正常系 | 最大長を受け入れる | 15文字 | 生成成功 |  |
| 4 | 異常系 | `null` を拒否する | `null` | `required("username")` |  |
| 5 | 異常系 | 長さ不足を拒否する | 3文字 | `outOfRange(...)` |  |
| 6 | 異常系 | 長さ超過を拒否する | 16文字 | `outOfRange(...)` |  |
| 7 | 異常系 | 空白を含む値を拒否する | `"ab c"` | `invalidFormat(...)` | trim 後内部空白あり |
| 8 | 異常系 | 全角文字を拒否する | `"ユーザー"` | `invalidFormat(...)` |  |

## 5. 実装メモ

- `@DisplayName` 方針: `UserName.of の trim と ASCII 制約を記載する`

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_trimUserName_when_valueHasLeadingOrTrailingSpaces` | `UserName.of は前後空白を除去して保持する` |
| 2 | `should_createUserName_when_valueLengthIsWithinRange` | `UserName.of は長さ範囲内の ASCII 可視文字を受け入れる` |
| 3 | `should_throwException_when_userNameIsNull` | `UserName.of は null を拒否する` |
| 4 | `should_throwException_when_userNameLengthIsOutOfRange` | `UserName.of は長さ範囲外の値を拒否する` |
| 5 | `should_throwException_when_userNameContainsUnsupportedCharacters` | `UserName.of は空白や全角文字を含む値を拒否する` |
