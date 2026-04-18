# ADR: 現在時刻と ULID 生成は直接呼ばず注入可能な境界を使う

## ステータス
Accepted

## 背景
現在の application service では、現在時刻と ID 生成を次のように直接呼び出している。

- `LocalDateTime.now()`
- `UlidCreator.getUlid()`

この実装は本番動作としては単純だが、UT では次の問題がある。

- 生成時刻を固定できず、`createdAt` / `takenAt` などの検証がしづらい
- 生成 ID を固定できず、保存対象の `PostId`, `ReplyId`, `UserId` を厳密に確認しづらい
- 生成タイミングに依存するため、実装変更時に意図しない差分を見逃しやすい

特に `application.command` はユースケース手順の正しさを確認したい層であり、時刻と ID が固定できないと、UT が「呼ばれたこと」中心になりやすい。

## 問題
- `now()` と static な ULID 生成が application service に埋め込まれている
- テストで deterministic に値を固定できない
- 将来、タイムゾーン方針や ID 生成方式を変更すると修正箇所が散らばる
- static mock に頼るとテストが重く、設計改善にもならない

## 決定
現在時刻と ULID 生成は application service から直接呼ばず、注入可能な境界を通して利用する。

採用方針は次のとおり。

1. 現在時刻は `java.time.Clock` を利用する
2. ID 生成は専用の生成境界を設ける
3. 導入前に既存挙動を UT で固定し、デグレ防止を優先する

## 設計方針

### 1. 現在時刻は `Clock` を注入する
`LocalDateTime.now()` を直接呼ばず、`LocalDateTime.now(clock)` を使う。

本番では `Clock` を Bean として提供し、テストでは `Clock.fixed(...)` を使って固定する。

対象の第一候補:

- `PostCommandService`
- `ReplyCommandService`

必要に応じて他レイヤにも同じ方針を展開する。

### 2. ID 生成は専用ポートを経由する
`UlidCreator.getUlid()` を直接呼ばず、用途別の ID 生成ポートを用意する。

- `PostIdGenerator`
- `ReplyIdGenerator`
- `UserIdGenerator`
- `ImageIdGenerator`

例:

```java
public interface PostIdGenerator {
    PostId generate();
}
```

本番実装は infrastructure 層で ULID を生成し、値オブジェクトへ変換して返す。テストでは固定の値オブジェクトを返す test double を使う。

実装配置は次のとおりとする。

- port: `application.port`
- 実装: `infrastructure.id`

これにより application service は生成方式の詳細を知らず、将来 UUID などへ変更する場合も infrastructure 実装の差し替えで対応できる。

### 3. 導入前に既存 UT を先に作る
`Clock` や ULID ラッパーを導入すると constructor 引数や DI 設定が変わるため、先に既存実装の UT を整備しておく。

この順序にする理由:

- リファクタリング前の現状挙動を明確にできる
- リファクタリング後の差分が追いやすい
- テスト修正と設計変更を同時に行うリスクを減らせる

## ルール

### 1. application service で直接呼ばない
以下を application service で直接呼ばない。

- `LocalDateTime.now()`
- `Instant.now()`
- `UlidCreator.getUlid()`

### 2. テストでは固定値を使う
- 時刻は `Clock.fixed(...)`
- ULID は固定文字列を返す test double

を使って deterministic に検証する。

### 3. static mock は最終手段とする
`UlidCreator` や時刻 API の static mock は、既存コードの一時的な保護としては許容できるが、恒久運用の前提にはしない。

### 4. 生成境界は application に置き、実装は infrastructure に置く
時刻と ID の生成は業務ルールそのものではなく、ユースケース実行時の技術的関心であるため、port は application 層に置く。

一方で、ULID ライブラリへの依存は技術実装であるため、実装クラスは infrastructure 層に置く。

### 5. 値オブジェクトに `generate()` は持たせない
`PostId.generate()` のような API は見た目は簡潔だが、値オブジェクトが ULID / UUID の生成方式、またはその抽象に依存しやすくなる。

このプロジェクトでは、値オブジェクトは次の責務に限定する。

- 文字列表現の妥当性検証
- 値としての比較・保持
- 再構築

新規 ID の払い出し責務は generator port に分離する。

## 選択肢

### 案1: 現状どおり直接呼ぶ
不採用。

理由:
- テストで固定しづらい
- 差分の追跡が難しい
- 生成方式変更時の影響範囲が広い

### 案2: `Clock` は導入し、ULID は static mock で対応する
暫定案としては可能だが、不採用。

理由:
- 時刻だけ改善しても ID 生成のテストしづらさが残る
- static mock 依存が設計改善にならない

### 案3: `Clock` と ID 生成境界を両方導入する
採用。

理由:
- テストを deterministic にできる
- 依存の向きが明確になる
- 将来の方針変更に追従しやすい

### 案4: 値オブジェクト自身に `generate()` を持たせる
不採用。

理由:
- `PostId`, `ReplyId` などの値オブジェクトが ID 生成方式の詳細を知ることになる
- ULID / UUID 生成ライブラリ、またはその抽象を domain に持ち込みやすい
- 値の妥当性と新規値払い出しの責務が混ざる
- 将来の生成方式変更時に domain まで影響が広がる

## 段階的な進め方
1. 既存の `application.command` の UT を現状実装ベースで先に整備する
2. `Clock` を導入し、時刻依存を置き換える
3. UT で時刻を固定し、保存されたモデルの時刻を明示的に検証する
4. ID 生成境界を導入し、`UlidCreator.getUlid()` を置き換える
5. UT で生成 ID も固定し、保存対象の ID を明示的に検証する

## 期待する効果
- `application.command` の UT が安定し、時刻と ID を含めた厳密な検証ができる
- リファクタリング時のデグレ検知力が上がる
- 時刻方針や ID 生成方式の変更を一箇所に閉じ込められる
- static API への直接依存を減らせる
