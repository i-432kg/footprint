# OGP 実装理解確認問題

## 目的

今回追加した OGP 対応の修正差分について、実装意図と各クラス・設定の責務を説明できるか確認する。

## 対象範囲

- `OgpProperties`
- `OgpModel`
- `OgpModelAdvice`
- `fragments/ogp.html`
- 各 Thymeleaf template からの fragment 読み込み
- `application.yml` の `app.ogp.*`
- `.env.example` の `APP_OGP_SITE_BASE_URL`
- `SecurityConfig` の静的 OGP asset 公開設定
- `static/ogp.png` / `static/ogp.svg`

## 回答方法

- 各問に文章で回答すること。
- 必要に応じて、対象ファイル名や具体的な meta tag 名を挙げること。
- 採点は 100 点満点とする。

---

## 問1: OGP 画像だけでは不十分な理由

`/ogp.png` を static 配下へ追加するだけでは、Zenn などのリンクカード表示に十分ではない。

その理由を、リンクカード生成側が何を参照するか、今回どの meta tag を出力しているかに触れながら説明してください。

配点: 20 点

### 回答

OGP に対応している各 SNS クローラーが対象 html ページの meta tag を読みとってOGPリンクカードを生成する
出力している meta tag は og:imageとtwitter:image
Zenn などで表示されるのは og:image, SNSで表示されるのは twitter:image

---

## 問2: `OgpProperties` の責務

`OgpProperties` は `app.ogp.*` の設定値を受け取るクラスである。

このクラスに `siteBaseUrl`、`title`、`description`、`imagePath`、`twitterCard` などを持たせた理由を説明してください。
また、これらをテンプレートへ直書きする場合と比較したメリット・デメリットも述べてください。

配点: 20 点

### 回答

OgpProperties が OGP の設定内容を一元管理、動的に変更するため
テンプレートへ直書きした場合、OGP設定値の内容をテンプレートの数だけ修正する必要があり保守性が落ちる
siteBaseUrl はデプロイ先の環境によって変動するため直書きの場合 if で制御する必要があり、煩雑なコードになる

---

## 問3: `OgpModelAdvice` が `HttpServletRequest` を使う理由

`OgpModelAdvice#ogp(HttpServletRequest request)` は、`HttpServletRequest` を引数に受け取って `OgpModel` を生成している。

この request から何を取得しているか、`APP_OGP_SITE_BASE_URL` が設定されている場合と未設定の場合で URL 解決がどう変わるかを説明してください。

配点: 20 点

### 回答

HttpServletRequest からrequestURL を取得してbaseURLと連結し、サイトの絶対URLを構築している
APP_OGP_SITE_BASE_URLが設定されているならその値を末尾 / だけ検査してそのまま利用する
設定されていない場合はリクエスト情報からschema, serverName, port を組み立ててURLを構築する
---

## 問4: URL 正規化処理の意図

`OgpModelAdvice` には、base URL の末尾スラッシュ除去、画像 path の先頭スラッシュ補完、標準ポート以外の port 付与といった処理がある。

それぞれの処理がない場合に、どのような不正または不自然な URL が生成されうるか、具体例を挙げて説明してください。

配点: 20 点

### 回答

https://example.com:443/ogp.png のような標準ポート付きの冗長なURL
http://localhost/ogp.png のような 8080 の指定がない不正なURL など

---

## 問5: セキュリティ設定と OGP asset 公開

今回 `SecurityConfig` で `/ogp.png` と `/ogp.svg` を `permitAll` の対象に追加している。

なぜ OGP 画像は未ログインでも取得できる必要があるのかを説明してください。
また、`/about` など公開ページに OGP を設定する場合と、`/timeline` のような認証必須ページに OGP を設定する場合の違いや注意点も述べてください。

配点: 20 点

### 回答

OGPカードリンクはSNSサービス側が参照して生成するため認証が通らないとそもそも画像を埋め込めないため
認証必須ページにOGPを設定する場合は公開ページのURLにする必要がある

---

## 採点結果

総合点: 70 / 100 点

全体として、OGP の基本的な仕組みと今回の実装意図は理解できている。
一方で、`OgpModelAdvice` が実際に組み立てている URL と、URL 正規化処理の具体例に不足がある。

| 問 | 点数 | コメント |
| --- | ---: | --- |
| 問1 | 14 / 20 | 「クローラーが HTML の meta tag を読む」は正しい。ただし今回出力しているのは `og:image` / `twitter:image` だけでなく、`og:title`、`og:description`、`og:url`、`og:type`、`twitter:card` などもある。 |
| 問2 | 18 / 20 | 一元管理、環境差分、テンプレート重複回避を説明できている。直書きより保守性が高い点もよい。デメリットとして「クラスが増えて少し大げさになる」「設定と出力の対応を追う必要がある」まで書けると満点。 |
| 問3 | 15 / 20 | 概ね正しい。ただし実装では `requestURL` そのものではなく、`requestURI` で現在パスを取り、base URL 未設定時に `scheme/serverName/port` を使っている。`APP_OGP_SITE_BASE_URL` 設定時も現在パスは request から取る点が補足できるとよい。 |
| 問4 | 9 / 20 | ポートの例は正しい。ただし問題文で求めている 3 要素のうち、末尾スラッシュ除去と画像 path の先頭スラッシュ補完の具体例が抜けている。例: `https://example.com//ogp.png`、`https://example.comogp.png`。 |
| 問5 | 14 / 20 | OGP 画像が未ログインで取れる必要がある理由は正しい。認証必須ページについては、「公開ページの URL にする必要がある」というより、「クローラーはログインできないため、ログイン画面や 401/302 を拾う可能性がある。Zenn に貼るなら `/about` のような公開ページが安全」と書けるとより正確。 |

## 復習ポイント

`OgpModelAdvice` の URL 組み立ては、次の形で押さえる。

```text
og:url   = baseUrl + request.getRequestURI()
og:image = baseUrl + normalizePath(imagePath)
```

`baseUrl` は `APP_OGP_SITE_BASE_URL` があればそれを優先し、なければ request の `scheme/serverName/port` から組み立てる。

URL 正規化処理は、次の 3 パターンを具体例で説明できるとよい。

```text
末尾スラッシュ未除去:
https://example.com/ + /ogp.png -> https://example.com//ogp.png

先頭スラッシュ未補完:
https://example.com + ogp.png -> https://example.comogp.png

非標準ポート未付与:
http://localhost + /ogp.png -> http://localhost/ogp.png
本来は http://localhost:8080/ogp.png
```
