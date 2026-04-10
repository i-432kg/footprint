# ADR: `@Nullable` は境界に限定し、`@NullMarked` は効果の高いパッケージに絞る

## ステータス
Accepted

## 背景
本プロジェクトでは JSpecify を導入し、`package-info.java` に `@NullMarked` を付与して nullness を管理してきた。

当初は「全パッケージを `@NullMarked` にする」方針を採用していたが、実装を進める中で次の問題が見えてきた。

- `presentation` や `infrastructure` では framework 都合で `null` が入りやすい
- `@NullMarked` を全域に適用すると、DTO や mapper、type handler で `@Nullable` が過剰に増える
- constructor や内部メソッドまで `@Nullable` が広がると、設計意図が読みにくくなる
- IDE 警告回避のためだけに `@SuppressWarnings` を増やすのも避けたい

## 決定
1. `@Nullable` は「外部入力や framework 由来の `null` が実際に入りうる境界」に限定して付与する
2. `@NullMarked` は全パッケージ一律ではなく、効果が高いパッケージに絞って適用する
3. domain 内部や private constructor、private helper は non-null 前提とし、factory / mapper / adapter で `null` を吸収する

## 運用ルール

### 1. `@Nullable` を付けてよい場所
- request DTO など、framework から直接値が注入されるフィールドや引数
- mapper / type handler / adapter など、外部システムから `null` が返りうる箇所
- 値オブジェクトの `of(...)` のような入力境界
- 実際に `null` を返す戻り値

### 2. `@Nullable` を付けない場所
- private constructor
- validation 済み値のみを受ける internal method
- domain model の必須属性
- non-null を前提とする service / helper の内部処理

### 3. `null` チェックの責務
- 境界で `null` を受ける場合は、その場で業務バリデーションまたは変換を行う
- 内部へ渡す値は non-null にそろえる
- internal constructor では、原則として `@Nullable` を受けない

### 4. `@SuppressWarnings` の扱い
- `null` 警告を消すために広く使わない
- Java の型システム上どうしても必要な箇所だけに限定する
- 代表例は unchecked cast などであり、nullness の回避策として常用しない

## パッケージ方針

### `@NullMarked` を残すパッケージ
- `jp.i432kg.footprint.domain`
- `jp.i432kg.footprint.domain.exception`
- `jp.i432kg.footprint.domain.helper`
- `jp.i432kg.footprint.domain.model`
- `jp.i432kg.footprint.domain.repository`
- `jp.i432kg.footprint.domain.service`
- `jp.i432kg.footprint.domain.value`
- `jp.i432kg.footprint.application`
- `jp.i432kg.footprint.application.command`
- `jp.i432kg.footprint.application.command.model`
- `jp.i432kg.footprint.application.exception`
- `jp.i432kg.footprint.application.port`
- `jp.i432kg.footprint.application.query`
- `jp.i432kg.footprint.application.query.model`
- `jp.i432kg.footprint.exception`
- `jp.i432kg.footprint.logging.masking`

理由:
- framework 依存が比較的少ない
- non-null default の恩恵が大きい
- 業務ルールやアプリケーションルールを明示しやすい

### `@NullMarked` を外すパッケージ
- `jp.i432kg.footprint.presentation`
- `jp.i432kg.footprint.presentation.api`
- `jp.i432kg.footprint.presentation.api.request`
- `jp.i432kg.footprint.presentation.api.response`
- `jp.i432kg.footprint.presentation.api.response.mapper`
- `jp.i432kg.footprint.presentation.helper`
- `jp.i432kg.footprint.presentation.validation`
- `jp.i432kg.footprint.presentation.web`
- `jp.i432kg.footprint.infrastructure`
- `jp.i432kg.footprint.infrastructure.datasource`
- `jp.i432kg.footprint.infrastructure.datasource.mapper`
- `jp.i432kg.footprint.infrastructure.datasource.mapper.query`
- `jp.i432kg.footprint.infrastructure.datasource.mapper.repository`
- `jp.i432kg.footprint.infrastructure.datasource.query`
- `jp.i432kg.footprint.infrastructure.datasource.repository`
- `jp.i432kg.footprint.infrastructure.datasource.typehandler`
- `jp.i432kg.footprint.infrastructure.security`
- `jp.i432kg.footprint.infrastructure.security.mapper`
- `jp.i432kg.footprint.infrastructure.storage`
- `jp.i432kg.footprint.infrastructure.storage.repository`
- `jp.i432kg.footprint.config`
- `jp.i432kg.footprint`

理由:
- Spring / MyBatis / Jackson / SDK など framework 由来の nullable 境界が多い
- DTO、mapper、type handler、controller では `null` が一時的に入りやすい
- 一律 `@NullMarked` にすると `@Nullable` の氾濫や冗長な警告回避が起きやすい

## 実装上のベストプラクティス
- `@NullMarked` 配下では `@NonNull` を原則付けない
- 値オブジェクトは `public static of(@Nullable ...)` で境界を受け、private constructor は non-null にする
- `Optional` は主に戻り値で使い、フィールドや引数で常用しない
- `null` と `unknown` / 特別値オブジェクトを同じ意味で併用しない
- `Objects.requireNonNull(...)` は internal contract の破れ検知に使い、業務バリデーションの代替にはしない

## この方針の狙い
- `@Nullable` の氾濫を防ぐ
- nullness の設計意図を読みやすくする
- framework 境界と domain 内部を明確に分ける
- IDE 警告を suppress に頼らず減らす
