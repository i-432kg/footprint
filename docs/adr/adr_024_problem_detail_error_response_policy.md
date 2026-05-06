# ADR: エラーレスポンスは `ProblemDetail` を基本とし、機械判定用の拡張プロパティを追加する

## ステータス

Accepted

## 背景

初期設計では、API エラーレスポンスを独自の `ErrorResponse` 形式で統一する方針を採っていた。

一方、現状実装では Spring Framework の `ProblemDetail` を利用し、`errorCode` と `details` を追加プロパティとして付与している。

HTTP API のエラー表現については RFC 9457 Problem Details for HTTP APIs が標準化されており、Spring Framework も `ProblemDetail` を正式にサポートしている。

このため、独自 DTO を維持するよりも、標準形式に寄せた方が次の利点がある。

- フレームワークとの整合がよい
- HTTP API の標準的なエラー表現に乗れる
- `title` / `detail` などの共通意味を再発明せずに済む
- 追加情報は拡張プロパティとして表現できる

## 決定

Footprint の API エラーレスポンスは `ProblemDetail` を基本形式として採用する。

機械判定や画面側の分岐に必要な情報は、標準項目を置き換えるのではなく、拡張プロパティとして追加する。

採用方針は次のとおり。

1. HTTP エラー本文は `ProblemDetail` 形式を基本とする
2. 業務・実装上の識別子は `errorCode` 拡張プロパティで表現する
3. バリデーションなど複数要因の詳細は `details` 拡張プロパティで表現する
4. 独自 `ErrorResponse` DTO を正規形式としては採用しない

## ルール

### 1. 基本項目

原則として次の項目を利用する。

- `type`
- `title`
- `status`
- `detail`
- `instance`

ただし `instance` は必須ではなく、必要性が明確な場合にのみ付与する。

### 2. 拡張項目

プロジェクト固有の追加情報は次で表現する。

- `errorCode`: 機械判定用の安定識別子
- `details`: バリデーションエラーや補足情報の一覧

### 3. 役割分担

- `title`: 問題種別ごとに安定した短い説明
- `detail`: その発生事象に固有の説明
- `errorCode`: クライアントや運用で参照する安定コード
- `details`: フィールド単位の追加情報や補足データ

クライアントは原則として `detail` の文字列解析に依存せず、`errorCode` と `details` を利用する。

### 4. `type` の扱い

将来的には `type` を安定した URI として運用する。

例:

- `https://footprint.example/problems/validation-error`
- `https://footprint.example/problems/post-not-found`

ただし、URI 管理をまだ開始していない段階では暫定的に未設定または `about:blank` を許容する。

### 5. Content-Type

可能であれば `application/problem+json` を返す。

ただし、Spring のデフォルト動作や既存クライアントとの整合を優先し、段階的に適用してよい。

## 不採用案

### 独自 `ErrorResponse` 形式を正規形式として維持する

不採用。

次の問題がある。

- 標準仕様との整合が取れない
- Spring 標準サポートを活かしにくい
- `title` / `detail` / `status` と同等の概念を独自に持つことになる

### `ProblemDetail` だけを使い、`errorCode` を持たない

今回は不採用。

標準準拠としてはより純粋だが、クライアントやログ、運用で安定した識別子を持ちづらい。
`type` URI の運用が十分に整うまでは、`errorCode` を併用した方が実務上扱いやすい。

## 良い影響

- Spring Framework の標準機能と整合する
- 標準仕様に沿った説明がしやすい
- 機械判定用の `errorCode` と人間向けの `detail` を分離できる
- 将来 `type` URI を導入しても移行しやすい

## 注意点

- `type` を未整備のままにすると、Problem Details の利点を一部しか活かせない
- `details` の構造が揺れるとクライアント実装が不安定になる
- `detail` に内部実装や機微情報を載せすぎるとセキュリティ上の問題になる
- HTTP ステータスと `status` 項目の不整合を起こさないようにする必要がある

## 実装メモ

- `GlobalExceptionHandler` は `ProblemDetail` ベースのまま維持する
- `errorCode` は継続して付与する
- `details` は用途ごとに構造を安定化させる
- `07_authz_authn.md` や API 仕様書では、独自 `ErrorResponse` ではなく `ProblemDetail` ベースとして記載する
- 将来 `type` URI と `application/problem+json` の扱いを整備する
