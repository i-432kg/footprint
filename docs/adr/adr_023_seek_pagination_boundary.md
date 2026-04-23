# ADR: シークページングは `lastId` を使い、SQL の境界条件をソートキーと一致させる

## Status

Accepted

## Context

Footprint の一覧系 API は無限スクロール前提であり、offset ではなくシーク法を採用している。

現状の API は次の方針で動作している。

- クエリパラメータは `lastId` と `size`
- レスポンスは `List<...>` を返し、`page.nextCursor` は返さない
- フロントエンドは、最後に受け取った要素の ID を次回の `lastId` に渡して次ページを取得する

この API 形状自体は、現状のフロントエンド実装で成立しており、`cursor` / `limit` や `page.nextCursor` へ戻す必要性は高くない。

一方で、現状の SQL は次のような性質を持つ。

- 並び順は `ORDER BY created_at DESC, id DESC`
- 次ページ条件は `created_at < last_created_at`

この場合、同一 `created_at` の行が複数存在すると、ページ境界で一部レコードが取得漏れになる可能性がある。

## Decision

Footprint のページング仕様は、次の方針で固定する。

1. API パラメータ名は `lastId` / `size` とする
2. レスポンスは配列を返し、`page.nextCursor` は追加しない
3. フロントエンドは最後に受け取った要素の ID を次ページ取得の基準に使う
4. SQL のシーク条件は、`ORDER BY` に使うソートキーと同じキー集合で表現する

## Rules

### 1. API 仕様

- `lastId`: 前ページの最後の要素の公開 ID
- `size`: 取得件数
- `lastId` 未指定時は先頭ページを取得する
- `size` 件ちょうど返ってきた場合でも、次ページの有無は確定しない
- 終端判定は、次ページ取得結果が空配列、または `size` 未満になることをもって行う

### 2. SQL の境界条件

一覧取得が次の並び順を採る場合:

```sql
ORDER BY created_at DESC, id DESC
```

シーク条件も同じキーを使って、概念上は次の条件にする。

```sql
created_at < :lastCreatedAt
OR (created_at = :lastCreatedAt AND id < :lastInternalId)
```

`created_at` だけでページ境界を切らない。

### 3. `lastId` の役割

`lastId` はクライアントが保持する公開用カーソルであり、サーバー側ではその ID に対応する行のソートキーを参照して次ページ条件を構築する。

このため、`lastId` という名前でも実質的には cursor と同等に扱う。

## Consequences

### Positive

- 既存フロントエンドのページング方式を変えずに運用できる
- API レスポンスに余分な envelope を追加せずに済む
- ページ境界での取得漏れを防ぎやすくなる
- 設計資料上の `cursor` / `limit` と実装の `lastId` / `size` の差分を明確に整理できる

### Negative

- `page.nextCursor` がないため、クライアントは次ページの有無を推測的に扱う必要がある
- `lastId` からソートキーを逆引きする分、SQL がやや複雑になる
- `created_at` と `id` の両方を意識して SQL を実装しないと不具合を埋め込みやすい

## Rejected Options

### `cursor` / `limit` / `page.nextCursor` へ戻す

不採用。

現行フロントエンドは `lastId` / `size` で成立しており、今から API 形状を戻す必要性が低い。

### `created_at` だけでシーク条件を構成する

不採用。

`ORDER BY created_at DESC, id DESC` と整合しないため、同一 `created_at` の行でページ境界の取りこぼしが発生しうる。

### offset pagination に戻す

不採用。

新着順の無限スクロールと相性が悪く、データ増加時の性能・一貫性の面でも利点が薄い。

## Implementation Notes

- `docs/design/03_database.md` では、シーク法の説明を `lastId` / `size` と複合ソートキー前提へ更新する
- `docs/design/05_screen_spec.md` では、ページネーション説明を `lastId` / `size` と配列レスポンス前提へ更新する
- `docs/design/06_log_design.md` では、`cursor` / `limit` の表現を `lastId` / `size` へ更新する
- `docs/design/review/implementation_gap_review.md` では、ページング差分を「パラメータ名」ではなく「SQL の境界条件の厳密性」を主論点として整理する
- 実装修正時は、`ORDER BY` に対応する複合条件で SQL を見直す
