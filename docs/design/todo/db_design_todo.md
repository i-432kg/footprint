# DB設計 TODO

作成日: 2026-04-24

対象:

- `docs/design/03_database.md`
- `src/main/resources/db/migration/V1__init.sql`

## 目的

DB 設計書を現状実装ベースへ合わせる方針を前提にしつつ、設計差分として吸収するだけでは不十分な論点を整理する。

## 対応状況

| ID | 項目 | ステータス | 対応内容 | 備考 |
|---|---|---|---|---|
| DB-01 | `public_id` を FK に使う設計意図を明記する | Open | `id` と `public_id` の役割分担、および FK に `public_id` を使う理由を仕様書へ追記する | `posts.user_id -> users.public_id` など |
| DB-02 | インデックス設計と実装 SQL の整合を確認する | In Progress | 設計書は複合ソートキー前提へ更新済み。実装 SQL の seek 条件見直しが残る | ADR-023 と連動 |
| DB-03 | `id` と `public_id` の使い分けを固定する | Open | API / 外部公開は `public_id`、内部ソートや内部主キーは `id` と明文化する | API への `id` 漏出防止 |
| DB-04 | ユーザー状態カラムの意味を固定する | Open | `is_active`, `disabled`, `disabled_at` の意味と利用場面を仕様書で定義する | 認証済み / 無効化 / 論理削除の切り分け |
| DB-05 | `replies.child_count` の整合性責務を明記する | Open | 派生値であることと、更新責務をどこで持つか整理する | 返信追加・削除時の整合性 |

## TODO 詳細

### 1. `public_id` を FK に使う設計意図を明記する

状況:

- 実装は `BIGINT id` ではなく `CHAR(26) public_id` を公開参照キーとして使っている
- 一部テーブルでは FK も `public_id` を参照している

TODO:

- `03_database.md` に、`public_id` を参照キーにも使う意図を記載する
- 内部主キー `id` を残している理由も併記する

### 2. インデックス設計と実装 SQL の整合を確認する

状況:

- 設計書では `created_at, id` の複合ソート前提へ更新済み
- 実装 SQL はまだ `created_at` 中心の seek 条件であり、完全一致していない

TODO:

- `ORDER BY` と seek 条件が同じキー集合になるよう実装を見直す
- 必要なら index 定義も再確認する

### 3. `id` と `public_id` の使い分けを固定する

状況:

- 実装上は `id` と `public_id` が併存している
- 用途が曖昧だと、将来 API に内部 `id` を出してしまう危険がある

TODO:

- `id` は内部主キー・内部ソート用
- `public_id` は API / 外部公開 / 外部参照用

という整理を仕様書へ明記する

### 4. ユーザー状態カラムの意味を固定する

状況:

- `users` には `is_active`, `disabled`, `disabled_at` が存在する
- それぞれの意味が仕様書上でまだ十分に固定されていない

TODO:

- `is_active`: 何をもって true とするか
- `disabled`: 何をもって true とするか
- `disabled_at`: どのタイミングで設定するか

を仕様書で定義する

### 5. `replies.child_count` の整合性責務を明記する

状況:

- `child_count` は返信ツリーの派生値である
- 派生値である以上、どこで更新保証するかを決めておく必要がある

TODO:

- `child_count` を派生値として明記する
- 返信作成・削除時の更新責務を application / repository / SQL のどこで持つか整理する

## 運用メモ

- DB 設計書は現状実装ベースへ寄せる
- ただし、実装追随だけでは意図が伝わらない箇所は TODO として残す
- API 仕様やページング仕様と連動する項目は、ADR-023 やレビュー資料と整合を取る
