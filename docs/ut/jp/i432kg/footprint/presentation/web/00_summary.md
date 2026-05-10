# `jp.i432kg.footprint.presentation.web` パッケージ UT仕様書サマリー

## 1. 対象概要

- 対象パッケージ: `jp.i432kg.footprint.presentation.web`
- 対象クラス: `RootController`, `FaviconController`, `LocalFrontendAssetModelAdvice`, `BuiltFrontendAssetModelAdvice`, `ViteManifestAssetResolver`, `OgpModel`, `OgpModelAdvice`
- 個別仕様書: 各クラスごとに `ファイル名_test_design.md` を作成

## 2. 横断観点

| No. | 観点 | 内容 |
|---|---|---|
| 1 | 画面遷移 | controller が期待するテンプレート名を返すこと |
| 2 | モデル属性 | 認証ユーザーや frontend asset 情報をモデルへ適切に追加すること |
| 3 | 環境差異 | local と stg/prod で frontend asset の供給経路が異なること |
| 4 | manifest 解決 | Vite manifest を読み込み、エントリごとの JS/CSS を解決すること |
| 5 | favicon | `/favicon.ico` へのリクエストを静的 favicon へ案内すること |
| 6 | 異常系 | manifest 不在、読み込み失敗、必須エントリ欠落で `IllegalStateException` を送出すること |
| 7 | OGP モデル | OGP / Twitter Card 用の値をモデルとして保持・公開できること |
| 8 | OGP URL 解決 | 公開 origin 設定または request 情報から `og:url` / `og:image` 用の絶対 URL を組み立てること |

## 3. グルーピング方針

- 画面 controller: `RootController`
  - テンプレート名と `displayUsername` のモデル追加を確認する
- favicon controller: `FaviconController`
  - `/favicon.ico` へのリクエストを `/favicon.svg` へリダイレクトすることを確認する
- local 向け advice: `LocalFrontendAssetModelAdvice`
  - 設定済み `FrontendAssetProperties` をそのまま返すことを確認する
- built asset 向け advice: `BuiltFrontendAssetModelAdvice`
  - resolver の結果を `frontendAssets` として返すことを確認する
- manifest resolver: `ViteManifestAssetResolver`
  - manifest 読み込み、各画面エントリの JS/CSS 解決、CSS import 再帰収集、異常系を確認する
- OGP model: `OgpModel`
  - `of(...)` で OGP / Twitter Card 用の値を保持できることを確認する
- OGP advice: `OgpModelAdvice`
  - `OgpProperties` と `HttpServletRequest` から `OgpModel` を組み立て、URL 正規化を公開メソッド経由で確認する

## 4. テスト実装メモ

- `RootController`, `FaviconController` と advice はクラス単体 UT で十分
- `ViteManifestAssetResolver` は一時 manifest JSON か `ByteArrayResource` を使って `ResourceLoader` をスタブする
- `FrontendAssetProperties` は `entries.login/map/mypage/search/timeline` の各 asset を確認する
- `OgpModelAdvice` は `MockHttpServletRequest` を使い、private メソッドは直接テストしない
- `APP_OGP_SITE_BASE_URL` 相当の値は `OgpProperties#setSiteBaseUrl(...)` で固定する
