# `Pixel` テスト仕様書

## 1. 基本情報

- 対象クラス: `Pixel`
- 対象メソッド: `of(int)`
- 対象パッケージ: `jp.i432kg.footprint.domain.value`
- 対応するテストクラス: `PixelTest`
- 作成者: Codex
- 作成日: 2026-04-15

## 2. 対象概要

- 何をする処理か: ピクセル数を表す汎用の値オブジェクトを生成する
- 入力: `int`
- 出力: `Pixel`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 範囲内 | 0 以上 100,000 以下の値を受け入れること |
| 2 | 境界値 | 下限・上限 | 0 と 100,000 を受け入れること |
| 3 | 異常系 | 下限未満 | 負の値を拒否すること |
| 4 | 異常系 | 上限超過 | 100,000 を超える値を拒否すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 範囲内の値を受け入れる | `1024` | 生成成功 |  |
| 2 | 境界値 | 下限値と上限値を受け入れる | `0`, `100000` | 生成成功 |  |
| 3 | 異常系 | 負の値を拒否する | `-1` | `outOfRange(...)` |  |
| 4 | 異常系 | 上限超過を拒否する | `100001` | `outOfRange(...)` |  |

## 5. 実装メモ

- `@DisplayName` 方針: `Pixel.of の範囲条件を日本語で記載する`

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_createPixel_when_valueIsWithinRange` | `Pixel.of は範囲内の値を受け入れる` |
| 2 | `should_createPixel_when_valueIsBoundary` | `Pixel.of は下限値 0 と上限値 100000 を受け入れる` |
| 3 | `should_throwException_when_pixelIsOutOfRange` | `Pixel.of は 0 未満の値を拒否する` |
| 4 | `should_throwException_when_pixelExceedsMaximum` | `Pixel.of は上限値 100000 を超える値を拒否する` |
