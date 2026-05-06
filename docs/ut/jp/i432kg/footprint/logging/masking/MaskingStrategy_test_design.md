# `MaskingStrategy` テスト仕様書

## 1. 基本情報

- 対象クラス: `MaskingStrategy`
- 対象パッケージ: `jp.i432kg.footprint.logging.masking`
- 対応するテストクラス: `MaskingStrategyTest`（新規作成予定）
- 作成者: Codex
- 作成日: 2026-04-26

## 2. 対象概要

- 何をするクラスか: 機微情報のマスク方式を表す enum
- 主な責務: 値全体の固定文字列置換、メールアドレスのローカル部部分マスク

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | `FULL` | 入力値の内容に関係なく固定マスク文字列 `********` を返すこと |
| 2 | 正常系 | `EMAIL` 通常系 | `@` より前が 2 文字以上のメールアドレスを先頭 1 文字だけ残してマスクすること |
| 3 | 正常系 | `EMAIL` 境界値 | ローカル部が 1 文字以下、または `@` を含まない文字列では全文マスクすること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | `FULL` は値全体をマスクする | `"secret-value"` | `********` を返す |
| 2 | 正常系 | `EMAIL` はローカル部を部分マスクする | `"alice@example.com"` | `a********@example.com` を返す |
| 3 | 正常系 | `EMAIL` はローカル部 1 文字のとき全文マスクする | `"a@example.com"` | `********` を返す |
| 4 | 正常系 | `EMAIL` は `@` を含まない値を全文マスクする | `"invalid-mail"` | `********` を返す |

## 5. 実装メモ

- 依存はないため enum メソッドを直接呼び出す
- `EMAIL` は `toString()` 経由で文字列化する実装のため、テスト入力は文字列で十分

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_returnMaskedValue_when_fullStrategyMasksAnyValue` | `MaskingStrategy.FULL は任意の値を固定文字列でマスクする` |
| 2 | `should_maskLocalPart_when_emailStrategyReceivesNormalEmail` | `MaskingStrategy.EMAIL は通常のメールアドレスのローカル部をマスクする` |
| 3 | `should_returnFullyMaskedValue_when_emailStrategyReceivesSingleCharLocalPart` | `MaskingStrategy.EMAIL はローカル部が 1 文字のメールアドレスを全文マスクする` |
| 4 | `should_returnFullyMaskedValue_when_emailStrategyReceivesValueWithoutAtMark` | `MaskingStrategy.EMAIL は @ を含まない値を全文マスクする` |
