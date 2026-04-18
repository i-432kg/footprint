# `jp.i432kg.footprint.application.exception` パッケージ UT仕様書サマリー

## 1. 対象概要

- 対象パッケージ:
  - `jp.i432kg.footprint.application.exception`
  - `jp.i432kg.footprint.application.exception.resource`
  - `jp.i432kg.footprint.application.exception.usecase`
- 対象クラス:
  - `ApplicationException`
  - `ResourceNotFoundException`
  - `UseCaseExecutionException`
  - `PostNotFoundException`
  - `ReplyNotFoundException`
  - `UserNotFoundException`
  - `PostCommandFailedException`
  - `ReplyCommandFailedException`
  - `UserCommandFailedException`

## 2. 横断観点

| No. | 観点 | 内容 |
|---|---|---|
| 1 | 基本保持 | `errorCode`, `message`, `details`, `cause` を保持すること |
| 2 | 継承構造 | resource 系は `ResourceNotFoundException`、usecase 系は `UseCaseExecutionException` を継承すること |
| 3 | details 組み立て | `target`, `reason`, `rejectedValue` や ID 情報が仕様どおり格納されること |
| 4 | factory | usecase 系 static factory が期待する `message` / `details` / `cause` を組み立てること |

## 3. グルーピング方針

- 共通基底:
  - `ApplicationException`, `ResourceNotFoundException`, `UseCaseExecutionException`
  - 保持責務と継承関係を確認する
- resource 系:
  - `PostNotFoundException`, `ReplyNotFoundException`, `UserNotFoundException`
  - `ErrorCode`, message, details の ID 格納を確認する
- usecase 系:
  - `PostCommandFailedException`, `ReplyCommandFailedException`, `UserCommandFailedException`
  - 各 factory の `target`, `reason`, `rejectedValue`, `cause` を確認する

## 4. テスト実装メモ

- 抽象クラスはテスト用サブクラスをローカルに用意して保持値を確認する
- `details` は map 全体一致より、意味のある key-value を明示確認する
- usecase 系は各 static factory を個別に確認する
- `cause` あり/なしの違いは基底クラスと usecase 系で重点的に確認する
