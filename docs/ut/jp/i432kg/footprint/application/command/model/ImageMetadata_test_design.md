# `ImageMetadata` テスト仕様書

## 1. 基本情報

- 対象クラス: `ImageMetadata`
- 対象メソッド: `of(FileExtension, Byte, Pixel, Pixel, Location, boolean, LocalDateTime)`
- 対象パッケージ: `jp.i432kg.footprint.application.command.model`
- 対応するテストクラス: `ImageMetadataTest`
- 作成者: Codex
- 作成日: 2026-04-17

## 2. 対象概要

- 何をする処理か: 画像解析結果を保持する application 用モデルを生成する
- 入力: `FileExtension`, `Byte`, `Pixel`, `Pixel`, `Location`, `boolean`, `LocalDateTime`
- 出力: `ImageMetadata`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 保持値 | `of(...)` に渡した解析結果をそのまま保持すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | メタデータを生成する | `fileExtension`, `fileSize`, `width`, `height`, `location`, `hasEXIF`, `takenAt` | 各 getter で同値を取得できる |  |

## 5. 実装メモ

- モック化する依存: なし
- 固定化が必要な値: `takenAt`
- `@DisplayName` 方針: `ImageMetadata.of の保持値を記載する`

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_createMetadata_when_valuesAreProvided` | `ImageMetadata.of は渡された解析結果を保持する` |
