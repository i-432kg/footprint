# ADR: failure / warning 系ログの event は path ではなく operation から解決する

## ステータス
Accepted

## 背景

`LOG-06` の read 系成功イベントでは、controller が event 名と補助項目を設定し、`AccessLogFilter` が `footprint.access` へ 1 リクエスト 1 本で集約する方針を採用した。

一方で、failure / warning 系イベントでは、`MethodArgumentNotValidException`、`ConstraintViolationException`、`MissingServletRequestPartException`、`MethodArgumentTypeMismatchException` など、controller 本体に入る前後で発生し得る共通例外を `GlobalExceptionHandler` で処理している。

このとき、`GlobalExceptionHandler` が `path + method + exception type` から event を直接解決する設計には次の問題がある。

1. API エンドポイントが増えるたびに URL ベースの分岐が増える
2. 同じユースケースでも path の表現差分に引きずられやすい
3. validation 失敗と業務警告の解決責務が `GlobalExceptionHandler` に過剰集中する
4. success 系で使っている request 内のログ文脈と、failure 系で使う分類軸が分断される

実務上は URL ではなく「そのリクエストが何の操作をしようとしていたか」を安定キーとして持ち、そこから success / failure event を解決する方が保守しやすい。

## 決定

failure / warning 系ログの event 解決は path ベースで行わず、request に設定した `operation` を基準に行う。

### 1. `operation` の位置づけ

`operation` は、HTTP リクエストがどのユースケース操作を表すかを示す安定識別子とする。

例:

- `POST_CREATE`
- `REPLY_CREATE`
- `POST_TIMELINE_FETCH`
- `POST_SEARCH_FETCH`
- `POST_MAP_BBOX_FETCH`
- `POST_DETAIL_FETCH`
- `REPLY_LIST_FETCH`
- `ME_FETCH`
- `ME_POSTS_FETCH`
- `ME_REPLIES_FETCH`

### 2. request 内の保持場所

`operation` は `AccessLogContext` に保持する。

これにより、1 リクエスト分のログ文脈は次の 3 要素で一元管理する。

- `operation`
- `event`
- `fields`

### 3. 設定タイミング

`operation` は controller 本体ではなく、handler mapping が確定した時点で request へ設定する。

そのため、controller メソッドへ annotation で operation を宣言し、`HandlerInterceptor` などで `preHandle` 時に `AccessLogContext` へ格納する方式を基本とする。

これにより、controller 本体実行前に発生する validation 失敗でも同じ `operation` を参照できる。

### 4. success / failure の関係

- success 系では、必要に応じて `event` を `operation` と同じ値または対応する成功イベント名へ設定する
- failure / warning 系では、`GlobalExceptionHandler` が `operation + exception type` から failure event を解決する

`operation` は内部の安定識別子、`event` は最終的にログへ出す分類名として扱う。

### 5. `GlobalExceptionHandler` の責務

`GlobalExceptionHandler` は URL や HTTP メソッドの文字列分岐を直接持たず、`FailureEventResolver` のような専用 collaborator へ event 解決を委譲する。

resolver は `operation + exception type` を元に次のような event を返す。

- `POST_CREATE_VALIDATION_FAIL`
- `POST_CREATE_UPLOAD_REJECTED`
- `REPLY_CREATE_VALIDATION_FAIL`
- `POST_LAST_ID_INVALID`
- `REPLY_LAST_ID_INVALID`

### 6. path ベース分岐の扱い

path ベース分岐は原則採用しない。

どうしても `operation` や例外種別だけでは識別できないケースに限り、resolver 内部の補助情報として最小限参照してよいが、`GlobalExceptionHandler` 本体へ URL 分岐を持ち込まない。

## 理由

- URL ではなく操作名を分類軸にした方が、API 拡張時の分岐増加を抑えやすい
- success と failure が同じ `operation` 軸で整理され、設計が一貫する
- controller 本体に入る前の validation 失敗でも、同じ request 文脈から event を解決できる
- `GlobalExceptionHandler` を薄く保ち、例外整形と event 解決責務を分離できる
- `AccessLogContext` を request 内の単一ログ文脈として育てやすい

## 影響

### 良い影響

- failure / warning 系イベント追加時に URL 分岐を増やさずに済む
- ログ設計が endpoint 名ではなくユースケース操作にひもづく
- `GlobalExceptionHandler` の保守性が上がる

### 注意点

- 全 endpoint に対して operation 定義が必要になる
- operation 名の命名規則が曖昧だと、逆に分類軸がぶれる
- annotation と interceptor の導入後は、operation 未設定 endpoint の扱いを明確にする必要がある

## 実装方針

1. `LoggingOperations` などの operation 定数を追加する
2. `AccessLogContext` に `operation` を追加する
3. controller メソッドへ operation を宣言する annotation を追加する
4. `HandlerInterceptor` で annotation から operation を request へ設定する
5. `FailureEventResolver` を追加し、`GlobalExceptionHandler` から委譲する

## 既存 ADR との関係

- [adr_007_logging.md](./adr_007_logging.md) のログ責務整理を、failure / warning 系 event 解決方式まで具体化する判断である
- [adr_030_access_log_context.md](./adr_030_access_log_context.md) の `AccessLogContext` を、success 系だけでなく failure 系 event 解決にも使う方針を追加で明確化する
