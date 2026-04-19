# `jp.i432kg.footprint.infrastructure.id` パッケージ UT仕様書サマリー

## 1. 対象概要

- 対象パッケージ: `jp.i432kg.footprint.infrastructure.id`
- 対象クラス: `UlidImageIdGenerator`, `UlidPostIdGenerator`, `UlidReplyIdGenerator`, `UlidUserIdGenerator`
- 個別仕様書: 各クラスごとに `ファイル名_test_design.md` を作成

## 2. 横断観点

| No. | 観点 | 内容 |
|---|---|---|
| 1 | 値オブジェクト生成 | `generate()` が対応する ID 値オブジェクト型を返すこと |
| 2 | 文字列表現 | 生成される ID 文字列が値オブジェクトのバリデーションを満たす ULID 形式であること |
| 3 | 空値防止 | 生成結果の文字列が `null` / 空文字ではないこと |
| 4 | 呼び出し独立性 | 連続呼び出しで毎回値オブジェクトが返り、少なくとも同一参照ではないこと |

## 3. グルーピング方針

- 投稿 ID 生成: `UlidPostIdGenerator`
- 返信 ID 生成: `UlidReplyIdGenerator`
- ユーザー ID 生成: `UlidUserIdGenerator`
- 画像 ID 生成: `UlidImageIdGenerator`

各クラスとも責務は同じで、対応する値オブジェクト型だけが異なるため、同じ観点で揃えて検証する。

## 4. テスト実装メモ

- `UlidCreator` 自体はモック化せず、実際に `generate()` を呼んで戻り値の妥当性を確認する
- ULID のランダム性そのものを厳密比較せず、値オブジェクトとして成立していることを優先する
- 連続呼び出しでは「毎回非 `null` で妥当な ID が返ること」を確認し、値の完全一意性はライブラリ責務として深追いしない
- `@DisplayName` は「どの ID を生成するか」と「何を確認するか」を日本語で簡潔に示す
