# `LocalFrontendAssetModelAdvice` テスト仕様書

## 1. 基本情報

- 対象クラス: `LocalFrontendAssetModelAdvice`
- 対象パッケージ: `jp.i432kg.footprint.presentation.web`
- 対応するテストクラス: `LocalFrontendAssetModelAdviceTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をするクラスか: local 環境向けに設定済み frontend asset 情報をモデルへ公開する advice
- 主な依存: `FrontendAssetProperties`

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 設定値返却 | コンストラクタで受けた `FrontendAssetProperties` をそのまま返すこと |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | local 用 frontend asset 情報を返す | `FrontendAssetProperties` を注入 | 同一インスタンスを返す |

## 5. 実装メモ

- `frontendAssets()` の戻り値が参照同一であることを見れば十分

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_returnConfiguredFrontendAssets_when_frontendAssetsIsCalled` | `LocalFrontendAssetModelAdvice は設定済み frontend asset 情報をそのまま返す` |
