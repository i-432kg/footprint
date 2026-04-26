# `jp.i432kg.footprint.logging.masking` パッケージ UT仕様書サマリー

## 1. 対象概要

- 対象パッケージ: `jp.i432kg.footprint.logging.masking`
- 対象クラス: `MaskingStrategy`, `MaskingTarget`, `SensitiveDataMasker`
- 個別仕様書: 各クラスごとに `ファイル名_test_design.md` を作成

## 2. 横断観点

| No. | 観点 | 内容 |
|---|---|---|
| 1 | マスク方式 | 全文マスクとメールアドレス部分マスクが意図どおりに適用されること |
| 2 | ターゲット解決 | キー名や `target` 名から `PASSWORD`, `TOKEN`, `EMAIL`, `FILE` を正しく解決できること |
| 3 | 再帰マスク | `Map`, `List`, ネストした `Map` に含まれる機微情報を再帰的にマスクできること |
| 4 | `rejectedValue` 特例 | `details.target` を参照して `rejectedValue` のマスク要否を判定できること |
| 5 | 型ベース判定 | `RawPassword`, `HashedPassword`, `EmailAddress` をキー名に依存せずマスクできること |
| 6 | 不変性 | マスク後の `Map` が不変であり、元入力を破壊しないこと |

## 3. グルーピング方針

- マスク方式定義: `MaskingStrategy`
  - 戦略ごとの置換結果と境界値を確認する
- キーワード解決定義: `MaskingTarget`
  - キー名の部分一致、大小文字差、戦略委譲を確認する
- 実行クラス: `SensitiveDataMasker`
  - 再帰処理、型ベース判定、`rejectedValue` 特例、戻り値の不変性を確認する

## 4. テスト実装メモ

- モック化する依存: なし
- `MaskingStrategy` と `MaskingTarget` は enum のため、全定数を個別に確認する
- `SensitiveDataMasker` は `Map.of(...)` と `List.of(...)` を使い、ネスト構造を明示したテストデータを組み立てる
- `rejectedValue` は `context.target` に依存するため、`target=image.objectKey` など判定可能な値を含む `details` を用意する
- 現時点で実装済みのテストクラスは `SensitiveDataMaskerTest` のみ。`MaskingStrategyTest`, `MaskingTargetTest` は本仕様書に従って追加する
