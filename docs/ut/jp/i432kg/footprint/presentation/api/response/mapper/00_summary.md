# `jp.i432kg.footprint.presentation.api.response.mapper` パッケージ UT仕様書サマリー

## 1. 対象概要

- 対象パッケージ: `jp.i432kg.footprint.presentation.api.response.mapper`
- 対象クラス: `PostResponseMapper`, `ReplyResponseMapper`, `UserProfileResponseMapper`
- 個別仕様書: 各クラスごとに `ファイル名_test_design.md` を作成

## 2. 横断観点

| No. | 観点 | 内容 |
|---|---|---|
| 1 | 単体変換 | 単一の query model を対応する response DTO に正しく写像すること |
| 2 | 一覧変換 | `fromList(...)` が要素順を維持して response 一覧へ変換すること |
| 3 | nullable 項目 | `parentReplyId`, `lat`, `lng` など nullable 項目を適切に引き継ぎ、位置情報未設定投稿では `lat/lng=null` の `location` オブジェクトを返すこと |
| 4 | 日時変換 | `LocalDateTime` を `ZoneOffset.UTC` の `OffsetDateTime` に変換すること |
| 5 | 補助依存 | `PostResponseMapper` では `ImageUrlResolver` の戻り値を `ImageResponse.url` に反映すること |

## 3. グルーピング方針

- 投稿レスポンス変換: `PostResponseMapper`
  - 画像一覧、位置情報、UTC 化、`ImageUrlResolver` 利用を確認する
- 返信レスポンス変換: `ReplyResponseMapper`
  - 親返信 ID の nullable と UTC 化を確認する
- ユーザープロフィール変換: `UserProfileResponseMapper`
  - 単純な項目写像と `null` 入力を確認する

## 4. テスト実装メモ

- `PostResponseMapper` は `ImageUrlResolver` をスタブして URL 解決結果を固定する
- 単一変換と一覧変換を分けて確認する
- `fromList(...)` は戻り順序が維持されることも合わせて確認する
- public API は `@NullMarked` 前提とし、`null` 入力の吸収はテスト対象にしない
