# `BoundingBox` テスト仕様書

## 1. 基本情報

- 対象クラス: `BoundingBox`
- 対象メソッド: `of(BigDecimal, BigDecimal, BigDecimal, BigDecimal)`, `of(Latitude, Latitude, Longitude, Longitude)`
- 対象パッケージ: `jp.i432kg.footprint.domain.model`
- 対応するテストクラス: `BoundingBoxTest`
- 作成者: Codex
- 作成日: 2026-04-25

## 2. 対象概要

- 何をする処理か: bbox 検索に利用する境界ボックスを表し、緯度経度の個別妥当性は値オブジェクトへ委譲しつつ、`min <= max` の複合ルールを検証する
- 入力: `BigDecimal`, `Latitude`, `Longitude`
- 出力: `BoundingBox`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 境界ボックス生成 | 妥当な座標値から `BoundingBox` を生成できること |
| 2 | 異常系 | 緯度範囲逆転 | `minLat > maxLat` のとき `InvalidModelException` を送出すること |
| 3 | 異常系 | 経度範囲逆転 | `minLng > maxLng` のとき `InvalidModelException` を送出すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 妥当な境界ボックスを生成する | `minLat <= maxLat`, `minLng <= maxLng` | 各値を保持した `BoundingBox` を返す |  |
| 2 | 異常系 | 緯度範囲が逆転している場合に例外を送出する | `minLat > maxLat` | `InvalidModelException` を送出する | `reason=min_lat_gt_max_lat` |
| 3 | 異常系 | 経度範囲が逆転している場合に例外を送出する | `minLng > maxLng` | `InvalidModelException` を送出する | `reason=min_lng_gt_max_lng` |

## 5. 実装メモ

- 固定化が必要な値: 妥当な `Latitude`, `Longitude` と、範囲逆転用の座標値
- `@DisplayName` 方針: bbox の成立条件と失敗理由を日本語で明記する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_createBoundingBox_when_valuesAreValid` | `BoundingBox.of は妥当な境界ボックスを生成する` |
| 2 | `should_throwException_when_minLatIsGreaterThanMaxLat` | `BoundingBox.of は minLat が maxLat を超える場合に例外を送出する` |
| 3 | `should_throwException_when_minLngIsGreaterThanMaxLng` | `BoundingBox.of は minLng が maxLng を超える場合に例外を送出する` |
