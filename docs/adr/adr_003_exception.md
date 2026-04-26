# ErrorCode と HTTP ステータスの対応メモ

## 目的
プロジェクト内で扱う独自例外の `ErrorCode` と、API 応答時の HTTP ステータスの対応を明確にする。

## 方針
- 例外の種類は `ErrorCode` で識別する
- HTTP ステータスは `GlobalExceptionHandler` で決定する
- `details` は例外の補足情報として `ProblemDetail` に含める
- ユースケース失敗系は、基本的に「技術的失敗」か「入力起因」かで扱いを分ける

## 対応表

| ErrorCode              |               HTTP Status | 備考             |
|------------------------|--------------------------:|----------------|
| `POST_NOT_FOUND`       |             404 Not Found | 投稿が存在しない       |
| `REPLY_NOT_FOUND`      |             404 Not Found | 返信が存在しない       |
| `USER_NOT_FOUND`       |             404 Not Found | ユーザーが存在しない     |
| `EMAIL_ALREADY_USED`   |              409 Conflict | 一意制約・重複系       |
| `REPLY_POST_MISMATCH`  |           400 Bad Request | 親子関係の不整合       |
| `DOMAIN_INVALID_VALUE` |           400 Bad Request | 値オブジェクトの検証失敗   |
| `POST_COMMAND_FAILED`  | 500 Internal Server Error | 投稿作成ユースケース失敗   |
| `REPLY_COMMAND_FAILED` | 500 Internal Server Error | 返信作成ユースケース失敗   |
| `USER_COMMAND_FAILED`  | 500 Internal Server Error | ユーザー作成ユースケース失敗 |
| `UNEXPECTED_ERROR`     | 500 Internal Server Error | 想定外例外          |

## 補足
### 1. 400 と 500 の境界
- `DOMAIN_INVALID_VALUE` は主に入力値の不正なので 400
- `POST_COMMAND_FAILED` などは、実装上は保存失敗や I/O 失敗を含むため 500 を基本とする
- ただし将来的に「入力に起因するユースケース失敗」が明確なら 400 に変更してよい

### 2. 409 Conflict の採用理由
- `EMAIL_ALREADY_USED` はリソースの重複を表すため Conflict が自然

### 3. `details` の扱い
- `details` には `target`, `reason`, `rejectedValue` を基本として含める
- 必要に応じて追加情報を含める
- API クライアントは `errorCode` と `details` を見て判定できる

## 今後の運用
- 新しい独自例外を追加する場合は、この表に追記する
- HTTP ステータスの変更が必要な場合は、`GlobalExceptionHandler` と本メモを同時に更新する
