# ADR: JavaDoc は公開契約を優先し、実装詳細には必要な場合のみ付与する

## ステータス
Accepted

## 背景
本プロジェクトではレイヤ分離を重視しており、`controller`、`service`、`repository`、`port`、`mapper`、domain model など、外部から利用される公開要素が多い。

一方で、すべてのクラス・フィールド・メソッドへ一律に JavaDoc を付けると、以下の問題が起きやすい。

- コードの言い換えだけの低情報コメントが増える
- 実装変更に追従されず、コメントがすぐ古くなる
- 読むべき重要な契約情報が埋もれる

そのため、JavaDoc は「可視性」だけではなく、「公開契約として重要か」「コードだけでは意図が読み取りにくいか」で判断する。

## 決定
1. `public class` / `public interface` には原則として JavaDoc を付ける
2. `public method` は、外部公開契約・制約・例外条件が重要なものに付ける
3. `public field` は原則として避けるが、存在する場合は JavaDoc を付ける
4. `private` / `package-private` 要素には、コードだけで意図が十分読める場合は付けない
5. `private` / `package-private` 要素でも、誤用しやすい・境界条件が重要・設計意図が読みにくい場合は付けてよい
6. コードの言い換えだけの JavaDoc は書かない

## マスト対象

- `public class` / `public interface`
- controller, service, repository, port, mapper などの公開入口メソッド
- domain model / value object / command model の `public static of(...)`
- 契約や失敗条件が重要な public factory / validator / converter
- 例外クラスと例外生成メソッド
- `public field` が存在する場合

## 原則不要な対象

- Lombok による自明な getter / setter
- 名前で意図が十分読める `private` helper
- テストコードの補助メソッド
- mapper 専用 entity や内部保持用 `record`
- 単純な override で追加説明がないもの

## 判断基準

以下のいずれかに当てはまる場合は JavaDoc を付ける価値がある。

- 呼び出し側が誤用しやすい
- `null`、空値、境界値、例外条件が重要
- 命名だけでは責務が分からない
- 設計上の理由や制約がコードから読み取りにくい
- 将来の保守で「なぜこうなっているか」が分かりにくい

## 運用ルール

- JavaDoc は「何をするか」だけでなく「どの契約を守るべきか」を優先して書く
- `@param` / `@return` は必要な情報がある場合のみ書き、変数名の言い換えだけにはしない
- 実装変更時に意味が変わる JavaDoc は必ず更新する
- 実装詳細は JavaDoc より、必要に応じてコード近傍の短いコメントで補う

## この方針で避けるもの

- 可視性だけを理由にした一律な JavaDoc 強制
- コードの焼き直しコメント
- 更新されずに古くなる冗長な説明
- 本当に重要な公開契約情報の埋没
