# `jp.i432kg.footprint.domain.helper` パッケージ UT仕様書サマリー

## 1. 対象概要

- 対象パッケージ: `jp.i432kg.footprint.domain.helper`
- 対象クラス: `CoordinateValidation`, `UlidValidation`
- 個別仕様書: 各クラスごとに `ファイル名_test_design.md` を作成

## 2. 横断観点

| No. | 観点 | 内容 |
|---|---|---|
| 1 | 必須チェック | `@Nullable` 引数に `null` が渡された場合に `InvalidValueException.required(...)` を送出すること |
| 2 | 形式・範囲検証 | ULID 形式や数値範囲など、ヘルパーが担当する共通検証を正しく行うこと |
| 3 | 正規化 | 検証後に返す値が丸めなどの正規化結果になっていること |
| 4 | 境界値 | 許容最小値・最大値ちょうどと、その直前直後の扱いを確認すること |
| 5 | 例外内容 | `InvalidValueException` の reason が観点に応じて `required`, `blank`, `invalid_format`, `out_of_range` になること |

## 3. グルーピング方針

- 数値検証系: `CoordinateValidation`
  - `null`、HALF_UP による丸め、最小値・最大値境界、範囲外を確認する
- 文字列検証系: `UlidValidation`
  - `null`、blank、ULID 形式不正、妥当な ULID の受け入れを確認する

## 4. テスト実装メモ

- モック化する依存: なし
- `CoordinateValidation` は `BigDecimal` の scale と境界比較を明示して検証する
- `CoordinateValidation` の範囲外判定は、丸め後の値で比較・例外生成される点を確認対象に含める
- `UlidValidation` は `blank` と `invalid_format` を分けて確認する
- 各テストメソッドには `@DisplayName` を付与し、日本語の見出しで観点を明示する
