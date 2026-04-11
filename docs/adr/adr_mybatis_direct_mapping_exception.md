# ADR: MyBatis は原則としてドメインモデルへ直接マッピングし、状態オブジェクト再構築が必要な場合のみ result entity を挟む

## ステータス
Accepted

## 背景
本プロジェクトの infrastructure.datasource では、MyBatis の取得結果を原則として直接ドメインモデルへマッピングしている。

この方針は、以下の利点がある。

- mapper と repository の実装が単純になる
- 中間 DTO や mapper 用 entity の増殖を避けられる
- DB の取得形状とドメインモデルの構造が素直に一致する箇所では十分に機能する

一方で、`Reply` は `ParentReply` という状態オブジェクトを持っており、`replies.parent_id` の値をそのまま constructor 引数へ流すだけでは再構築できない。

- `parent_id = null` の場合は `ParentReply.root()`
- `parent_id != null` の場合は `ParentReply.of(replyId)`

このような「列値の単純な型変換」ではなく「状態の解釈」が必要なケースでは、直接マッピングの前提が崩れる。

## 決定
1. MyBatis の取得結果は、原則としてドメインモデルへ直接マッピングする
2. ただし、DB 列からそのままでは再構築できない状態オブジェクトを含む場合だけ、mapper 専用 result entity を挟む
3. 値オブジェクトの単純な 1 カラム変換は、これまでどおり `TypeHandler` で扱う
4. `Reply` はこの例外ルールの対象とし、mapper の result entity から `ParentReply` を組み立ててドメインモデルへ変換する

## 理由

- 既存コードの大半は direct mapping で十分に成立しており、全体を mapper result entity 方式へ寄せるコストは高い
- `ParentReply` は単なる値オブジェクトではなく、`null` を含む列値から状態を解釈して組み立てる必要がある
- この 1 件のためにドメインモデル側を MyBatis 都合へ合わせるより、例外的に mapper result entity を許容したほうが責務分離を保ちやすい

## 運用ルール

- DB 列とドメインモデルの constructor 引数が素直に一致する場合は direct mapping を使う
- 単純な 1 カラム変換は `TypeHandler` を使う
- `null` の有無や複数列の組み合わせから状態オブジェクトを再構築する必要がある場合のみ、mapper result entity を導入する
- mapper result entity は infrastructure に閉じ、repository で domain model へ変換する

## この方針で避けるもの

- 変換価値のない mapper entity の一律導入
- MyBatis 都合に引っ張られたドメインモデルの歪み
- 状態オブジェクトを `TypeHandler` へ押し込むことで責務が曖昧になること
