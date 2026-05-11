# `OgpModelAdvice` テスト仕様書

## 1. 基本情報

- 対象クラス: `OgpModelAdvice`
- 対象メソッド: `ogp(HttpServletRequest)`
- 対象パッケージ: `jp.i432kg.footprint.presentation.web`
- 対応するテストクラス: `OgpModelAdviceTest`
- 作成者: Codex
- 作成日: 2026-05-11

## 2. 対象概要

- 何をする処理か: `OgpProperties` と現在の request から、Thymeleaf に公開する OGP / Twitter Card 用 `OgpModel` を生成する
- 入力: `HttpServletRequest`
- 出力: `OgpModel`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 設定値保持 | `siteName`, `title`, `description`, `type`, `twitterCard` が `OgpProperties` から `OgpModel` へ反映されること |
| 2 | 正常系 | siteBaseUrl 優先 | `siteBaseUrl` が設定されている場合、request の scheme / host / port ではなく設定値を base URL に使うこと |
| 3 | 正常系 | request base URL 解決 | `siteBaseUrl` 未設定の場合、request の scheme / serverName / serverPort から base URL を組み立てること |
| 4 | 境界値 | 標準ポート省略 | `http:80` / `https:443` の場合、base URL に port を付与しないこと |
| 5 | 境界値 | 非標準ポート付与 | `http:8080` / `https:8443` の場合、base URL に port を付与すること |
| 6 | エッジケース | 末尾スラッシュ除去 | `siteBaseUrl` の末尾 `/` を除去して path と結合すること |
| 7 | エッジケース | imagePath 正規化 | `imagePath` が先頭 `/` なしの場合も `/` を補って `imageUrl` を生成すること |
| 8 | エッジケース | imagePath 空値 | `imagePath` が `null` または blank の場合、`/` を画像 path として扱うこと |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 設定値を model へ反映する | `OgpProperties` に通常値、request URI `/about` | `siteName`, `title`, `description`, `type`, `twitterCard` が設定値と一致する | URL 以外の値 |
| 2 | 正常系 | 設定済み siteBaseUrl を優先する | `siteBaseUrl=https://example.com`, request は `http://localhost:8080/about` | `url=https://example.com/about`, `imageUrl=https://example.com/ogp.png` | request の origin は使わない |
| 3 | 正常系 | siteBaseUrl 未設定時に request から base URL を作る | `siteBaseUrl=""`, request は `http://localhost:8080/about` | `url=http://localhost:8080/about`, `imageUrl=http://localhost:8080/ogp.png` | local 相当 |
| 4 | 境界値 | http 標準ポートを省略する | `siteBaseUrl=""`, request は `http://example.com:80/about` | `url=http://example.com/about` | `:80` なし |
| 5 | 境界値 | https 標準ポートを省略する | `siteBaseUrl=""`, request は `https://example.com:443/about` | `url=https://example.com/about` | `:443` なし |
| 6 | 境界値 | http 非標準ポートを付与する | `siteBaseUrl=""`, request は `http://localhost:8080/about` | `url=http://localhost:8080/about` | `:8080` あり |
| 7 | 境界値 | https 非標準ポートを付与する | `siteBaseUrl=""`, request は `https://example.com:8443/about` | `url=https://example.com:8443/about` | `:8443` あり |
| 8 | エッジケース | siteBaseUrl の末尾スラッシュを除去する | `siteBaseUrl=https://example.com/`, request URI `/about` | `url=https://example.com/about`, `imageUrl=https://example.com/ogp.png` | `//about` にならない |
| 9 | エッジケース | imagePath の先頭スラッシュを補完する | `imagePath=ogp.png`, `siteBaseUrl=https://example.com` | `imageUrl=https://example.com/ogp.png` | `https://example.comogp.png` にならない |
| 10 | エッジケース | imagePath が null の場合に root を使う | `imagePath=null`, `siteBaseUrl=https://example.com` | `imageUrl=https://example.com/` | 現実装の fallback |
| 11 | エッジケース | imagePath が blank の場合に root を使う | `imagePath="   "`, `siteBaseUrl=https://example.com` | `imageUrl=https://example.com/` | `isBlank()` |
| 12 | エッジケース | query string を og:url に含めない | request URI `/search`, query string `q=tokyo` | `url=https://example.com/search` | `request.getRequestURI()` を使う現仕様 |

## 5. 実装メモ

- モック化する依存:
  - `HttpServletRequest` は `MockHttpServletRequest` を利用する
  - `OgpProperties` は実インスタンスを利用し、setter で値を固定する
- 固定化が必要な値:
  - `siteName=Footprint`
  - `title=Footprint - 写真と場所で日常を残す投稿アプリ`
  - `description=Footprint description`
  - `type=website`
  - `imagePath=/ogp.png`
  - `twitterCard=summary_large_image`
- `@DisplayName` 方針: `OgpModelAdvice.ogp` の base URL 解決条件と image path 正規化条件を日本語で記載する
- 備考:
  - private メソッドである `resolveBaseUrl(...)`, `shouldAppendPort(...)`, `normalizePath(...)`, `removeTrailingSlash(...)` は直接テストしない
  - 各 private メソッドの振る舞いは `ogp(...)` の戻り値である `url` / `imageUrl` を通して確認する
  - `request.getRequestURI()` を利用するため、query string は `og:url` に含めない現仕様として確認する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_createOgpModelWithConfiguredValues_when_ogpIsCalled` | `OgpModelAdvice.ogp は OGP 設定値を model に反映する` |
| 2 | `should_useConfiguredSiteBaseUrl_when_siteBaseUrlIsSet` | `OgpModelAdvice.ogp は siteBaseUrl が設定されている場合に設定値を base URL として使う` |
| 3 | `should_buildBaseUrlFromRequest_when_siteBaseUrlIsBlank` | `OgpModelAdvice.ogp は siteBaseUrl が未設定の場合に request から base URL を組み立てる` |
| 4 | `should_omitPort_when_requestUsesStandardHttpPort` | `OgpModelAdvice.ogp は HTTP 標準ポート 80 を省略する` |
| 5 | `should_omitPort_when_requestUsesStandardHttpsPort` | `OgpModelAdvice.ogp は HTTPS 標準ポート 443 を省略する` |
| 6 | `should_appendPort_when_requestUsesNonStandardHttpPort` | `OgpModelAdvice.ogp は HTTP 非標準ポートを付与する` |
| 7 | `should_appendPort_when_requestUsesNonStandardHttpsPort` | `OgpModelAdvice.ogp は HTTPS 非標準ポートを付与する` |
| 8 | `should_removeTrailingSlash_when_siteBaseUrlEndsWithSlash` | `OgpModelAdvice.ogp は siteBaseUrl 末尾のスラッシュを除去して URL を組み立てる` |
| 9 | `should_prefixSlash_when_imagePathDoesNotStartWithSlash` | `OgpModelAdvice.ogp は imagePath に先頭スラッシュがない場合に補完する` |
| 10 | `should_useRootPath_when_imagePathIsNull` | `OgpModelAdvice.ogp は imagePath が null の場合に root path を使う` |
| 11 | `should_useRootPath_when_imagePathIsBlank` | `OgpModelAdvice.ogp は imagePath が blank の場合に root path を使う` |
| 12 | `should_notIncludeQueryString_when_requestHasQueryString` | `OgpModelAdvice.ogp は request の query string を og:url に含めない` |
