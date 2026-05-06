# `Post` テスト仕様書

## 1. 基本情報

- 対象クラス: `Post`
- 対象メソッド: `of(PostId, UserId, Image, PostComment, LocalDateTime)`
- 対象パッケージ: `jp.i432kg.footprint.domain.model`
- 対応するテストクラス: `PostTest`
- 作成者: Codex
- 作成日: 2026-04-16

## 2. 対象概要

- 何をする処理か: 投稿の主要属性を保持するドメインモデルを生成する
- 入力: `PostId`, `UserId`, `Image`, `PostComment`, `LocalDateTime`
- 出力: `Post`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 生成 | 入力値を保持した `Post` を生成できること |
| 2 | 正常系 | 保持値 | `postId`, `userId`, `image`, `caption`, `createdAt` をそのまま返すこと |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 投稿を生成する | 妥当な各値オブジェクトと日時 | 生成成功 |  |
| 2 | 正常系 | 入力値を保持する | 生成済み `Post` | getter が入力値と一致 |  |

## 5. 実装メモ

- 固定化が必要な値: `createdAt`
- `@DisplayName` 方針: `Post.of の生成と保持値確認を記載する`

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_createPost_when_valuesAreValid` | `Post.of は妥当な値から投稿を生成できる` |
| 2 | `should_keepGivenValues_when_postIsCreated` | `Post.of は入力した値を保持する` |
