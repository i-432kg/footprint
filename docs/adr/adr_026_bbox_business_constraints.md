# ADR: bbox 検索は `BoundingBox` ドメインモデルで最小成立条件を検証する

## ステータス

Accepted

## 背景

Footprint の地図検索 API は `minLat`, `maxLat`, `minLng`, `maxLng` を受け取り、表示範囲内の投稿を検索する。

現状の実装では Controller と `Latitude` / `Longitude` 値オブジェクトにより、各座標値の数値範囲は検証できている。

一方で、4 つの値をまとめた bbox としての妥当性、たとえば次の業務ルールは表現できていない。

- `minLat <= maxLat`
- `minLng <= maxLng`

このコードベースでは、単一値を保持するものを値オブジェクトとし、それらを組み合わせて意味を持つものはドメインモデルとして扱っている。

そのため bbox も、`Latitude` / `Longitude` を組み合わせる `BoundingBox` ドメインモデルとして扱うのが設計上自然である。

## 決定

地図検索用 bbox は `domain.model.BoundingBox` として表現し、次の最小成立条件を Domain で検証する。

1. 各座標値の範囲検証は既存の `Latitude` / `Longitude` 値オブジェクトに委譲する
2. bbox としての複合ルールは `BoundingBox.of(...)` で検証する
3. 今回は `minLat <= maxLat` と `minLng <= maxLng` を必須ルールとする
4. 「過大範囲」の上限は現時点では採用せず、将来必要になった時点で追加する

## 理由

### 1. 単一値と複合ルールを分離できる

`Latitude` / `Longitude` は単一値の妥当性、`BoundingBox` は 4 値の関係性という役割に分けられる。

### 2. Controller の責務を最低限に保てる

Controller は HTTP 入力としての最低限の検証と Domain 変換だけを担い、業務ルールは Domain へ集約できる。

### 3. 既存のモデル分類と整合する

`Location` が `Latitude` / `Longitude` を持つドメインモデルであるのと同様に、bbox もドメインモデルとして扱うとコードベースの整理と合う。

## 不採用案

### 1. Controller で `min <= max` を検証する

不採用。

HTTP 入力処理に業務ルールが混ざり、Controller の責務が増える。

### 2. `BoundingBox` を値オブジェクトとして `domain.value` に置く

不採用。

このプロジェクトでは、単一値を値オブジェクト、複数の値オブジェクトを組み合わせるものをドメインモデルとして整理している。

### 3. bbox の最大面積や最大緯度差・経度差を今すぐ導入する

今回は不採用。

他サービスでも bbox の成立条件は厳密に扱う一方、用途ごとの広さ制約は別判断であることが多い。Footprint ではまず bbox の成立条件を優先する。

## 影響

### 良い影響

- bbox の業務ルールが Domain に集約される
- Controller と Service の引数が単純化される
- 将来、過大範囲制約を追加しやすい

### 注意点

- `PostRestController`, `PostQueryService`, `PostQueryMapper` など bbox を受け渡す境界をまとめて更新する必要がある
- `BoundingBox` を使うことで、既存テストと設計書も追随が必要になる

## 実装メモ

- `domain.model.BoundingBox` を追加する
- `PostRestController.searchMap(...)` は `BoundingBox.of(...)` を介して Domain へ変換する
- `PostQueryService.searchPostsByBBox(...)` は `BoundingBox` を受け取る
- `PostQueryMapper` / MyBatis XML は `BoundingBox` のネストプロパティを参照する
