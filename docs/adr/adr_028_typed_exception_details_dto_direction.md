# ADR: 例外詳細は将来的に typed details DTO へ移行する

## ステータス

Accepted

## 背景

本プロジェクトの独自例外は、`BaseException` に `Map<String, Object> details` を持たせる設計で運用している。

この設計は次の利点があり、MVP 段階では合理的であった。

- `ProblemDetail` の拡張プロパティへ流し込みやすい
- ログ出力やマスキング処理へそのまま渡しやすい
- 新しい例外を低コストで追加できる

一方で、独自例外が増えるにつれて次の課題が明確になった。

- `details` のキー名や構造が揺れやすい
- 追加項目の意味が型で表現されず、リファクタが弱い
- 例外ごとの責務や表現の統一が崩れやすい
- `Map<String, Object>` では reserved key の上書きや構造逸脱をコンパイル時に防げない

ADR-027 により `ProblemDetail.details` の正規形は定義したが、これはあくまで出力契約の整理であり、例外クラス内部表現の型安全性までは解決していない。

## 決定

例外詳細の本命設計は、`Map<String, Object>` ではなく typed details DTO とする。

ただし、現時点で動作中の例外設計を大規模改修するリスクは高いため、リリース前は現行の `Map<String, Object>` ベースを維持する。

### 1. 当面の運用

- `BaseException` は `Map<String, Object> details` を維持する
- 例外詳細の正規形は ADR-027 の `target`, `reason`, `rejectedValue` を基準とする
- `DetailBasedException` などの helper で構造の揺れを抑える

### 2. 将来の移行方針

リリース後の改修では、例外詳細を typed details DTO へ段階的に移行する。

候補例:

- `ValidationErrorDetail`
- `ResourceNotFoundDetail`
- `ConflictDetail`
- `MismatchDetail`
- `UseCaseFailureDetail`

### 3. 目標形

将来的には、独自例外は型付きの detail object を保持し、`ProblemDetail` やログ出力の境界でのみ map / JSON へ変換する。

概念上の構成は次を想定する。

- 例外内部表現: typed details DTO
- 境界変換: `GlobalExceptionHandler` や logging adapter が map / JSON へ変換
- API 契約: ADR-027 の正規形を維持

## 不採用案

### `Map<String, Object>` のまま全面固定する

不採用。

短期的には成立するが、例外クラスが増えるほど型安全性と保守性の課題が蓄積する。

### `extra` 用 enum や専用 builder を導入して `Map<String, Object>` を改善する

今回は採用しない。

reserved key の上書き抑止には有効だが、最終的な本命である typed details DTO への移行を遅らせやすい。
リリース前の安全策としては理解できるが、中長期の設計としては中間形に留まる。

## 良い影響

- 将来的な例外設計の本命を明確にできる
- リリース前の変更量を抑えつつ、設計の到達点を共有できる
- 例外詳細の型安全化を段階導入しやすくなる

## 注意点

- 現時点では `Map<String, Object>` と typed details DTO が併存しないようにする
- typed details DTO 化は、例外階層整理、`ErrorCode` 整理、`GlobalExceptionHandler` 変換責務整理と合わせて実施する必要がある
- テストも「例外詳細 DTO 単体」と「API 応答変換」の2段構えへ見直す必要がある

## 実装メモ

- リリース前は `BaseException` のシグネチャを変更しない
- `exception_design_todo.md` では typed details DTO 化をリリース後課題として管理する
- 実際の移行時は `ValidationErrorDetail` と `ResourceNotFoundDetail` から着手するのがよい
