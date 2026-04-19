# `jp.i432kg.footprint.infrastructure.datasource.query` パッケージ UT仕様書サマリー

## 1. 対象概要

- 対象パッケージ: `jp.i432kg.footprint.infrastructure.datasource.query`
- 対象クラス: `PostQueryServiceImpl`, `ReplyQueryServiceImpl`, `UserQueryServiceImpl`
- 個別仕様書: 各クラスごとに `ファイル名_test_design.md` を作成

## 2. 横断観点

| No. | 観点 | 内容 |
|---|---|---|
| 1 | Mapper 委譲 | service が受け取った引数を変形せず対応する mapper へ委譲すること |
| 2 | 戻り値透過 | mapper の `List` / `Optional` / 参照モデルをそのまま返すこと |
| 3 | 例外変換 | `getPost`, `getUserProfile` は `Optional.empty()` を `PostNotFoundException` / `UserNotFoundException` へ変換すること |
| 4 | 副作用なし | 参照系 service として追加の更新処理や余分な mapper 呼び出しを行わないこと |
| 5 | nullable 引数 | `lastId` の `null` をそのまま mapper に渡し、ページング開始条件として扱えること |

## 3. グルーピング方針

- 投稿参照: `PostQueryServiceImpl`
  - 一覧取得、キーワード検索、BBox 検索、詳細取得、`Optional` と例外変換を確認する
- 返信参照: `ReplyQueryServiceImpl`
  - トップレベル返信、子返信、自分の返信一覧の mapper 委譲を確認する
- ユーザー参照: `UserQueryServiceImpl`
  - プロフィール検索と `UserNotFoundException` 変換を確認する

## 4. テスト実装メモ

- モック化する依存:
  - `PostQueryMapper`
  - `ReplyQueryMapper`
  - `UserQueryMapper`
- 固定化する値:
  - `PostSummary`, `ReplySummary`, `UserProfileSummary`
  - `PostId`, `ReplyId`, `UserId`, `SearchKeyword`, `Latitude`, `Longitude`
- `List` は件数と同一インスタンス性までは不要で、期待要素がそのまま返ることを確認する
- `getPost`, `getUserProfile` は `findXxx` を直接 spy せず、mapper 戻り値に基づく公開メソッドの振る舞いで確認する
- 各テストメソッドには `@DisplayName` を付与し、日本語でサービス名と観点を明示する
