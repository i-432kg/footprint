# ADR: 値オブジェクトの factory 引数は `@Nullable` を明示する

## ステータス
Accepted

## 背景
本プロジェクトでは、パッケージ単位で JSpecify の `@NullMarked` を付与し、明示しない限り non-null とみなす方針を採用している。

一方、値オブジェクトの factory メソッドは外部入力の入口であり、実際には `null` が渡されうる。そのため、以下のような実装が多い。

- `of(value)` で `null` を受ける
- `null` の場合は `InvalidValueException.required(...)` を投げる

しかし `@NullMarked` 配下で引数が non-null 扱いのままだと、IDE や静的解析で「`null` チェックが常に false」と警告される。

## 決定
`jp.i432kg.footprint.domain.value` 配下の値オブジェクトについて、外部入力を受けて `null` をバリデーションする factory 引数には `@Nullable` を付与する。

## 対象
例:
- `UserId.of(@Nullable String value)`
- `EmailAddress.of(@Nullable String value)`
- `BirthDate.of(@Nullable LocalDate value)`
- `Latitude.of(@Nullable BigDecimal value)`
- `StorageObject.of(@Nullable StorageType storageType, @Nullable ObjectKey objectKey)`

## 目的
- `@NullMarked` 方針と実装実態を一致させる
- `null` チェックに対する IDE 警告を解消する
- 「この factory は外部入力の入口であり、`null` を受けて検証する」という意図を明確にする

## ルール
- 値オブジェクトの factory で `null` を業務バリデーション対象に含める場合、該当引数に `@Nullable` を付与する
- `null` を許容しない内部メソッドや private helper には `@Nullable` を付与しない
- `@Nullable` を付けたとしても、内部保持値が nullable であることを意味しない
- 値オブジェクト生成後のインスタンスは、従来どおり non-null な不変条件を守る

## 補足
- primitive 型には `@Nullable` を付けない
- `@Nullable` は「`null` をそのまま受け入れる」ためではなく、「入口で `null` を検出し、適切な例外へ変換する」ために使う
- この方針は値オブジェクトの public factory に適用し、domain model の内部表現を nullable に広げるものではない

## この方針のメリット
- JSpecify の契約と実装が一致する
- 警告を抑えつつ防御的バリデーションを維持できる
- review 時に「どこが null を受ける入口か」が明確になる

## この方針のデメリット
- factory シグネチャに `@Nullable` が増える
- 入口と内部処理で nullness の考え方を分けて理解する必要がある

## 今後の運用
- 新しい値オブジェクトを追加する際、factory で `null` チェックを行うなら引数に `@Nullable` を付与する
- IDE 警告を suppress で隠す前に、nullness 契約が正しいかを見直す
