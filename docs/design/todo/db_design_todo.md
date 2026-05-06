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
| DB-01 | `public_id` を FK に使う設計意図を明記する | Closed | `id` と `public_id` の役割分担、および FK に `public_id` を使う理由を仕様書へ追記した | `posts.user_id -> users.public_id` など |
| DB-02 | インデックス設計と実装 SQL の整合を確認する | Closed | seek 条件、SQL 分割方針、bbox / reply 取得の記述を現状実装へ合わせて更新した | ADR-023 / ADR-025 と連動 |
| DB-03 | `id` と `public_id` の使い分けを固定する | Closed | API / 外部公開は `public_id`、内部主キー・内部ソートは `id` と明文化した | API への `id` 漏出防止 |
| DB-04 | ユーザー状態カラムの意味を固定する | Closed | `is_active`, `disabled`, `disabled_at` の意味と利用場面を仕様書で定義した | 認証済み / 無効化 / 論理削除の切り分け |
| DB-05 | `replies.child_count` の整合性責務を明記する | Closed | 派生値であることと、更新責務を application 層が持つこと、注意点を仕様書へ追記した | 返信追加・削除時の整合性 |

## 対応内容

### 1. `public_id` を FK に使う設計意図を明記する

対応済み:

- 実装は `BIGINT id` ではなく `CHAR(26) public_id` を公開参照キーとして使っている
- 一部テーブルでは FK も `public_id` を参照している

- `03_database.md` に、`public_id` を参照キーにも使う意図を記載する
- 内部主キー `id` を残している理由も併記する

### 2. インデックス設計と実装 SQL の整合を確認する

対応済み:

- 設計書では `created_at, id` の複合ソート前提へ更新済み
- 実装 SQL は初回表示用と継続取得用に分割し、継続取得では `lastId` から `created_at` / `id` を解決して seek 条件を組む方式へ修正済み

- `03_database.md` に SQL 分割方針と複合 seek 条件を反映した
- あわせて bbox 検索と返信取得の記述も現状の列名・取得方式へ修正した

### 3. `id` と `public_id` の使い分けを固定する

対応済み:

- 実装上は `id` と `public_id` が併存している
- 用途が曖昧だと、将来 API に内部 `id` を出してしまう危険がある

- `id` は内部主キー・内部ソート用
- `public_id` は API / 外部公開 / 外部参照用

という整理を仕様書へ明記した

### 4. ユーザー状態カラムの意味を固定する

対応済み:

- `users` には `is_active`, `disabled`, `disabled_at` が存在する
- それぞれの意味が仕様書上でまだ十分に固定されていない

- `is_active`: 何をもって true とするか
- `disabled`: 何をもって true とするか
- `disabled_at`: どのタイミングで設定するか

を仕様書で定義した

### 5. `replies.child_count` の整合性責務を明記する

対応済み:

- `child_count` は返信ツリーの派生値である
- 派生値である以上、どこで更新保証するかを決めておく必要がある

- `child_count` を派生値として明記する
- 返信作成・削除時の更新責務を application / repository / SQL のどこで持つか整理する
- `child_count` を派生値として明記した
- 返信作成・削除時の更新責務を application 層が持つ方針、および整合性上の注意点を記載した

## 運用メモ

- DB 設計書は現状実装ベースへ寄せる
- ただし、実装追随だけでは意図が伝わらない箇所は TODO として残す
- API 仕様やページング仕様と連動する項目は、ADR-023 やレビュー資料と整合を取る
