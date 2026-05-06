# `UlidValidation` テスト仕様書

## 1. 基本情報

- 対象クラス: `UlidValidation`
- 対象メソッド: `requireValidUlid(String, String)`
- 対象パッケージ: `jp.i432kg.footprint.domain.helper`
- 対応するテストクラス: `UlidValidationTest`
- 作成者: Codex
- 作成日: 2026-04-16

## 2. 対象概要

- 何をする処理か: ID 系値オブジェクトで共通となる ULID 文字列の必須チェック、blank チェック、形式検証を行う
- 入力: `fieldName`, `String`
- 出力: 検証済みの `String`
- 主な副作用: 条件に応じて `InvalidValueException` を送出する

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 妥当な ULID | 26 文字の ULID 形式文字列をそのまま返すこと |
| 2 | 異常系 | 必須 | `value == null` の場合に `required(...)` を送出すること |
| 3 | 異常系 | blank | 空文字や空白のみ文字列の場合に `blank(...)` を送出すること |
| 4 | 異常系 | 形式不正 | 長さ不足、長さ超過、小文字、禁止文字を含む場合に `invalidFormat(...)` を送出すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 妥当な ULID を受け入れる | `01ARZ3NDEKTSV4RRFFQ69G5FAV` | 同じ文字列を返す |  |
| 2 | 異常系 | `null` を拒否する | `value=null` | `required(fieldName)` |  |
| 3 | 異常系 | 空文字を拒否する | `value=\"\"` | `blank(fieldName)` |  |
| 4 | 異常系 | 空白のみを拒否する | `value=\"   \"` | `blank(fieldName)` | `isBlank()` |
| 5 | 異常系 | 長さ不足を拒否する | 25 文字 | `invalidFormat(...)` |  |
| 6 | 異常系 | 長さ超過を拒否する | 27 文字 | `invalidFormat(...)` |  |
| 7 | 異常系 | 小文字を含む値を拒否する | `01arZ3NDEKTSV4RRFFQ69G5FAV` | `invalidFormat(...)` |  |
| 8 | 異常系 | 禁止文字を含む値を拒否する | `01ARZ3NDEKTSV4RRFFQ69G5FAI` | `invalidFormat(...)` | `I`, `L`, `O`, `U` は不許可 |

## 5. 実装メモ

- モック化する依存: なし
- 固定化が必要な値: 妥当な ULID 文字列
- `@DisplayName` 方針: `UlidValidation.requireValidUlid の検証条件を入力別に記載する`
- 備考:
  - blank 判定は `isBlank()` を使うため、空白のみ入力も `blank` になる
  - 形式検証は `^[0-9A-HJKMNP-TV-Z]{26}$` に一致するかで判定する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_returnValue_when_valueIsValidUlid` | `UlidValidation.requireValidUlid は妥当な ULID をそのまま返す` |
| 2 | `should_throwException_when_valueIsNull` | `UlidValidation.requireValidUlid は null を拒否する` |
| 3 | `should_throwException_when_valueIsBlank` | `UlidValidation.requireValidUlid は空文字と空白のみ文字列を拒否する` |
| 4 | `should_throwException_when_valueHasInvalidFormat` | `UlidValidation.requireValidUlid は ULID 形式でない値を拒否する` |
