# `TraceIdFilter` UT仕様書

## 1. 対象概要

- 対象クラス: `jp.i432kg.footprint.logging.trace.TraceIdFilter`
- 主な責務:
  - リクエストごとの `traceId` 採番
  - `MDC` 格納
  - request attribute 格納
  - `X-Trace-Id` レスポンスヘッダ返却
  - 例外時を含む `MDC` 後始末

## 2. 観点

| No. | 観点 | 内容 |
|---|---|---|
| 1 | 採番 | 各リクエストで ULID 形式の `traceId` を生成すること |
| 2 | 伝播 | request attribute、response header、MDC の値が一致すること |
| 3 | 後始末 | 正常終了時に `MDC` から `traceId` を除去すること |
| 4 | 例外時後始末 | 下流で例外が発生しても `MDC` を除去すること |
| 5 | エラーレスポンス | `GlobalExceptionHandler` を通るレスポンスにも `X-Trace-Id` を付与すること |

## 3. テストケース

| No. | 分類 | テスト内容 | 入力 / 条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | `traceId` を MDC / request / response に設定する | 通常リクエスト | 3 箇所に同一値が入り、ULID 形式である |
| 2 | 正常系 | 正常終了後に `MDC` をクリアする | 通常リクエスト | `MDC.get("traceId") == null` |
| 3 | 異常系 | 下流例外時も `MDC` をクリアする | `FilterChain` が例外送出 | `MDC.get("traceId") == null` |
| 4 | 正常系 | 成功レスポンスへ `X-Trace-Id` を付与する | 200 レスポンス | ヘッダが存在する |
| 5 | 正常系 | `GlobalExceptionHandler` 経由のエラーレスポンスへ `X-Trace-Id` を付与する | 404 レスポンス | ヘッダが存在する |

## 4. 実装メモ

- 単体観点は `MockHttpServletRequest`, `MockHttpServletResponse` を使う
- エラーレスポンス観点は `MockMvcBuilders.standaloneSetup(...)` と `GlobalExceptionHandler` を組み合わせる
- 生成値の文字種は ULID 正規表現 `^[0-9A-HJKMNP-TV-Z]{26}$` で確認する
