# `OgpModel` テスト仕様書

## 1. 基本情報

- 対象クラス: `OgpModel`
- 対象メソッド: `of(String, String, String, String, String, String, String)`, record accessor
- 対象パッケージ: `jp.i432kg.footprint.presentation.web`
- 対応するテストクラス: `OgpModelTest`
- 作成者: Codex
- 作成日: 2026-05-11

## 2. 対象概要

- 何をする処理か: Thymeleaf テンプレートへ公開する OGP / Twitter Card 用の値を保持する
- 入力: `siteName`, `title`, `description`, `type`, `url`, `imageUrl`, `twitterCard`
- 出力: `OgpModel`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | ファクトリ生成 | `of(...)` が渡された値を保持する `OgpModel` を返すこと |
| 2 | 正常系 | accessor | record accessor が各 OGP / Twitter Card 値をそのまま返すこと |
| 3 | エッジケース | 空文字保持 | `OgpModel` 自体は検証責務を持たず、空文字を渡した場合もそのまま保持すること |
| 4 | エッジケース | null 保持 | `OgpModel` 自体は検証責務を持たず、`null` を渡した場合もそのまま保持すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | OGP model を生成する | すべての引数に通常値を指定 | 各 accessor が入力値と一致する | `of(...)` 経由 |
| 2 | エッジケース | 空文字を保持する | すべての引数に `""` を指定 | 各 accessor が `""` を返す | validation は `OgpProperties` / 呼び出し側責務 |
| 3 | エッジケース | null を保持する | すべての引数に `null` を指定 | 各 accessor が `null` を返す | 現実装の record 保持仕様を確認 |

## 5. 実装メモ

- モック化する依存: なし
- 固定化が必要な値: OGP / Twitter Card のサンプル文字列
- `@DisplayName` 方針: `OgpModel.of` が保持する値を入力条件別に記載する
- 備考:
  - `OgpModel` は値保持用 record のため、URL 正規化や default 値の検証は `OgpModelAdvice` 側で確認する
  - null / 空文字の扱いは「許可したい仕様」ではなく「このクラスが検証責務を持たないこと」の確認として扱う

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_createModel_when_valuesAreGiven` | `OgpModel.of は OGP と Twitter Card の値を保持する` |
| 2 | `should_keepEmptyValues_when_emptyValuesAreGiven` | `OgpModel.of は空文字をそのまま保持する` |
| 3 | `should_keepNullValues_when_nullValuesAreGiven` | `OgpModel.of は null をそのまま保持する` |
