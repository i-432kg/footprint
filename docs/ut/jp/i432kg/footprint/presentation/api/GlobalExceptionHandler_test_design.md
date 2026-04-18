# `GlobalExceptionHandler` テスト仕様書

## 1. 基本情報

- 対象クラス: `GlobalExceptionHandler`
- 対象パッケージ: `jp.i432kg.footprint.presentation.api`
- 対応するテストクラス: `GlobalExceptionHandlerTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をするクラスか: REST API 全体の例外を `ProblemDetail` へ変換する例外ハンドラ
- 主な依存: `SensitiveDataMasker`

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | resource 例外 | `PostNotFoundException` を 404 / `POST_NOT_FOUND` の `ProblemDetail` に変換すること |
| 2 | 正常系 | domain 例外 | `InvalidValueException` を 400 / `DOMAIN_INVALID_VALUE` の `ProblemDetail` に変換すること |
| 3 | 正常系 | validation 例外 | `MethodArgumentNotValidException` から field error 一覧を `details.errors` に詰めること |
| 4 | 正常系 | request body 解析失敗 | `HttpMessageNotReadableException` を 400 の validation error に変換すること |
| 5 | 正常系 | use case 例外 | `UseCaseExecutionException` を `ErrorCode` に応じた HTTP ステータスへ変換すること |
| 6 | 正常系 | 想定外例外 | `Exception` を 500 / `UNEXPECTED_ERROR` の `ProblemDetail` に変換すること |
| 7 | 正常系 | マスキング | 独自例外の `details` が `SensitiveDataMasker` の戻り値に置き換えられること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | 投稿未検出例外を変換する | `PostNotFoundException` | status=404, title=`Post Not Found`, `errorCode=POST_NOT_FOUND` |
| 2 | 正常系 | 不正値例外を変換する | `InvalidValueException.required(...)` | status=400, title=`Invalid Value`, `errorCode=DOMAIN_INVALID_VALUE` |
| 3 | 正常系 | Bean Validation 失敗を変換する | field error 1 件の `MethodArgumentNotValidException` | status=400, `details.errors[0].field/message/rejectedValue` を含む |
| 4 | 正常系 | request body 解析失敗を変換する | `HttpMessageNotReadableException` | status=400, title=`Validation Error`, `details.errors[0].field=requestBody` |
| 5 | 正常系 | use case 実行失敗を変換する | `PostCommandFailedException.persistenceFailed(...)` | status=500, title=`Use Case Error` |
| 6 | 正常系 | 想定外例外を変換する | `RuntimeException` | status=500, title=`Internal Server Error`, `errorCode=UNEXPECTED_ERROR` |
| 7 | 正常系 | details をマスクする | `BaseException.details` がある独自例外 | `ProblemDetail.details` が masker の戻り値と一致する |

## 5. 実装メモ

- `resolveStatus(...)` の全分岐を一度に網羅する必要はなく、代表的な 404 / 400 / 500 を優先する
- `MethodArgumentNotValidException` は `BeanPropertyBindingResult` から作る
- `ProblemDetail.getProperties()` から `errorCode`, `details` を取得して確認する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_createNotFoundProblemDetail_when_postNotFoundExceptionIsHandled` | `GlobalExceptionHandler は PostNotFoundException を 404 の ProblemDetail へ変換する` |
| 2 | `should_createBadRequestProblemDetail_when_invalidValueExceptionIsHandled` | `GlobalExceptionHandler は InvalidValueException を 400 の ProblemDetail へ変換する` |
| 3 | `should_createValidationProblemDetail_when_methodArgumentNotValidExceptionIsHandled` | `GlobalExceptionHandler は MethodArgumentNotValidException を validation error の ProblemDetail へ変換する` |
| 4 | `should_createValidationProblemDetail_when_httpMessageNotReadableExceptionIsHandled` | `GlobalExceptionHandler は HttpMessageNotReadableException を validation error の ProblemDetail へ変換する` |
| 5 | `should_createUseCaseProblemDetail_when_useCaseExecutionExceptionIsHandled` | `GlobalExceptionHandler は UseCaseExecutionException を対応ステータスの ProblemDetail へ変換する` |
| 6 | `should_createInternalServerErrorProblemDetail_when_unexpectedExceptionIsHandled` | `GlobalExceptionHandler は想定外例外を 500 の ProblemDetail へ変換する` |
| 7 | `should_useMaskedDetails_when_baseExceptionIsHandled` | `GlobalExceptionHandler は独自例外の details に SensitiveDataMasker の結果を使う` |
