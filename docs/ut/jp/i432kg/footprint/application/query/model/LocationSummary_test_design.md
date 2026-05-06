# `LocationSummary` テスト仕様書

## 1. 基本情報

- 対象クラス: `LocationSummary`
- 対象パッケージ: `jp.i432kg.footprint.application.query.model`
- 対応するテストクラス: `LocationSummaryTest`
- 作成者: Codex
- 作成日: 2026-04-18

## 2. 対象概要

- 何をするクラスか: 位置情報の参照値を保持する query model
- 主な項目: `lat`, `lng`

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 保持値 | 緯度・経度を設定した場合に getter で同じ値を返すこと |
| 2 | 異常系相当 | nullable 項目 | `lat`, `lng` が `null` を保持できること |
| 3 | エッジケース | 既定値 | no-args 再構築時に `lat`, `lng` が `null` になること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | 緯度経度を保持する | `lat`, `lng` に値を設定する | 各 getter が設定値を返す |
| 2 | 異常系相当 | `null` の位置情報を保持する | `lat=null`, `lng=null` | 各 getter が `null` を返す |
| 3 | エッジケース | 既定値で再構築できる | no-args 生成 | `lat`, `lng` が `null` |

## 5. 実装メモ

- `@DisplayName` 方針: `保持値`、`null 許容`、`既定値` を明示する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_holdValues_when_latAndLngAreSet` | `LocationSummary は設定された緯度経度を保持する` |
| 2 | `should_allowNullValues_when_locationIsUnknown` | `LocationSummary は不明な位置情報として null を保持できる` |
| 3 | `should_initializeNullValues_when_createdWithNoArgsConstructor` | `LocationSummary は no-args 生成時に null で初期化される` |
