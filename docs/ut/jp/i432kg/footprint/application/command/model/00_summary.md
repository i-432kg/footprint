# `jp.i432kg.footprint.application.command.model` パッケージ UT仕様書サマリー

## 1. 対象概要

- 対象パッケージ: `jp.i432kg.footprint.application.command.model`
- 対象クラス: `CreatePostCommand`, `CreateReplyCommand`, `CreateUserCommand`, `ImageMetadata`
- 個別仕様書: 各クラスごとに `ファイル名_test_design.md` を作成

## 2. 横断観点

| No. | 観点 | 内容 |
|---|---|---|
| 1 | 生成 | `of(...)` が期待どおりのインスタンスを返すこと |
| 2 | 保持値 | 渡した値をそのまま保持すること |
| 3 | 状態値 | `boolean` や `InputStream` のような補助的な値も失われないこと |

## 3. グルーピング方針

- command 系: `CreatePostCommand`, `CreateReplyCommand`, `CreateUserCommand`
  - `of(...)` に渡した値の保持を確認する
- metadata 系: `ImageMetadata`
  - `Image` 生成に必要な解析結果を保持することを確認する

## 4. テスト実装メモ

- モック化する依存: なし
- `InputStream` は同一インスタンス保持で確認する
- 各テストメソッドには `@DisplayName` を付与し、日本語の見出しで観点を明示する
