# `jp.i432kg.footprint.presentation.api.response` パッケージ UT仕様書サマリー

## 1. 対象概要

- 対象パッケージ: `jp.i432kg.footprint.presentation.api.response`
- 対象クラス: `ImageResponse`, `LocationResponse`, `PostItemResponse`, `ReplyItemResponse`, `UserProfileItemResponse`
- 個別仕様書: 各クラスごとに `ファイル名_test_design.md` を作成

## 2. 横断観点

| No. | 観点 | 内容 |
|---|---|---|
| 1 | 保持値 | `of(...)` で渡した値を getter でそのまま取得できること |
| 2 | `@NullMarked` 契約 | package 全体は non-null 前提であり、nullable 項目だけ `@Nullable` で表現されていること |
| 3 | nullable 項目 | `LocationResponse.lat/lng`, `PostItemResponse.location`, `ReplyItemResponse.parentReplyId` が `null` を保持できること |
| 4 | 日時項目 | `OffsetDateTime` をそのまま保持できること |
| 5 | コレクション項目 | 画像一覧などのコレクションを順序どおり保持できること |

## 3. グルーピング方針

- 画像レスポンス: `ImageResponse`
  - 画像参照情報の保持を確認する
- 位置情報レスポンス: `LocationResponse`
  - nullable 座標の保持を確認する
- 投稿/返信レスポンス: `PostItemResponse`, `ReplyItemResponse`
  - nullable 項目と `OffsetDateTime` の保持を確認する
- ユーザープロフィールレスポンス: `UserProfileItemResponse`
  - 単純な項目保持を確認する

## 4. テスト実装メモ

- DTO 単体の UT とし、生成と getter の保持確認に絞る
- `null` が許容される項目だけ、明示的に `null` を渡すケースを持つ
- `@NullMarked` 配下の non-null 項目に対する `null` 入力は原則テスト対象にしない
