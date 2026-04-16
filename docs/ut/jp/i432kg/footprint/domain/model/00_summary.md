# `jp.i432kg.footprint.domain.model` パッケージ UT仕様書サマリー

## 1. 対象概要

- 対象パッケージ: `jp.i432kg.footprint.domain.model`
- 対象クラス: `Image`, `Location`, `ParentReply`, `Post`, `Reply`, `User`
- 個別仕様書: 各クラスごとに `ファイル名_test_design.md` を作成

## 2. 横断観点

| No. | 観点 | 内容 |
|---|---|---|
| 1 | ファクトリ | `of(...)` や `root()`, `unknown()` が期待どおりの状態を持つインスタンスを返すこと |
| 2 | 状態判定 | `hasLocation()`, `hasParent()`, `hasParentReply()` などの判定メソッドが状態と一致すること |
| 3 | 派生 getter | `getParentReplyId()` のような派生 getter が内部状態を正しく公開すること |
| 4 | 業務制約 | `Image` の短辺ピクセル数と総ピクセル数制約を満たし、違反時は `InvalidModelException` となること |
| 5 | 不変表現 | `Location.unknown()` や `ParentReply.root()` が `null` ではなく明示的な状態表現になっていること |

## 3. グルーピング方針

- 状態オブジェクト系: `Location`, `ParentReply`
  - 既知 / 不明、親あり / なしの状態表現と判定メソッドを確認する
- 集約モデル系: `Post`, `Reply`, `User`
  - `of(...)` で入力値を保持できること、補助 getter が期待どおりであることを確認する
- 制約付きモデル系: `Image`
  - 正常生成に加え、解像度制約違反時の `InvalidModelException` を確認する

## 4. テスト実装メモ

- 固定化が必要な値: `LocalDateTime`, `Location.unknown()`, `ParentReply.root()`
- `Image` は値オブジェクト側で最小短辺 320px が保証されるが、総ピクセル数 40MP 超過の制約はモデル側で確認する
- `Image` の不変条件違反は `InvalidValueException` ではなく `InvalidModelException` として扱う
- `Location.unknown()` は singleton 的に同一インスタンスを返す実装なので、その点も確認対象にできる
- `Post`, `Reply`, `User` は現在の実装では `null` チェックを持たないため、UT は主に保持値と状態判定に集中する
- 各テストメソッドには `@DisplayName` を付与し、日本語の見出しで観点を明示する
