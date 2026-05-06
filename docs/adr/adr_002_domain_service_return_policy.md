# ADR: Domain Service の戻り値と例外変換の方針

## ステータス
Accepted

## 背景
本プロジェクトでは、`domain.service` から `application.exception.resource.*` を送出しており、domain 層が application 層の例外に依存していた。

例:
- `PostDomainService` が `UserNotFoundException` を投げる
- `ReplyDomainService` が `PostNotFoundException` / `UserNotFoundException` / `ReplyNotFoundException` を投げる

この構成は Onion Architecture の依存方向に反する。

一方で、単純に例外を消すだけでは、domain service の戻り値が `boolean` と `Optional` で混在し、呼び出し側の判断基準が曖昧になる。

そのため、domain service / repository の戻り値をどのように扱うかを先に整理する。

## 決定
### 1. application 向け例外は application 層で投げる
- `PostNotFoundException`
- `UserNotFoundException`
- `ReplyNotFoundException`

これらの「対象リソースが見つからない」例外は application 層に残し、application service が throw する。

### 2. domain service は application 例外を投げない
- domain service は domain rule を判定する
- 判定に必要な existence / lookup は repository や下位 service を通じて取得する
- application 例外への変換は application service が行う

### 3. 戻り値の使い分けを以下に統一する

#### `boolean`
「存在確認」や「条件成立確認」のように、呼び出し側が yes / no だけ知れば十分な場合に使う。

例:
- `existsById(...)`
- `existsByEmail(...)`
- `isExistUser(...)`
- `isExistPost(...)`

用途:
- application service が `if (!exists) throw XxxNotFoundException` のように使う

#### `Optional<T>`
「存在すれば値そのものが必要」な場合に使う。

例:
- `findReplyById(...)`

用途:
- application service または domain service が取得結果の中身を使って次の判定を行う

#### domain exception
「存在有無」ではなく、取得済みの値同士の整合性や業務ルール違反を表す場合に使う。

例:
- `ReplyPostMismatchException`
- `EmailAlreadyUsedException`
- `InvalidValueException`

### 4. 判定ルール
- 値が不要で、存在だけ分かればよい: `boolean`
- 値が必要で、存在しない可能性がある: `Optional<T>`
- 取得済みデータに対する業務ルール違反: domain exception

## 適用イメージ

### `PostDomainService`
- `isExistPost(PostId)` は `boolean` のまま
- `validateCreatePost(...)` のような application 例外前提メソッドは縮小または廃止
- ユーザー存在確認は application service が `userDomainService.isExistUser(...)` を使って `UserNotFoundException` に変換する

### `ReplyDomainService`
- 投稿存在確認: `postDomainService.isExistPost(...) -> boolean`
- ユーザー存在確認: `userDomainService.isExistUser(...) -> boolean`
- 親返信取得: `replyRepository.findReplyById(...) -> Optional<Reply>`
- 親返信が別投稿に属する場合のみ `ReplyPostMismatchException` を投げる
- `PostNotFoundException` / `UserNotFoundException` / `ReplyNotFoundException` への変換は application service で行う

## この方針の理由
- domain と application の依存方向を守れる
- 「存在しない」と「業務ルール違反」を分離できる
- `boolean` と `Optional` の使い分けに基準ができる
- API / use case 都合の例外を domain に持ち込まずに済む

## この方針の注意点
- `boolean` を返すだけでは、呼び出し側が何の存在確認かを読み取りづらいことがある
  - メソッド名を明確にする
- `Optional` を「nullable の代替」として乱用しない
  - 値が必要な場合に限定する
- domain service が再び application 例外へ依存しないようにする

## 今後の実装方針
- `application.exception.resource.*` は application に残す
- `domain.service` から resource 例外 import を除去する
- application service が existence / lookup の結果を受けて resource 例外へ変換する
- domain service では domain rule exception のみ送出可能とする
