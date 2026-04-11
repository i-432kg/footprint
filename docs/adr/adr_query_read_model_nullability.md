# ADR: application query model は維持し、実データ上 nullable な項目を明示する

## ステータス
Accepted

## 背景
本プロジェクトの参照系では、DB から取得したい形が domain model と一致しない。

- 一部フィールドは `NULL` を取りうる
- domain model と同じ完全な状態を前提にしない read model が必要
- 参照系では複雑な SQL を直接発行したい

そのため、`application.query.model` に query DTO を置き、MyBatis が直接マッピングする構成を採用している。

一方で、`application.query.model` は `@NullMarked` 配下にあり、実データとして `null` を取りうる項目まで non-null と見なされていたため、契約と実態にズレが生じていた。

## 決定
1. `application.query.model` は引き続き application 層の read model として維持する
2. MyBatis 用の中間 DTO は、意味のある変換が必要になるまで追加しない
3. 実データ上 `null` を取りうる query model の項目には `@Nullable` を明示する
4. 中間 DTO は、単なる 1:1 転写ではなく、変換によって意味や責務が増えるタイミングで導入する

## 理由

- 現時点では MyBatis の結果と `application.query.model` の関係がほぼ 1:1 であり、中間 DTO を追加しても知識の重複が増えるだけ
- query model は command model や domain model と異なり、read 専用で不完全状態を含みうる
- そのため重要なのは DTO の枚数を増やすことではなく、nullability 契約を正直に表現すること

## 運用ルール

- `application.query.model` で `null` を取りうるフィールドには `@Nullable` を付ける
- `@NoArgsConstructor(force = true)` など MyBatis 都合の構成は query model に限って許容する
- domain model / command model には mapper 都合を持ち込まない
- query model に整形ロジックや派生値が増え、中間表現の意味が出てきたら `infrastructure.datasource.query` に MyBatis 専用 DTO を追加する

## この方針で避けるもの

- 変換価値がない 1:1 DTO の増殖
- `@NullMarked` 契約と実データ形状の不一致
- read model と domain model の責務混同
