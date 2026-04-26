# `jp.i432kg.footprint.logging` パッケージ UT仕様書サマリー

## 1. 対象概要

- 対象パッケージ: `jp.i432kg.footprint.logging`
- 対象クラス:
  - `trace.TraceIdFilter`
  - `masking.MaskingStrategy`
  - `masking.MaskingTarget`
  - `masking.SensitiveDataMasker`

## 2. 横断観点

| No. | 観点 | 内容 |
|---|---|---|
| 1 | 相関 ID | リクエスト単位で `traceId` を採番し、MDC・request・response header へ一貫して付与できること |
| 2 | 後始末 | 例外時を含め、処理後に `MDC` から `traceId` を除去できること |
| 3 | エラーレスポンス | `GlobalExceptionHandler` を通るレスポンスにも `X-Trace-Id` を付与できること |
| 4 | マスキング | 機微情報をキー名・型・`target` に応じて再帰的にマスクできること |

## 3. グルーピング方針

- `trace`
  - `TraceIdFilter` 単体で、採番・MDC 格納・ヘッダ付与・後始末を確認する
- `masking`
  - 既存の `MaskingStrategy`, `MaskingTarget`, `SensitiveDataMasker` でマスキング挙動を確認する

## 4. テスト実装メモ

- `TraceIdFilter` は `MockHttpServletRequest` / `MockHttpServletResponse` で単体確認する
- エラーレスポンスへの `X-Trace-Id` 付与は `MockMvc` の standalone setup と `GlobalExceptionHandler` を併用して確認する
- `traceId` の形式は ULID 26 文字で検証する
- `MDC` はスレッドローカルのため、各テスト後に値が残っていないことを確認する
