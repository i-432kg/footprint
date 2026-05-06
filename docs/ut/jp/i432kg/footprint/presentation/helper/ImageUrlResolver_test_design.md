# `ImageUrlResolver` テスト仕様書

## 1. 基本情報

- 対象クラス: `ImageUrlResolver`
- 対象パッケージ: `jp.i432kg.footprint.presentation.helper`
- 対応するテストクラス: なし（interface のため個別テストなし）
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をするクラスか: 保存先種別に応じて画像表示用 URL を解決する presentation 向けポート
- 主なメソッド: `resolve(StorageObject storageObject)`

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 共通観点 | non-null 契約 | `StorageObject` を non-null 前提で受け取り、実装側で URL を返すこと |
| 2 | 共通観点 | 保存種別ごとの差異 | LOCAL / S3 それぞれの実装で異なる URL 解決戦略を持つこと |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 共通観点 | interface 契約を実装で満たす | `StorageObject` を渡す | 実装側で URL が返る |

## 5. 実装メモ

- interface 自体はテスト対象ではなく、`LocalImageUrlResolverTest` / `S3ImageUrlResolverTest` で担保する

## 6. 対応するテストメソッド

- なし
