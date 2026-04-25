# ADR: `ProblemDetail.details` は object を基本とし、正規項目を固定する

## ステータス

Accepted

## 背景

`ProblemDetail` を基本とするエラーレスポンス方針は ADR-024 で採用済みである。

一方で、`details` の構造は現状実装で次のように揺れている。

- バリデーションエラーは `details.errors[]` の中に `field`, `message`, `rejectedValue` を持つ
- domain / application 例外は `target`, `reason`, `rejectedValue` を中心にしつつ、必要に応じて追加項目を持つ
- resource not found 系は `postId` など個別キーだけを返している
- API 仕様書では `details` を array として記述しているが、実装は object を返している

この状態では、クライアント実装、ログ整備、API 仕様書のいずれでも扱いが不安定になる。

## 決定

`ProblemDetail` の拡張プロパティ `details` は、常に object を返す正規形とする。

### 1. 基本方針

- `details` 自体は常に object とする
- 機械判定に使う正規項目は `target`, `reason`, `rejectedValue` とする
- `rejectedValue` は任意項目とし、機微情報や巨大値を無理に含めない
- 人間向け文言は `detail` に集約し、`details` に `message` は原則持たせない

### 2. バリデーションエラー

バリデーションエラーは、`details.errors` 配下へ配列で持つ。

```json
{
  "type": "about:blank",
  "title": "Validation Error",
  "status": 400,
  "detail": "リクエストの形式が不正です。",
  "errorCode": "DOMAIN_INVALID_VALUE",
  "details": {
    "errors": [
      {
        "target": "size",
        "reason": "type_mismatch",
        "rejectedValue": "abc",
        "source": "query"
      }
    ]
  }
}
```

`errors[]` の各要素は次を正規項目とする。

- `target`
- `reason`
- `rejectedValue`（任意）

必要に応じて次の任意項目を追加してよい。

- `source`
- `min`
- `max`
- `minLength`
- `maxLength`
- `expectedFormat`

### 3. domain / application / use case 例外

独自例外の `details` は、単一 object として返す。

```json
{
  "type": "about:blank",
  "title": "Domain Error",
  "status": 400,
  "detail": "latitude must be between -90 and 90.",
  "errorCode": "DOMAIN_INVALID_VALUE",
  "details": {
    "target": "latitude",
    "reason": "out_of_range",
    "rejectedValue": 100,
    "min": -90,
    "max": 90
  }
}
```

### 4. resource not found 系

resource not found 系も、個別キーだけでなく `target` / `reason` を含む形へ寄せる。

例:

```json
{
  "details": {
    "target": "post",
    "reason": "not_found",
    "resourceId": "01ARZ3NDEKTSV4RRFFQ69G5FAV"
  }
}
```

`postId`, `replyId`, `userId` のような個別キーは、必要なら `resourceId` または追加プロパティとして保持してよいが、`target` / `reason` を省略しない。

### 5. OpenAPI 記述

OpenAPI の `ProblemDetailError.details` も object 前提へ修正する。

- `details` を array として表現しない
- バリデーションエラーは `details.errors[]`
- それ以外は `details` 直下に正規項目を持つ

## 不採用案

### `details` を常に array にする

不採用。

独自例外や resource not found 系まで配列化すると、単一原因のエラーでも冗長になる。

### バリデーション系の `field` / `message` を正規項目として維持する

不採用。

- `field` は domain / application 例外の `target` と揃わない
- `message` は人間向け文言であり、機械判定キーとして不安定

### `rejectedValue` を必須とする

不採用。

機微情報、巨大 payload、内部識別子を不必要に露出しやすいため、任意項目とする。

## 良い影響

- クライアントが `details` を安定して解釈できる
- `GlobalExceptionHandler` と独自例外の責務分担が明確になる
- ログ設計で `errorCode` / `details.target` / `details.reason` を共通キーとして扱いやすい
- OpenAPI と実装の齟齬を減らせる

## 注意点

- `target` と `reason` の命名が揺れると、固定スキーマの価値が下がる
- `rejectedValue` はマスキング対象になりうるため、ERR-05 と一体で運用する必要がある
- `source` や追加プロパティは、最小限にとどめないと再び構造が拡散する

## 実装メモ

- `GlobalExceptionHandler` の `validationError(...)` は `field` / `message` ではなく `target` / `reason` ベースへ寄せる
- resource not found 系の例外 `details` を `target` / `reason` つきへ寄せる
- `04_api_spec.yaml` の `ProblemDetailError.details` は object スキーマへ見直す
- マスキング方針は ADR-008 と ERR-05 に従う
