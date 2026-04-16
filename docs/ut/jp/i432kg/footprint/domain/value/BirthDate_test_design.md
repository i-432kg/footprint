# `BirthDate` テスト仕様書

## 1. 基本情報

- 対象クラス: `BirthDate`
- 対象メソッド: `of(LocalDate)`
- 対象パッケージ: `jp.i432kg.footprint.domain.value`
- 対応するテストクラス: `BirthDateTest`
- 作成者: Codex
- 作成日: 2026-04-15

## 2. 対象概要

- 何をする処理か: 生年月日を表す値オブジェクトを生成する
- 入力: `LocalDate`
- 出力: `BirthDate`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 当日・過去日 | 当日および過去日で生成できること |
| 2 | 異常系 | 必須 | `null` を拒否すること |
| 3 | 境界値 | 未来日 | 翌日以降を拒否すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 当日を受け入れる | `LocalDate.now()` | 生成成功 |  |
| 2 | 正常系 | 過去日を受け入れる | `LocalDate.now().minusDays(1)` | 生成成功 |  |
| 3 | 異常系 | `null` を拒否する | `null` | `InvalidValueException.required("birthdate")` |  |
| 4 | 境界値 | 未来日を拒否する | `LocalDate.now().plusDays(1)` | `InvalidValueException.invalid(...)` |  |

## 5. 実装メモ

- 固定化が必要な値: `LocalDate.now()` 基準日
- `@DisplayName` 方針: `BirthDate.of の受入条件を日本語で記載する`

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_createBirthDate_when_valueIsToday` | `BirthDate.of は当日の生年月日を生成できる` |
| 2 | `should_createBirthDate_when_valueIsPastDate` | `BirthDate.of は過去日の生年月日を生成できる` |
| 3 | `should_throwException_when_birthDateIsNull` | `BirthDate.of は null を拒否する` |
| 4 | `should_throwException_when_birthDateIsFuture` | `BirthDate.of は未来日を拒否する` |
