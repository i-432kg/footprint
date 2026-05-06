# `InvalidModelException` テスト仕様書

## 1. 基本情報

- 対象クラス: `InvalidModelException`
- 対象メソッド: `invalid(...)`
- 対象パッケージ: `jp.i432kg.footprint.domain.exception`
- 対応するテストクラス: `InvalidModelExceptionTest`
- 作成者: Codex
- 作成日: 2026-04-16

## 2. 対象概要

- 何をする処理か: ドメインモデル全体の不変条件違反を表す例外を生成する
- 入力: `target`, `rejectedValue`, `reason`
- 出力: `InvalidModelException`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | invalid | `ErrorCode.DOMAIN_INVALID_MODEL` と `target`, `reason`, `rejectedValue` を保持すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | invalid 例外を生成する | `target=image`, `rejectedValue=319`, `reason=short_side_pixels_too_small` | `DOMAIN_INVALID_MODEL`, `reason=short_side_pixels_too_small` |  |

## 5. 実装メモ

- モック化する依存: なし
- 固定化が必要な値: `target`, `rejectedValue`, `reason`
- `@DisplayName` 方針: `InvalidModelException.invalid の details と message を記載する`
- 備考:
  - `InvalidValueException` と異なり factory は `invalid(...)` のみ

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_createInvalidException_withExpectedDetails` | `InvalidModelException.invalid はモデル不変条件違反の details を持つ例外を生成する` |
