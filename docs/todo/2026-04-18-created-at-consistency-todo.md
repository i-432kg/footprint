# CreatedAt 決定責務の統一 TODO

## 背景

`createdAt` 相当の時刻決定責務がモデルごとに揃っていない。

- `Post`
  - [PostCommandService.java](/Users/432kg/IdeaProjects/footprint/src/main/java/jp/i432kg/footprint/application/command/PostCommandService.java) で `LocalDateTime.now(clock)` を生成し、[Post.java](/Users/432kg/IdeaProjects/footprint/src/main/java/jp/i432kg/footprint/domain/model/Post.java) に渡している
- `Reply`
  - [ReplyCommandService.java](/Users/432kg/IdeaProjects/footprint/src/main/java/jp/i432kg/footprint/application/command/ReplyCommandService.java) で `LocalDateTime.now(clock)` を生成し、[Reply.java](/Users/432kg/IdeaProjects/footprint/src/main/java/jp/i432kg/footprint/domain/model/Reply.java) に渡している
- `User`
  - [User.java](/Users/432kg/IdeaProjects/footprint/src/main/java/jp/i432kg/footprint/domain/model/User.java) は `createdAt` を保持していない
  - [UserMapper.java](/Users/432kg/IdeaProjects/footprint/src/main/java/jp/i432kg/footprint/infrastructure/datasource/mapper/repository/UserMapper.java) の `UserInsertEntity.from(user, clock)` で `createdAt` / `updatedAt` を決定している

現状の動作に破綻はないが、同じ「作成日時」の責務が `application` と `infrastructure` に分散している。

## 問題

- 同種の監査項目に対する責務がモデルごとに揃っていない
- `User` だけ domain model から作成日時が見えず、UT や将来の仕様変更時に扱いが分かれる
- `createdAt` をドメイン概念として扱うのか、永続化監査項目として扱うのかが曖昧

## 現時点の整理

- `Post`, `Reply`
  - `createdAt` は domain model の属性
  - 決定主体は application service
- `User`
  - `createdAt` は domain model の属性ではない
  - 決定主体は infrastructure の insert entity

## 検討候補

### 案1. application で統一する

- `User` にも `createdAt` を持たせる
- `UserCommandService` で `Clock` から時刻を決定し、domain model に渡す
- repository / mapper は受け取った値をそのまま保存する

メリット:

- `Post`, `Reply`, `User` の作成日時決定責務がそろう
- UT で保存対象の `createdAt` を同じ粒度で確認できる
- `createdAt` をドメイン上意味のある属性として扱いやすい

懸念:

- `User` domain model の属性追加が必要
- read model / mapper / fixture / seed への影響範囲が比較的大きい

### 案2. infrastructure で統一する

- `Post`, `Reply` から `createdAt` を外すか、少なくとも insert 時刻決定を repository / mapper 側へ寄せる
- application service は時刻を決定しない

メリット:

- 監査項目を persistence concern と割り切れる
- domain model をやや軽くできる

懸念:

- すでに `Post`, `Reply` は domain model が `createdAt` を保持しており、逆方向の変更量が大きい
- application UT で保存モデルの時刻を直接確認しづらくなる
- 既存の `Clock` 導入メリットが薄くなる

### 案3. 現状維持

- `Post`, `Reply` は application 決定
- `User` は infrastructure 決定

メリット:

- 追加改修が不要

懸念:

- 設計説明が難しい
- 今後 `updatedAt`, `publishedAt`, `deletedAt` などの扱いでも揺れやすい

## 暫定推奨

案1を第一候補とする。

理由:

- 既に `Post`, `Reply` が application で `Clock` を使っており、現在の流れと整合する
- 今回の `Clock` 導入で UT も `createdAt` を明示検証できるようになっている
- 作成日時を「永続化都合」ではなく「ユースケース実行時に決まる値」として扱う方が一貫しやすい

## TODO

- `User.createdAt` を domain model に持たせるかを決定する
- 採用する場合:
  - `User` model / repository / mapper / fixture / seed / UT の修正範囲を洗い出す
  - `updatedAt` をどこで決定するかも合わせて整理する
- 見送る場合:
  - 「`User.createdAt` は監査項目であり domain 属性ではない」旨を ADR か設計メモに明文化する
