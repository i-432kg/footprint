# `BuiltFrontendAssetModelAdvice` テスト仕様書

## 1. 基本情報

- 対象クラス: `BuiltFrontendAssetModelAdvice`
- 対象パッケージ: `jp.i432kg.footprint.presentation.web`
- 対応するテストクラス: `BuiltFrontendAssetModelAdviceTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をするクラスか: stg/prod 環境向けに Vite manifest から解決した frontend asset 情報をモデルへ公開する advice
- 主な依存: `ViteManifestAssetResolver`

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | resolver 委譲 | `viteManifestAssetResolver.resolve()` の戻り値をそのまま返すこと |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | built 用 frontend asset 情報を返す | resolver が `FrontendAssetProperties` を返す | 同じインスタンスを返す |

## 5. 実装メモ

- `resolve()` の呼び出し有無も検証する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_returnResolvedFrontendAssets_when_frontendAssetsIsCalled` | `BuiltFrontendAssetModelAdvice は resolver が解決した frontend asset 情報を返す` |
