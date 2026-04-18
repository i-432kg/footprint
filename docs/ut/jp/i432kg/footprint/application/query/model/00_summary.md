# `jp.i432kg.footprint.application.query.model` パッケージ UT仕様書サマリー

## 1. 対象概要

- 対象パッケージ: `jp.i432kg.footprint.application.query.model`
- 対象クラス: `ImageSummary`, `LocationSummary`, `PostSummary`, `ReplySummary`, `UserProfileSummary`
- 個別仕様書: 各クラスごとに `ファイル名_test_design.md` を作成

## 2. 横断観点

| No. | 観点 | 内容 |
|---|---|---|
| 1 | 保持値 | Lombok `@Value` により、設定された各フィールドを getter でそのまま返すこと |
| 2 | nullable 項目 | `@NullMarked` 配下でも `@Nullable` を付けた項目だけは `null` を保持できること |
| 3 | 既定値 | `@NoArgsConstructor(force = true)` により、フレームワーク再構築時の既定値が設定されること |
| 4 | 入れ子モデル | `PostSummary.images`, `PostSummary.location` のような入れ子構造がそのまま保持されること |

## 3. グルーピング方針

- 投稿系: `PostSummary`
  - 投稿の基本情報、位置情報有無、入れ子の画像・位置情報を確認する
- 返信系: `ReplySummary`
  - 親返信なしを含む nullable 項目と件数情報を確認する
- ユーザー系: `UserProfileSummary`
  - 基本プロフィールと集計値を確認する
- 補助モデル: `ImageSummary`, `LocationSummary`
  - query model から参照される保持値と nullable 項目を確認する

## 4. テスト実装メモ

- `query.model` は参照専用データキャリアのため、主観点は保持値確認と nullable 項目確認に絞る
- 生成は `new` ではなく、テスト側で reflection または builder 補助を使わず、`@NoArgsConstructor(force = true)` とフィールド設定方針に合わせて実装方法を決める
- `LocationSummary.lat/lng`, `PostSummary.location`, `ReplySummary.parentReplyId` は `null` ケースを明示的に確認する
- `PostSummary.hasLocation` と `location` の組み合わせは、現行実装では整合性ロジックを持たないため保持値確認のみとする
