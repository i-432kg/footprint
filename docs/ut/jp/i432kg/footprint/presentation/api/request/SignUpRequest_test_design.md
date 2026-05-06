# `SignUpRequest` テスト仕様書

## 1. 基本情報

- 対象クラス: `SignUpRequest`
- 対象パッケージ: `jp.i432kg.footprint.presentation.api.request`
- 対応するテストクラス: `SignUpRequestTest`
- 作成者: Codex
- 作成日: 2026-04-18

## 2. 対象概要

- 何をするクラスか: サインアップ API の JSON リクエスト DTO
- 主な項目: `userName`, `email`, `password`, `birthDate`

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 最小正常系 | すべてのフィールドが制約を満たす場合に validation を通過すること |
| 2 | 異常系 | ユーザー名下限 | `userName` 3 文字を拒否すること |
| 3 | 異常系 | ユーザー名形式 | 空白を含む `userName` を拒否すること |
| 4 | 異常系 | メール形式 | 不正なメールアドレスを拒否すること |
| 5 | 異常系 | パスワード下限 | `password` 7 文字を拒否すること |
| 6 | 異常系 | パスワード形式 | 空白を含む `password` を拒否すること |
| 7 | 正常系 | 生年月日境界 | `birthDate` が当日の場合に validation を通過すること |
| 8 | 異常系 | 生年月日未来日 | `birthDate` が翌日の場合に拒否すること |
| 9 | 異常系 | 生年月日必須 | `birthDate=null` を拒否すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | 正常なサインアップ情報を受け入れる | 全項目が制約内 | violation なし |
| 2 | 異常系 | ユーザー名 3 文字を拒否する | `userName=3 文字` | `userName` に violation |
| 3 | 異常系 | ユーザー名の空白を拒否する | `userName=\"user name\"` | `userName` に violation |
| 4 | 異常系 | 不正メール形式を拒否する | `email=\"invalid\"` | `email` に violation |
| 5 | 異常系 | パスワード 7 文字を拒否する | `password=7 文字` | `password` に violation |
| 6 | 異常系 | パスワードの空白を拒否する | `password=\"Secret 12\"` | `password` に violation |
| 7 | 正常系 | 当日の生年月日を受け入れる | `birthDate=LocalDate.now()` | violation なし |
| 8 | 異常系 | 未来日の生年月日を拒否する | `birthDate=LocalDate.now().plusDays(1)` | `birthDate` に violation |
| 9 | 異常系 | `birthDate` 必須違反 | `birthDate=null` | `birthDate` に violation |

## 5. 実装メモ

- `birthDate` は `@PastOrPresent` に合わせて当日許容を明示的に確認する
- `userName`, `password` の形式は `ASCII_VISIBLE_NO_SPACE` を前提に空白含有で落とす
- 1 テスト 1 violation を基本とし、どの `propertyPath` が失敗したかを確認する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_passValidation_when_allFieldsAreValid` | `SignUpRequest は正常な入力の場合に検証を通過する` |
| 2 | `should_failValidation_when_userNameIsTooShort` | `SignUpRequest は userName が3文字の場合に検証エラーとなる` |
| 3 | `should_failValidation_when_userNameContainsSpace` | `SignUpRequest は userName に空白を含む場合に検証エラーとなる` |
| 4 | `should_failValidation_when_emailFormatIsInvalid` | `SignUpRequest は email が不正形式の場合に検証エラーとなる` |
| 5 | `should_failValidation_when_passwordIsTooShort` | `SignUpRequest は password が7文字の場合に検証エラーとなる` |
| 6 | `should_failValidation_when_passwordContainsSpace` | `SignUpRequest は password に空白を含む場合に検証エラーとなる` |
| 7 | `should_passValidation_when_birthDateIsToday` | `SignUpRequest は birthDate が当日の場合に検証を通過する` |
| 8 | `should_failValidation_when_birthDateIsFuture` | `SignUpRequest は birthDate が未来日の場合に検証エラーとなる` |
| 9 | `should_failValidation_when_birthDateIsNull` | `SignUpRequest は birthDate が null の場合に検証エラーとなる` |
