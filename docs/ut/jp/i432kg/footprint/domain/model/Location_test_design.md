# `Location` テスト仕様書

## 1. 基本情報

- 対象クラス: `Location`
- 対象メソッド: `of(Latitude, Longitude)`, `unknown()`
- 対象パッケージ: `jp.i432kg.footprint.domain.model`
- 対応するテストクラス: `LocationTest`
- 作成者: Codex
- 作成日: 2026-04-16

## 2. 対象概要

- 何をする処理か: 位置情報の既知 / 不明状態を表すドメインモデルを生成する
- 入力: `Latitude`, `Longitude`
- 出力: `Location`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 既知位置 | `of(...)` が緯度・経度を保持し `unknown=false` となること |
| 2 | 正常系 | 不明位置 | `unknown()` が `latitude=null`, `longitude=null`, `unknown=true` を返すこと |
| 3 | エッジケース | singleton | `unknown()` が同一インスタンスを返すこと |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 既知位置を生成する | `Latitude`, `Longitude` | 値を保持し `unknown=false` |  |
| 2 | 正常系 | 不明位置を生成する | なし | `latitude=null`, `longitude=null`, `unknown=true` |  |
| 3 | エッジケース | `unknown()` の戻り値が再利用される | 複数回呼び出し | 同一インスタンス | 実装依存 |

## 5. 実装メモ

- 固定化が必要な値: 妥当な `Latitude`, `Longitude`
- `@DisplayName` 方針: `Location.of と unknown の状態差を記載する`

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_createKnownLocation_when_latitudeAndLongitudeAreGiven` | `Location.of は既知の位置情報を生成する` |
| 2 | `should_createUnknownLocation_when_unknownIsCalled` | `Location.unknown は不明な位置情報を生成する` |
| 3 | `should_returnSameInstance_when_unknownIsCalledMultipleTimes` | `Location.unknown は同一の不明位置インスタンスを返す` |
