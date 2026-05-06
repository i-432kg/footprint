# `BirthDate` テスト仕様書

## 1. 基本情報

- 対象クラス: `BirthDate`
- 対象メソッド: `of(LocalDate, LocalDate)`, `restore(LocalDate)`
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
| 1 | 正常系 | 当日・過去日 | `of(...)` は当日および過去日で生成できること |
| 2 | 異常系 | 必須 | `of(...)`, `restore(...)` ともに `null` を拒否すること |
| 3 | 境界値 | 未来日 | `of(...)` は翌日以降を拒否すること |
| 4 | 正常系 | 再構築 | `restore(...)` は未来日を含めて値をそのまま復元すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 当日を受け入れる | `today`, `today` | 生成成功 | `of(...)` |
| 2 | 正常系 | 過去日を受け入れる | `today.minusDays(1)`, `today` | 生成成功 | `of(...)` |
| 3 | 異常系 | `of(...)` は `null` を拒否する | `value=null` | `InvalidValueException.required("birthdate")` |  |
| 4 | 境界値 | `of(...)` は未来日を拒否する | `today.plusDays(1)`, `today` | `InvalidValueException.invalid(...)` |  |
| 5 | 正常系 | `restore(...)` は未来日を復元できる | `today.plusDays(1)` | 生成成功 | 未来日チェックなし |
| 6 | 異常系 | `restore(...)` は `null` を拒否する | `null` | `InvalidValueException.required("birthdate")` |  |

## 5. 実装メモ

- 固定化が必要な値: `LocalDate.now()` 基準日
- `@DisplayName` 方針: `BirthDate.of / BirthDate.restore の受入条件を日本語で記載する`

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_createBirthDate_when_valueIsToday` | `BirthDate.of は当日の生年月日を生成できる` |
| 2 | `should_createBirthDate_when_valueIsPastDate` | `BirthDate.of は過去日の生年月日を生成できる` |
| 3 | `should_throwException_when_birthDateIsNull` | `BirthDate.of は null を拒否する` |
| 4 | `should_throwException_when_birthDateIsFuture` | `BirthDate.of は未来日を拒否する` |
| 5 | `should_restoreBirthDate_when_valueIsFutureDate` | `BirthDate.restore は未来日の生年月日を復元できる` |
| 6 | `should_throwException_when_restoredBirthDateIsNull` | `BirthDate.restore は null を拒否する` |
