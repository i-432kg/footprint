# ADR: seed 固定シナリオの利用識別子を整理する

## ステータス
Accepted

## 背景
fixed seed scenario への移行により、seed で投入されるデータの状態は明確になった。

一方で、実際に開発や確認で使う際には次の情報が必要になる。

- どのユーザーがどの状態を表すのか
- どのメールアドレス / ユーザー名でログイン確認できるのか
- seed 実行後の `userId` は固定なのか
- どの投稿 / 返信がどの状態を表すのか

そこで、seed シナリオの識別子と利用方法を ADR として整理する。

## 決定

### 1. 実 `userId` は固定しない
- seed ユーザーの `userId` は作成時に ULID で生成される
- そのため `userId` 自体は環境や再投入タイミングで変わる
- seed データの安定識別子としては、メールアドレス・ユーザー名・caption・message を使う

### 2. ログイン用パスワードは環境変数 / 設定値で与える
- local: `app.local-seed.test-password`
- stg: `app.stg-seed.test-password`
- seed ユーザー全員が同じ test password を共有する

### 3. 固定シナリオは以下の状態を保証する
- 投稿ありユーザー
- 投稿なしユーザー
- 返信ありユーザー
- 返信なしユーザー
- 返信あり投稿
- 返信なし投稿
- 返信あり返信
- 返信なし返信

## シナリオ一覧

### Users

#### local
| シナリオ key | email | username | 状態 |
|---|---|---|---|
| `poster_with_replies` | `local_seed_user_poster_with_replies@example.com` | `loc_post_reply` | 投稿あり / 返信あり |
| `poster_without_replies` | `local_seed_user_poster_without_replies@example.com` | `loc_post_only` | 投稿あり / 返信なし |
| `non_poster_replier` | `local_seed_user_non_poster_replier@example.com` | `loc_reply_only` | 投稿なし / 返信あり |

#### stg
| シナリオ key | email | username | 状態 |
|---|---|---|---|
| `poster_with_replies` | `stg_seed_user_poster_with_replies@example.com` | `stg_post_reply` | 投稿あり / 返信あり |
| `poster_without_replies` | `stg_seed_user_poster_without_replies@example.com` | `stg_post_only` | 投稿あり / 返信なし |
| `non_poster_replier` | `stg_seed_user_non_poster_replier@example.com` | `stg_reply_only` | 投稿なし / 返信あり |

### Password

- local: `app.local-seed.test-password`
- stg: `app.stg-seed.test-password`

seed ユーザーは、その環境で設定された test password を共通で使う。

### Posts

#### local
- `"[LOCAL] post_with_replies"`: 返信あり投稿
- `"[LOCAL] post_without_replies"`: 返信なし投稿

#### stg
- `"[STG] post_with_replies"`: 返信あり投稿
- `"[STG] post_without_replies"`: 返信なし投稿

### Replies

#### local
- `"[LOCAL] reply_with_children"`: 子返信あり返信
- `"[LOCAL] reply_without_children"`: 子返信なし返信

#### stg
- `"[STG] reply_with_children"`: 子返信あり返信
- `"[STG] reply_without_children"`: 子返信なし返信

## 運用ルール

- seed データを参照する際は、`userId` ではなく email / username / caption / message を使って識別する
- API 検証などで `userId` が必要な場合は、seed 投入後に email から取得する
- seed シナリオを追加・変更した場合は、この ADR も更新する

## この方針で避けるもの

- 実行ごとに変わる `userId` を固定値として扱うこと
- seed 利用時の識別子の混乱
- ログイン検証に必要な情報の散逸
