# ADR: ログ出力ルールの統一

## ステータス
Accepted

## 背景
本プロジェクトでは、API 層・Application 層・Domain 層・Infrastructure 層を分離し、独自例外を `ProblemDetail` に変換して返す設計を採用している。

例外設計が整ってきた一方で、ログ出力の方針が未統一だと、以下の問題が発生しやすい。

- 同じ例外が複数箇所で記録される
- 重要な障害と軽微な入力ミスが同じログレベルになる
- 調査時に必要な文脈が不足する
- 個人情報や秘匿情報が意図せずログに出る

そこで、プロジェクト全体でログ出力ルールを定義する。

## 決定
ログは以下の方針で出力する。

### 1. ログ出力の責務
- Controller は原則としてログを出しすぎない
- Application 層はユースケース失敗の文脈を記録する
- Infrastructure 層は技術障害の詳細を記録する
- GlobalExceptionHandler は想定外例外を記録し、必要に応じて業務例外も記録する

### 2. ログレベル
- `DEBUG`
    - 開発時の補助情報
    - 詳細な処理フロー
- `INFO`
    - 重要だが異常ではないイベント
    - バリデーション失敗の軽い記録
- `WARN`
    - 想定内の業務例外
    - 入力不正
    - リトライ不要な失敗
- `ERROR`
    - 想定外例外
    - 外部 I/O 失敗
    - DB 障害
    - システム障害

### 3. ログに含める情報
可能な範囲で以下を含める。

- `errorCode`
- `usecase`
- `target`
- `reason`
- `resourceId`
- `request path`
- `HTTP method`
- `traceable identifier`

### 4. ログに含めない情報
以下は原則として出力しない。

- パスワード
- アクセストークン
- セッション情報
- ファイルの中身
- 画像のバイナリ
- 大量のリクエスト本文
- 個人情報の過剰な生出力

### 5. 例外とログの関係
- 例外は投げる場所で文脈を持たせる
- ログは基本的に上位層で一度だけ出す
- 同じ例外を複数層で重複してログ出力しない
- 想定内例外は WARN、想定外例外は ERROR を基本とする

## レベル別の運用指針

### API 層
- バリデーションエラーは軽いログに留める
- 400 系は WARN か INFO
- 500 系は ERROR

### Application 層
- ユースケース単位の失敗を必要に応じて WARN で記録する
- 例外を業務文脈で補足する

### Domain 層
- 原則としてログ出力しない
- ドメイン不変条件違反は例外として上位へ返す

### Infrastructure 層
- 技術的失敗の原因を記録する
- 例: ファイル保存失敗、画像解析失敗、DB 書き込み失敗

## GlobalExceptionHandler の運用
- `Exception.class` を捕捉した場合は ERROR で記録する
- `ApplicationException` / `DomainException` / `UseCaseExecutionException` は必要に応じて WARN/INFO とする
- バリデーションエラーは通常 ERROR にしない
- `ProblemDetail` へは `errorCode` と `details` を含める

## 推奨するログ項目
### バリデーションエラー
- `errorCode`
- `path`
- `field`
- `message`

### 業務例外
- `errorCode`
- `usecase`
- `target`
- `reason`
- `resourceId`

### 想定外例外
- `errorCode`
- `path`
- `requestId`
- `stacktrace`

## 今後の方針
- Controller での個別 `catch` は極力避ける
- ログ出力位置を追加する場合は、まず「どの層で一度だけ記録するか」を決める
- 新しい例外種別を追加した場合は、ログレベルと記録項目も同時に決める

## 補足
本 ADR は例外設計と API レスポンス設計に密接に関係する。
そのため、`ErrorCode` の追加や `GlobalExceptionHandler` の更新時には本 ADR も合わせて更新する。