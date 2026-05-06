# `jp.i432kg.footprint.infrastructure.datasource.repository` パッケージ UT仕様書サマリー

## 1. 対象概要

- 対象パッケージ: `jp.i432kg.footprint.infrastructure.datasource.repository`
- 対象クラス: `PostRepositoryImpl`, `ReplyRepositoryImpl`, `UserRepositoryImpl`
- 個別仕様書: 各クラスごとに `ファイル名_test_design.md` を作成

## 2. 横断観点

| No. | 観点 | 内容 |
|---|---|---|
| 1 | Mapper 委譲 | repository が受け取った domain model / value object を対応する mapper 呼び出しへ正しく委譲すること |
| 2 | 変換 | save 系では mapper 用 entity へ変換して渡し、find 系では mapper 戻り値を domain model へ変換すること |
| 3 | 永続化順序 | 複数 mapper 呼び出しがある場合、期待する順序で実行されること |
| 4 | 例外再送出 | mapper が `RuntimeException` を送出した場合、repository が握りつぶさず再送出すること |
| 5 | Clock 利用 | `UserRepositoryImpl` は `Clock` を用いた `UserInsertEntity` 生成結果を mapper へ渡すこと |

## 3. グルーピング方針

- 投稿永続化: `PostRepositoryImpl`
  - 存在確認、投稿保存、画像保存、保存順序、例外再送出を確認する
- 返信永続化: `ReplyRepositoryImpl`
  - 返信検索、返信保存、子返信数更新、domain 変換、例外再送出を確認する
- ユーザー永続化: `UserRepositoryImpl`
  - 存在確認、メール重複確認、保存時の `Clock` 利用、例外再送出を確認する

## 4. テスト実装メモ

- モック化する依存:
  - `PostMapper`
  - `ReplyMapper`
  - `UserMapper`
  - `UserRepositoryImpl` では `Clock`
- 固定化する値:
  - `Post`, `Reply`, `User`
  - `PostId`, `ReplyId`, `UserId`, `EmailAddress`
  - `Clock.fixed(...)` による現在時刻
- save 系は mapper に渡す引数を `ArgumentCaptor` で取得し、生成 entity の中身を検証する
- 複数 mapper 呼び出しがある `PostRepositoryImpl.savePost` は `InOrder` で順序を確認する
- ログ出力自体は UT の主対象にせず、例外再送出と副作用の有無を優先して確認する
