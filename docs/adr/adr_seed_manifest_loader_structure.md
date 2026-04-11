# ADR: seed manifest loader は環境別に分け、manifest 解析は shared parser に集約する

## ステータス
Accepted

## 背景
`LocalSeedImageManifestLoader` と `SeedImageManifestLoader` は対になる構成だが、`SeedImageManifestLoader` は名前に環境が表れていない一方で、実装は `stg` 固有の `S3SeedSourceImageProvider` と `StgSeedProperties` に依存していた。

その結果、以下の問題があった。

- `SeedImageManifestLoader` が共通部品のように見えるが、実際は `stg` 専用である
- manifest の取得責務と JSON 解析責務が 1 クラスに混在している
- `shared` package に置ける候補かどうかが曖昧になり、package 分割方針がぶれる

## 決定
1. `SeedImageManifestLoader` は `StgSeedImageManifestLoader` に改名する
2. 環境差分のある loader は `local` / `stg` package に置く
3. manifest JSON の解析処理は `shared.SeedImageManifestParser` に切り出す
4. loader は manifest の取得と、環境固有の後処理だけを担当する

## 理由

- `LocalSeedImageManifestLoader` と `StgSeedImageManifestLoader` が対になり、責務が名前から分かる
- S3 取得やローカルファイル読み込みは環境依存なので loader に残すのが自然
- JSON 解析は環境に依存しないため、`shared` に置いて再利用するのが適切
- 共通化の対象を loader 全体ではなく parser に限定することで、無理な抽象化を避けられる

## 運用ルール

- 環境固有の manifest 取得ロジックは `local` / `stg` の loader に置く
- manifest のフォーマット解釈は `shared.SeedImageManifestParser` に集約する
- 新しい環境が増える場合も、まず環境別 loader を追加し、必要な共通化だけ `shared` へ寄せる

## この方針で避けるもの

- 環境依存を隠した曖昧なクラス名
- 取得責務と解析責務の混在
- `shared` package に環境依存を持ち込むこと
