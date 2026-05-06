# `Image` テスト仕様書

## 1. 基本情報

- 対象クラス: `Image`
- 対象メソッド: `of(...)`, `hasLocation()`
- 対象パッケージ: `jp.i432kg.footprint.domain.model`
- 対応するテストクラス: `ImageTest`
- 作成者: Codex
- 作成日: 2026-04-16

## 2. 対象概要

- 何をする処理か: 画像メタデータと保存先を保持するドメインモデルを生成し、位置情報の有無を判定する
- 入力: `StorageObject`, `FileExtension`, `Byte`, `Pixel`, `Location`, `boolean`, `LocalDateTime`
- 出力: `Image`, `boolean`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 生成 | 妥当な画像情報から生成でき、入力値を保持すること |
| 2 | 正常系 | 位置情報あり | `Location.of(...)` の場合に `hasLocation()` が `true` を返すこと |
| 3 | 正常系 | 位置情報なし | `Location.unknown()` の場合に `hasLocation()` が `false` を返すこと |
| 4 | 境界値 | 総ピクセル数 | 40MP ちょうどは受け入れ、超過時は拒否すること |
| 5 | 異常系 | 辺の上限 | 幅または高さが 8192px を超える場合に `InvalidModelException` を送出すること |
| 6 | 異常系 | 解像度制約 | 短辺 320px 未満や総ピクセル数 40MP 超過で `InvalidModelException` を送出すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 妥当な画像情報で生成する | 幅 640, 高さ 480, 既知の `Location` | 生成成功し各値を保持する |  |
| 2 | 正常系 | 位置情報ありを判定する | `Location.of(...)` | `hasLocation() == true` |  |
| 3 | 正常系 | 位置情報なしを判定する | `Location.unknown()` | `hasLocation() == false` |  |
| 4 | 境界値 | 40MP ちょうどを受け入れる | 例: 6250 x 6400 | 生成成功 | 短辺 320 以上 |
| 5 | 異常系 | 横幅上限超過を拒否する | 幅 8193, 高さ 6400 | `InvalidModelException` |  |
| 6 | 異常系 | 高さ上限超過を拒否する | 幅 6400, 高さ 8193 | `InvalidModelException` |  |
| 7 | 異常系 | 総ピクセル数超過を拒否する | 例: 6251 x 6400 | `InvalidModelException` |  |
| 8 | 異常系 | 短辺不足を拒否する | 幅 319, 高さ 640 | `InvalidModelException` | 値オブジェクト差し替え前提のケース |

## 5. 実装メモ

- 固定化が必要な値: `takenAt`, 総ピクセル数境界に使う `Pixel`
- `@DisplayName` 方針: `Image.of の生成条件と hasLocation の状態判定を分けて記載する`
- 備考:
  - `Pixel` は汎用化し、0 未満のみを拒否する。画像表示品質に関わる 320px / 8192px 制約は `Image` に持たせる
  - `Image` の制約違反は値オブジェクトの `InvalidValueException` ではなく、ドメインモデルの不変条件違反として `InvalidModelException` を用いる

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_createImage_when_valuesAreValid` | `Image.of は妥当な画像情報から画像を生成できる` |
| 2 | `should_returnTrue_when_imageHasKnownLocation` | `Image.hasLocation は位置情報が既知の場合に true を返す` |
| 3 | `should_returnFalse_when_imageHasUnknownLocation` | `Image.hasLocation は位置情報が不明の場合に false を返す` |
| 4 | `should_createImage_when_totalPixelsIsAtLimit` | `Image.of は総ピクセル数が 40MP ちょうどの場合に生成できる` |
| 5 | `should_throwException_when_widthExceedsLimit` | `Image.of は横幅が 8192px を超える場合に例外を送出する` |
| 6 | `should_throwException_when_heightExceedsLimit` | `Image.of は高さが 8192px を超える場合に例外を送出する` |
| 7 | `should_throwException_when_totalPixelsExceedLimit` | `Image.of は総ピクセル数が 40MP を超える場合に例外を送出する` |
| 8 | `should_throwException_when_shortSideIsLessThanMinimum` | `Image.of は短辺が 320px 未満の場合に例外を送出する` |
