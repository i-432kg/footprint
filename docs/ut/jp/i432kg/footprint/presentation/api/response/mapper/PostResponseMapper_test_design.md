# `PostResponseMapper` テスト仕様書

## 1. 基本情報

- 対象クラス: `PostResponseMapper`
- 対象パッケージ: `jp.i432kg.footprint.presentation.api.response.mapper`
- 対応するテストクラス: `PostResponseMapperTest`
- 作成者: Codex
- 作成日: 2026-04-18

## 2. 対象概要

- 何をするクラスか: 投稿系 query model を API レスポンス DTO に変換する mapper
- 主な入力: `PostSummary`, `List<PostSummary>`
- 主な依存: `ImageUrlResolver`

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 単体変換 | `PostSummary` の全項目が `PostItemResponse` に正しく写像されること |
| 2 | 正常系 | URL 解決 | 各 `ImageSummary` から組み立てた `StorageObject` を使って `ImageUrlResolver` が呼ばれ、その戻り値が `url` に入ること |
| 3 | 正常系 | 日時変換 | `createdAt` が UTC オフセット付き `OffsetDateTime` に変換されること |
| 4 | 正常系 | 位置情報未設定 | `location=null` の場合に `lat/lng=null` の `LocationResponse` を返すこと |
| 5 | 正常系 | nullable 座標 | `LocationSummary.lat/lng` の `null` が `LocationResponse` に引き継がれること |
| 6 | 正常系 | 一覧変換 | `fromList(...)` が要素順を維持してレスポンス一覧へ変換すること |
| 7 | 正常系 | 一覧中 null 除外 | 一覧中の `null` 要素を除外すること |
| 8 | 正常系 | null 一覧 | `fromList(null)` が空リストを返すこと |
| 9 | 正常系 | null 入力 | `from(null)` が `null` を返すこと |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | 投稿をレスポンスへ変換する | 画像 1 件、位置情報あり、`createdAt` 指定 | 全項目が response に写像される |
| 2 | 正常系 | 画像 URL を resolver で解決する | `ImageUrlResolver.resolve(...)` が固定 URL を返す | `ImageResponse.url` に固定 URL が入る |
| 3 | 正常系 | `createdAt` を UTC 化する | `createdAt=2026-04-18T19:15:30` | `2026-04-18T19:15:30Z` になる |
| 4 | 正常系 | 位置情報なし投稿を変換する | `location=null`, `hasLocation=false` | `location.lat/lng=null` のオブジェクトを返す |
| 5 | 正常系 | nullable 座標を引き継ぐ | `lat=null`, `lng=null` | `LocationResponse.lat/lng` が `null` |
| 6 | 正常系 | 投稿一覧を変換する | `PostSummary` 2 件 | 2 件の response を順序維持で返す |
| 7 | 正常系 | 一覧中の `null` を除外する | `[summary, null]` | response 1 件のみ返す |
| 8 | 正常系 | `null` 一覧を空リストにする | `summaries=null` | 空リスト |
| 9 | 正常系 | `null` 投稿を受け取る | `summary=null` | `null` |

## 5. 実装メモ

- `ImageUrlResolver` はモックまたはスタブで固定 URL を返す
- `StorageObject` の詳細比較までは不要だが、resolver が画像件数分呼ばれることは確認する
- 画像一覧と位置情報の変換は、主要フィールドを response 側から確認する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_mapPostSummaryToResponse_when_summaryHasAllFields` | `PostResponseMapper は投稿サマリーの全項目をレスポンスへ変換する` |
| 2 | `should_resolveImageUrl_when_mappingImageSummary` | `PostResponseMapper は画像 URL を ImageUrlResolver で解決する` |
| 3 | `should_convertCreatedAtToUtcOffsetDateTime_when_mappingPostSummary` | `PostResponseMapper は createdAt を UTC オフセット付き日時へ変換する` |
| 4 | `should_returnNullFieldLocationObject_when_postHasNoLocation` | `PostResponseMapper は位置情報なし投稿を null 項目入りの location オブジェクトとして変換する` |
| 5 | `should_preserveNullableCoordinates_when_locationSummaryHasNullValues` | `PostResponseMapper は nullable な緯度経度をそのまま引き継ぐ` |
| 6 | `should_mapPostSummaryList_when_summariesArePresent` | `PostResponseMapper は投稿一覧を順序を保って変換する` |
| 7 | `should_filterOutNullSummaries_when_mappingPostSummaryList` | `PostResponseMapper は一覧中の null 要素を除外して変換する` |
| 8 | `should_returnEmptyList_when_postSummaryListIsNull` | `PostResponseMapper は null の一覧入力に対して空リストを返す` |
| 9 | `should_returnNull_when_postSummaryIsNull` | `PostResponseMapper は null の投稿サマリー入力に対して null を返す` |
