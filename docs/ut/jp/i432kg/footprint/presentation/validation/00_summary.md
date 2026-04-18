# `jp.i432kg.footprint.presentation.validation` パッケージ UT仕様書サマリー

## 1. 対象概要

- 対象パッケージ: `jp.i432kg.footprint.presentation.validation`
- 対象クラス: `NotEmptyFile`, `NotEmptyFileValidator`, `PresentationValidationPatterns`
- 個別仕様書: 各クラスごとに `ファイル名_test_design.md` を作成

## 2. 横断観点

| No. | 観点 | 内容 |
|---|---|---|
| 1 | Bean Validation 構成 | annotation と validator の関連付けが想定どおりであること |
| 2 | 入力判定 | `null` / 空ファイル / 非空ファイルの扱いが明確であること |
| 3 | 定数定義 | 正規表現定数が意図した文字種・形式を表していること |

## 3. グルーピング方針

- annotation 定義: `NotEmptyFile`
  - `message`, `groups`, `payload`, `validatedBy` を確認する
- validator 実装: `NotEmptyFileValidator`
  - `isValid(...)` の判定を確認する
- パターン定数: `PresentationValidationPatterns`
  - ULID、制御文字禁止、ASCII 可視文字制約を確認する

## 4. テスト実装メモ

- `NotEmptyFile` は reflection で annotation メタ情報を確認する
- `NotEmptyFileValidator` は `MockMultipartFile` を使って空/非空を切り替える
- 正規表現定数は値そのもの固定でもよいが、実際のマッチ/ミスマッチを 1-2 件確認する
