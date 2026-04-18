# `NotEmptyFile` テスト仕様書

## 1. 基本情報

- 対象クラス: `NotEmptyFile`
- 対象パッケージ: `jp.i432kg.footprint.presentation.validation`
- 対応するテストクラス: `NotEmptyFileTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をするクラスか: `MultipartFile` が `null` でも空でもないことを検証する Bean Validation annotation
- 主な要素: `message`, `groups`, `payload`, `validatedBy`

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | デフォルトメッセージ | `message()` の初期値が想定どおりであること |
| 2 | 正常系 | validator 関連付け | `@Constraint(validatedBy=NotEmptyFileValidator.class)` が設定されていること |
| 3 | 正常系 | annotation 対象 | `FIELD` 対象、`RUNTIME` 保持であること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | デフォルトメッセージを確認する | annotation を参照 | `"ファイルを選択してください"` |
| 2 | 正常系 | validator 関連付けを確認する | annotation を参照 | `validatedBy=NotEmptyFileValidator.class` |
| 3 | 正常系 | annotation メタ情報を確認する | annotation を参照 | `FIELD` / `RUNTIME` |

## 5. 実装メモ

- reflection で annotation のメタデータを取得して確認する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_haveDefaultMessage_when_annotationIsRead` | `NotEmptyFile は想定どおりのデフォルトメッセージを持つ` |
| 2 | `should_referenceNotEmptyFileValidator_when_annotationIsRead` | `NotEmptyFile は NotEmptyFileValidator を validator として参照する` |
| 3 | `should_targetFieldAndRuntime_when_annotationMetadataIsRead` | `NotEmptyFile は FIELD 対象かつ RUNTIME 保持である` |
