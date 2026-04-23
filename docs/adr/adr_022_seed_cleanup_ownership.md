# ADR: seed cleanup は seed ユーザー所有データを削除対象にする

## ステータス
Accepted

## 背景
local / stg の fixed seed scenario は、開発・検証でログイン可能な seed ユーザーを作成する。

この seed ユーザーは画面や API の確認にも利用されるため、seed が明示的に投入した投稿だけでなく、確認作業中に通常投稿や返信を作成することがある。

従来の cleanup は主に caption / message のラベルで seed データを識別していた。

- local: `"[LOCAL] %"`
- stg: `"[STG] %"`

この方式では、seed ユーザーが通常投稿を作成した場合、その投稿は cleanup 対象外になる。一方で seed ユーザー自体は email prefix で削除対象になるため、`posts.user_id -> users.public_id` の外部キー制約によりユーザー削除で失敗する。

実際に stg 環境で、`stg_seed_user_%@example.com` のユーザーに紐づく通常投稿が残り、起動時 cleanup の `DELETE FROM users ...` が外部キー制約違反で失敗した。

## 決定

1. seed cleanup は、seed ラベル付きデータだけでなく、seed ユーザーが所有・作成したデータも削除対象にする
2. seed ユーザーの判定は email prefix で行う
   - local: `local_seed_user_%@example.com`
   - stg: `stg_seed_user_%@example.com`
3. cleanup の削除順は、外部キー参照元から参照先へ進める
   - replies
   - post_images
   - posts
   - users
4. 物理画像の削除対象も、seed ユーザーの投稿に紐づく `post_images.object_key` を含める
5. 既存の seed ラベルによる削除条件も残す
   - 過去 seed や部分的に残ったラベル付きデータを回収するため

## 理由

- seed ユーザーは検証用アカウントであり、そのユーザーが作成したデータは cleanup 対象として扱うのが運用上自然である
- 通常投稿の caption はユーザー入力であり、seed 判定に使うには不十分である
- email prefix は seed ユーザーの安定識別子として既存 ADR でも利用している
- 参照元テーブルから削除することで、外部キー制約違反による起動失敗を避けられる
- ラベル条件を残すことで、seed ユーザー以外に紐づいた過去の seed ラベル付きデータも cleanup できる

## 運用ルール

- seed ユーザーで作成した投稿・返信は、cleanup 実行時に削除される前提で扱う
- seed ユーザーに保持させたい永続データを手動作成しない
- seed ユーザーの email prefix を変更する場合は、cleanup SQL と seed シナリオ ADR を同時に更新する
- 新しい seed 関連テーブルを追加した場合は、外部キー参照順に従って cleanup 対象へ追加する

## この方針で避けるもの

- seed ユーザー削除時の外部キー制約違反
- 検証操作で作成した通常投稿が cleanup 後も残ること
- seed cleanup の成否が caption / message の命名に過度に依存すること
- cleanup 失敗によって Railway などのデプロイ環境でヘルスチェックに到達できなくなること
