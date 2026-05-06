# `PostDomainService` テスト仕様書

## 1. 基本情報

- 対象クラス: `PostDomainService`
- 対象メソッド: `isExistPost(PostId)`
- 対象パッケージ: `jp.i432kg.footprint.domain.service`
- 対応するテストクラス: `PostDomainServiceTest`
- 作成者: Codex
- 作成日: 2026-04-16

## 2. 対象概要

- 何をする処理か: 投稿の存在有無を repository 経由で判定する
- 入力: `PostId`
- 出力: `boolean`
- 主な副作用: `PostRepository.existsById(...)` 呼び出し

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 存在あり | repository が `true` を返した場合に `true` を返すこと |
| 2 | 正常系 | 存在なし | repository が `false` を返した場合に `false` を返すこと |
| 3 | 正常系 | 委譲 | 入力した `PostId` をそのまま repository に渡すこと |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | repository が存在ありを返す | `PostId`, `existsById(...) -> true` | `true` を返す |  |
| 2 | 正常系 | repository が存在なしを返す | `PostId`, `existsById(...) -> false` | `false` を返す |  |
| 3 | 正常系 | 入力した `PostId` を repository に渡す | `PostId` | 同じ引数で `existsById(...)` が呼ばれる | verify 観点 |

## 5. 実装メモ

- モック化する依存: `PostRepository`
- 固定化が必要な値: 妥当な `PostId`
- `@DisplayName` 方針: `PostDomainService.isExistPost の返却値と委譲を記載する`

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_returnTrue_when_postExists` | `PostDomainService.isExistPost は repository が true を返す場合に true を返す` |
| 2 | `should_returnFalse_when_postDoesNotExist` | `PostDomainService.isExistPost は repository が false を返す場合に false を返す` |
| 3 | `should_delegateToRepositoryWithGivenPostId_when_checkingPostExistence` | `PostDomainService.isExistPost は入力した PostId を repository に渡す` |
