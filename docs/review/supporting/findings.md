# Supporting Review

## 指摘内容
| No. | 観点 | 対象 | 優先度 | 指摘内容 | 推奨対応 | 状況 | 備考 |
|---|---|---|---|---|---|---|---|
| 1 | セキュアな実装 / 一貫性 | `src/main/java/jp/i432kg/footprint/config/SecurityConfig.java`<br>`src/main/java/jp/i432kg/footprint/presentation/helper/S3ImageUrlResolver.java` | Medium | non-local 環境の CSP が `img-src 'self' data: blob:` に固定されており、S3 利用時に `S3ImageUrlResolver` が返す presigned URL の画像表示をブラウザ側でブロックする。STG/PROD は `app.storage.type=S3` を前提にしているため、画像取得自体は成功しても front で表示できない構成になっている。 | S3 利用時の画像配信元を CSP に含める。たとえば S3 / CDN ドメインを設定値から注入するか、少なくとも `app.storage.type=S3` のときは `img-src` を保存先構成と整合する値へ切り替える。 | クローズ | `app.storage.image-csp-allow-origins` を追加し、`SecurityConfig` で `img-src` に設定値を反映する形へ修正済み。OpenStreetMap タイル利用を含む複数 origin の指定方法も設定コメントに反映した。 |
| 2 | 保守性 | `src/main/java/jp/i432kg/footprint/config/ViteManifestProperties.java`<br>`src/main/resources/application-stg.yml`<br>`src/main/resources/application-prod.yml`<br>`Dockerfile` | Low | `ViteManifestProperties` の JavaDoc は「`classpath:/static/.vite/manifest.json` を読む」と書いているが、実際のデフォルト値と profile 設定は `classpath:/static/manifest.json` で、Dockerfile も `frontend/dist/` をそのまま `src/main/resources/static/` にコピーしている。コメントだけが古く、運用時の実パス説明として誤っている。 | `ViteManifestProperties` の JavaDoc を現行実装に合わせて修正する。 | クローズ | `ViteManifestProperties` の JavaDoc を `classpath:/static/manifest.json` 前提へ更新済み。 |
