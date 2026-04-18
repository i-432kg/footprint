# `ViteManifestAssetResolver` テスト仕様書

## 1. 基本情報

- 対象クラス: `ViteManifestAssetResolver`
- 対象パッケージ: `jp.i432kg.footprint.presentation.web`
- 対応するテストクラス: `ViteManifestAssetResolverTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をするクラスか: Vite manifest から画面ごとの JS/CSS asset を解決する component
- 主な依存: `ResourceLoader`, `ViteManifestProperties`

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | manifest 読み込み | manifest を読み込み、全画面エントリの JS を `FrontendAssetProperties` に設定すること |
| 2 | 正常系 | CSS 再帰収集 | import 先 chunk の CSS も重複なく収集すること |
| 3 | 異常系 | manifest 不在 | resource が存在しない場合に `IllegalStateException` を送出し、読み込み失敗としてラップされること |
| 4 | 異常系 | 読み込み失敗 | manifest 読み込み中の例外を `IllegalStateException` にラップすること |
| 5 | 異常系 | エントリ欠落 | 必須 entry が不足している場合に `IllegalStateException` を送出すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | manifest から全画面 asset を解決する | 必須 5 エントリを含む manifest | `login/map/mypage/search/timeline` の JS/CSS が設定される |
| 2 | 正常系 | import 先 CSS を再帰収集する | entry が import を持つ manifest | CSS 一覧に import 先 CSS も重複なしで含まれる |
| 3 | 異常系 | manifest 不在を検出する | `resource.exists()==false` | `IllegalStateException`（`Failed to load Vite manifest` を含む） |
| 4 | 異常系 | manifest 読み込み失敗をラップする | `resource.getInputStream()` が例外 | `IllegalStateException` |
| 5 | 異常系 | 必須エントリ欠落を検出する | 例えば `search` entry がない manifest | `IllegalStateException` |

## 5. 実装メモ

- manifest は `ByteArrayResource` を返す `ResourceLoader` スタブで十分
- CSS の順序は `LinkedHashSet` 由来なので、entry 自身の CSS → import 先 CSS の順を確認する
- エラー時メッセージ全文固定までは不要だが、原因種別が分かることは確認する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_resolveFrontendAssets_when_manifestContainsAllRequiredEntries` | `ViteManifestAssetResolver は manifest から全画面の frontend asset 情報を解決する` |
| 2 | `should_collectImportedCssRecursively_when_entryHasImports` | `ViteManifestAssetResolver は import 先の CSS も再帰的に収集する` |
| 3 | `should_throwException_when_manifestResourceDoesNotExist` | `ViteManifestAssetResolver は manifest が存在しない場合に例外を送出する` |
| 4 | `should_throwException_when_manifestLoadingFails` | `ViteManifestAssetResolver は manifest 読み込み失敗を例外にラップする` |
| 5 | `should_throwException_when_requiredManifestEntryIsMissing` | `ViteManifestAssetResolver は必須 manifest entry が欠落している場合に例外を送出する` |
