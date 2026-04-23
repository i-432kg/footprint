# ADR: 画面・API・画像配信は認証必須とし、CloudFront 対応までは短命 presigned URL を使う

## Status

Accepted

## Context

初期設計では「閲覧は公開、操作はログイン必須」を基本方針としていた。
一方で、現状の `SecurityConfig` は画面側を概ね認証必須にしているが、閲覧系 API は `permitAll()` のまま残っている。

また、画像配信は保存先によって方式が異なる。

- local: `/images/**` をローカルストレージへマッピングして配信する
- stg / prod: `S3ImageUrlResolver` が S3 presigned URL を返す

S3 presigned URL は、有効期限付きで S3 オブジェクトへのアクセスを許可する仕組みである。
ただし URL 自体が bearer token として振る舞うため、URL を知っている利用者はアプリケーションのセッション認証を通らずに画像を取得できる。

Footprint は投稿画像と位置情報を扱うため、URL を知っているだけで未認証ユーザーが画像を閲覧できる状態は、アプリ全体を認証必須にする方針と整合しない。

参考:

- https://docs.aws.amazon.com/AmazonS3/latest/userguide/using-presigned-url.html
- https://docs.aws.amazon.com/prescriptive-guidance/latest/presigned-url-best-practices/overview.html
- https://docs.aws.amazon.com/AmazonCloudFront/latest/DeveloperGuide/PrivateContent.html

## Decision

Footprint の認可方針は、画面・API・画像配信を含めて原則認証必須とする。

ただし、CloudFront private content の導入はすぐには行わない。
暫定対応として、stg / prod の S3 画像配信は認証済み API レスポンスから短命の presigned URL を返す方式を継続する。

採用方針は次のとおり。

1. 画面は `/login`、サインアップ導線、静的アセット、ヘルスチェックを除き認証必須とする
2. API はログイン、サインアップ、ヘルスチェックを除き認証必須とする
3. local の `/images/**` は認証必須にする
4. stg / prod の S3 presigned URL は暫定策とし、有効期限を短くする
5. 将来の本命案は CloudFront private content とし、複数画像を扱う通常閲覧では signed cookies を優先候補にする

## Temporary Policy

CloudFront 対応までの暫定運用では、S3 presigned URL の有効期限を 1〜5 分程度に設定する。

現在の既定値である 30 分は、漏えい時の有効期間が長く、ログアウト・アカウント停止・権限変更後も発行済み URL が期限まで使われうるため短縮対象とする。

環境変数 `APP_STORAGE_S3_PRESIGNED_GET_EXPIRE_MINUTES` で stg / prod の有効期限を短く設定する。
設定値の上限は、可能であれば S3 bucket policy の `s3:signatureAge` 条件でも強制する。

## Future Policy

将来の画像配信は CloudFront private content を第一候補とする。

- S3 bucket は private とし、S3 直接アクセスを許可しない
- CloudFront からのみ S3 origin へアクセスできる構成にする
- タイムラインや検索結果など複数画像を表示する画面では CloudFront signed cookies を使う
- 個別ファイルの一時ダウンロード用途では CloudFront signed URL を検討する

CloudFront signed cookies を優先候補にする理由は、通常のアプリ閲覧では複数画像をまとめて表示するため、画像ごとに URL を差し替えるより、一定範囲の private content へのアクセス権を cookie で表現する方が扱いやすいためである。

## Consequences

### Positive

- 画面・API・画像配信の認可方針が一貫する
- 未認証ユーザーによる投稿・検索・地図・画像データの直接取得を抑止できる
- CloudFront 導入前でも、presigned URL 漏えい時の影響時間を短縮できる
- 将来の private content 配信方式を明文化できる

### Negative

- 短命 presigned URL は、発行済み URL の即時失効を保証しない
- 有効期限を短くすると、遅い回線や長時間表示中の画像再取得で 403 が発生しやすくなる
- API レスポンスに画像 URL を含める設計では、URL 再発行のために再取得処理が必要になる
- CloudFront 導入までは、画像配信の認可はアプリ認証と完全には連動しない

## Rejected Options

### 閲覧系 API と画像を公開のままにする

不採用。

投稿画像と位置情報を扱うアプリであり、「アプリの機能利用はログイン必須」という方針と整合しない。

### presigned URL の有効期限 30 分を維持する

不採用。

presigned URL は有効期限まで再利用できるため、30 分は暫定策としても長い。
漏えい、ログ共有、ブラウザ履歴、Referer、プロキシログなどに残った場合の影響時間を減らすため短縮する。

### アプリサーバーで全画像をプロキシ配信する

今回は不採用。

認証・認可との連動は最も明確だが、画像転送がアプリサーバー負荷になりやすい。
小規模運用では選択肢になるが、将来の本命は CloudFront private content とする。

## Implementation Notes

設計資料と実装で反映すべき事項:

- `docs/design/05_screen_spec.md` と `docs/design/07_authz_authn.md` は、画面・API・画像配信を原則認証必須として更新する
- `SecurityConfig` では閲覧系 API の `permitAll()` を外す
- `SecurityConfig` では `/images/**` の `permitAll()` を外す
- stg / prod の `APP_STORAGE_S3_PRESIGNED_GET_EXPIRE_MINUTES` を 1〜5 分へ短縮する
- 可能であれば S3 bucket policy で `s3:signatureAge` の上限を設ける
- 将来 CloudFront 導入時は、S3 presigned URL を API レスポンスへ直接返す方式から private content 配信へ移行する
