# `NotEmptyFileValidator` テスト仕様書

## 1. 基本情報

- 対象クラス: `NotEmptyFileValidator`
- 対象パッケージ: `jp.i432kg.footprint.presentation.validation`
- 対応するテストクラス: `NotEmptyFileValidatorTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をするクラスか: `NotEmptyFile` の検証ロジックを提供する validator
- 主なメソッド: `isValid(MultipartFile value, ConstraintValidatorContext context)`

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 異常系 | `null` 入力 | `value=null` の場合に `false` を返すこと |
| 2 | 異常系 | 空ファイル | `MultipartFile.isEmpty()==true` の場合に `false` を返すこと |
| 3 | 正常系 | 非空ファイル | `MultipartFile.isEmpty()==false` の場合に `true` を返すこと |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 異常系 | `null` を拒否する | `value=null` | `false` |
| 2 | 異常系 | 空ファイルを拒否する | `value=空ファイル` | `false` |
| 3 | 正常系 | 非空ファイルを受け入れる | `value=非空ファイル` | `true` |

## 5. 実装メモ

- `ConstraintValidatorContext` は未使用なので `null` 渡しでよい
- `MockMultipartFile` を使って空/非空を切り替える

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_returnFalse_when_fileIsNull` | `NotEmptyFileValidator は null ファイルを拒否する` |
| 2 | `should_returnFalse_when_fileIsEmpty` | `NotEmptyFileValidator は空ファイルを拒否する` |
| 3 | `should_returnTrue_when_fileIsNotEmpty` | `NotEmptyFileValidator は非空ファイルを受け入れる` |
