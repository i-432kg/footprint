# `Byte` テスト仕様書

## 1. 基本情報

- 対象クラス: `Byte`
- 対象メソッド: `of(long)`
- 対象パッケージ: `jp.i432kg.footprint.domain.value`
- 対応するテストクラス: `ByteTest`
- 作成者: Codex
- 作成日: 2026-04-15

## 2. 対象概要

- 何をする処理か: データサイズをバイト単位で表す値オブジェクトを生成する
- 入力: `long`
- 出力: `Byte`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 範囲内 | 0 以上 10MB 以下を受け入れること |
| 2 | 境界値 | 上下限 | 0 と 10,485,760 を受け入れること |
| 3 | 異常系 | 範囲外 | 負数と上限超過を拒否すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 範囲内値を受け入れる | `1`, `1024` | 生成成功 |  |
| 2 | 境界値 | 下限値を受け入れる | `0` | 生成成功 |  |
| 3 | 境界値 | 上限値を受け入れる | `10_485_760` | 生成成功 |  |
| 4 | 異常系 | 下限未満を拒否する | `-1` | `InvalidValueException.outOfRange(...)` |  |
| 5 | 異常系 | 上限超過を拒否する | `10_485_761` | `InvalidValueException.outOfRange(...)` |  |

## 5. 実装メモ

- `@DisplayName` 方針: `Byte.of の範囲条件を日本語で記載する`

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_createByte_when_valueIsWithinRange` | `Byte.of は範囲内の値を生成できる` |
| 2 | `should_createByte_when_valueIsMinBoundary` | `Byte.of は下限値 0 を受け入れる` |
| 3 | `should_createByte_when_valueIsMaxBoundary` | `Byte.of は上限値 10MB を受け入れる` |
| 4 | `should_throwException_when_valueIsBelowMin` | `Byte.of は負の値を拒否する` |
| 5 | `should_throwException_when_valueExceedsMax` | `Byte.of は上限超過を拒否する` |
