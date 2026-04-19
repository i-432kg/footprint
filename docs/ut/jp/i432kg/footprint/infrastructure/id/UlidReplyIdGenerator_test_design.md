# `UlidReplyIdGenerator` テスト仕様書

## 1. 基本情報

- 対象クラス: `UlidReplyIdGenerator`
- 対象メソッド: `generate()`
- 対象パッケージ: `jp.i432kg.footprint.infrastructure.id`
- 対応するテストクラス: `UlidReplyIdGeneratorTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をする処理か: ULID を生成し、`ReplyId` 値オブジェクトとして返す
- 入力: なし
- 出力: `ReplyId`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 値オブジェクト生成 | `generate()` が `ReplyId` を返すこと |
| 2 | 正常系 | 文字列表現 | 返却値の文字列が空でなく、`ReplyId.of(...)` と同等の妥当性を満たすこと |
| 3 | エッジケース | 呼び出し独立性 | 連続呼び出しでそれぞれ有効な `ReplyId` が返ること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 返信 ID を生成する | なし | `ReplyId` が返り、値が `null` / 空文字でない |  |
| 2 | エッジケース | 連続呼び出ししても毎回有効な ID が返る | `generate()` を複数回呼ぶ | 各結果が非 `null` かつ値オブジェクトとして成立する | 同一参照でないことも確認 |

## 5. 実装メモ

- モック化する依存: なし
- 固定化が必要な値: なし
- `@DisplayName` 方針: 返信 ID 生成と妥当性確認を日本語で記載する
- 備考: 妥当性確認は `ReplyId.of(generated.getValue())` が成功することを利用する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_generateReplyId_when_generateCalled` | `UlidReplyIdGenerator.generate は有効な ReplyId を生成する` |
| 2 | `should_returnValidReplyIds_when_generateCalledMultipleTimes` | `UlidReplyIdGenerator.generate は連続呼び出しでも有効な ReplyId を返す` |
