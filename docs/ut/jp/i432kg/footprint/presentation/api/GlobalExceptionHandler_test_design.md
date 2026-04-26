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
| 1 | 正常系 | resource 例外 | `PostNotFoundException`, `ReplyNotFoundException`, `UserNotFoundException` を 404 の `ProblemDetail` に変換すること |
| 2 | 正常系 | domain 例外 | `InvalidValueException`, `ReplyPostMismatchException`, `InvalidModelException`, `EmailAlreadyUsedException` を対応ステータスへ変換すること |
| 3 | 正常系 | validation 例外 | `MethodArgumentNotValidException`, `BindException`, `ConstraintViolationException` から `target` / `reason` / `rejectedValue` を持つ error 一覧を `details.errors` に詰めること |
| 4 | 正常系 | 不足入力 | `MissingServletRequestParameterException`, `MissingServletRequestPartException` を validation error に変換すること |
| 5 | 正常系 | request body / 型変換失敗 | `HttpMessageNotReadableException`, `MethodArgumentTypeMismatchException` を validation error に変換すること |
| 6 | 正常系 | use case / generic handler | `UseCaseExecutionException`, `DomainException`, `ApplicationException` を `ErrorCode` に応じた HTTP ステータスへ変換すること |
| 7 | 正常系 | 想定外例外 | `Exception` を 500 / `UNEXPECTED_ERROR` の `ProblemDetail` に変換すること |
| 8 | 正常系 | マスキング | 独自例外の `details` が `SensitiveDataMasker` の戻り値に置き換えられること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | 投稿未検出例外を変換する | `PostNotFoundException` | status=404, title=`Post Not Found`, `errorCode=POST_NOT_FOUND` |
| 2 | 正常系 | 返信未検出例外を変換する | `ReplyNotFoundException` | status=404, title=`Reply Not Found`, `errorCode=REPLY_NOT_FOUND` |
| 3 | 正常系 | ユーザー未検出例外を変換する | `UserNotFoundException` | status=404, title=`User Not Found`, `errorCode=USER_NOT_FOUND` |
| 4 | 正常系 | 不正値例外を変換する | `InvalidValueException.required(...)` | status=400, title=`Invalid Value`, `errorCode=DOMAIN_INVALID_VALUE` |
| 5 | 正常系 | メール重複例外を変換する | `EmailAlreadyUsedException` | status=409, title=`Email Already Used`, `errorCode=EMAIL_ALREADY_USED` |
| 6 | 正常系 | 返信投稿不整合例外を変換する | `ReplyPostMismatchException` | status=400, title=`Reply Post Mismatch` |
| 7 | 正常系 | Bean Validation 失敗を変換する | field error 1 件の `MethodArgumentNotValidException` | status=400, `details.errors[0].target/reason/rejectedValue` を含む |
| 8 | 正常系 | BindException を変換する | field error 1 件の `BindException` | status=400, `details.errors[0].target/reason/rejectedValue` を含む |
| 9 | 正常系 | ConstraintViolation を変換する | violation 1 件の `ConstraintViolationException` | status=400, `details.errors[0].target/reason/rejectedValue` を含む |
| 10 | 正常系 | 必須パラメータ欠落を変換する | `MissingServletRequestParameterException` | status=400, `details.errors[0].target=<parameterName>`, `source=query` |
| 11 | 正常系 | 必須 multipart パート欠落を変換する | `MissingServletRequestPartException` | status=400, `details.errors[0].target=<partName>`, `source=multipart` |
| 12 | 正常系 | request body 解析失敗を変換する | `HttpMessageNotReadableException` | status=400, title=`Validation Error`, `details.errors[0].target=requestBody`, `source=body` |
| 13 | 正常系 | 型変換失敗を変換する | `MethodArgumentTypeMismatchException` | status=400, `details.errors[0].target=<name>`, `source=query` |
| 14 | 正常系 | use case 実行失敗を変換する | `PostCommandFailedException.persistenceFailed(...)` | status=500, title=`Use Case Error`, `details` は `target` / `reason` のみで `rejectedValue` を含まない |
| 15 | 正常系 | generic domain 例外を変換する | `InvalidModelException.invalid(...)` | status=400, title=`Domain Error` |
| 16 | 正常系 | generic application 例外を変換する | `ApplicationException` test double | status=500, title=`Application Error` |
| 17 | 正常系 | 想定外例外を変換する | `RuntimeException` | status=500, title=`Internal Server Error`, `errorCode=UNEXPECTED_ERROR` |
| 18 | 正常系 | details をマスクする | `BaseException.details` がある独自例外 | `ProblemDetail.details` が masker の戻り値と一致する |

## 5. 実装メモ

- `resolveStatus(...)` は少なくとも 404 / 400 / 409 / 500 の代表分岐を確認する
- `MethodArgumentNotValidException` と `BindException` は `BeanPropertyBindingResult` から作る
- `ConstraintViolationException` は mock violation で十分
- `ProblemDetail.getProperties()` から `errorCode`, `details` を取得して確認する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_createNotFoundProblemDetail_when_postNotFoundExceptionIsHandled` | `GlobalExceptionHandler は PostNotFoundException を 404 の ProblemDetail へ変換する` |
| 2 | `should_createNotFoundProblemDetail_when_replyNotFoundExceptionIsHandled` | `GlobalExceptionHandler は ReplyNotFoundException を 404 の ProblemDetail へ変換する` |
| 3 | `should_createNotFoundProblemDetail_when_userNotFoundExceptionIsHandled` | `GlobalExceptionHandler は UserNotFoundException を 404 の ProblemDetail へ変換する` |
| 4 | `should_createBadRequestProblemDetail_when_invalidValueExceptionIsHandled` | `GlobalExceptionHandler は InvalidValueException を 400 の ProblemDetail へ変換する` |
| 5 | `should_createConflictProblemDetail_when_emailAlreadyUsedExceptionIsHandled` | `GlobalExceptionHandler は EmailAlreadyUsedException を 409 の ProblemDetail へ変換する` |
| 6 | `should_createBadRequestProblemDetail_when_replyPostMismatchExceptionIsHandled` | `GlobalExceptionHandler は ReplyPostMismatchException を 400 の ProblemDetail へ変換する` |
| 7 | `should_createValidationProblemDetail_when_methodArgumentNotValidExceptionIsHandled` | `GlobalExceptionHandler は MethodArgumentNotValidException を validation error の ProblemDetail へ変換する` |
| 8 | `should_createValidationProblemDetail_when_bindExceptionIsHandled` | `GlobalExceptionHandler は BindException を validation error の ProblemDetail へ変換する` |
| 9 | `should_createValidationProblemDetail_when_constraintViolationExceptionIsHandled` | `GlobalExceptionHandler は ConstraintViolationException を validation error の ProblemDetail へ変換する` |
| 10 | `should_createValidationProblemDetail_when_missingRequestParameterExceptionIsHandled` | `GlobalExceptionHandler は MissingServletRequestParameterException を validation error の ProblemDetail へ変換する` |
| 11 | `should_createValidationProblemDetail_when_missingRequestPartExceptionIsHandled` | `GlobalExceptionHandler は MissingServletRequestPartException を validation error の ProblemDetail へ変換する` |
| 12 | `should_createValidationProblemDetail_when_httpMessageNotReadableExceptionIsHandled` | `GlobalExceptionHandler は HttpMessageNotReadableException を validation error の ProblemDetail へ変換する` |
| 13 | `should_createValidationProblemDetail_when_methodArgumentTypeMismatchExceptionIsHandled` | `GlobalExceptionHandler は MethodArgumentTypeMismatchException を validation error の ProblemDetail へ変換する` |
| 14 | `should_createUseCaseProblemDetail_when_useCaseExecutionExceptionIsHandled` | `GlobalExceptionHandler は UseCaseExecutionException を対応ステータスの ProblemDetail へ変換する` |
| 15 | `should_createDomainProblemDetail_when_domainExceptionIsHandled` | `GlobalExceptionHandler は DomainException を対応ステータスの ProblemDetail へ変換する` |
| 16 | `should_createApplicationProblemDetail_when_applicationExceptionIsHandled` | `GlobalExceptionHandler は ApplicationException を対応ステータスの ProblemDetail へ変換する` |
| 17 | `should_createInternalServerErrorProblemDetail_when_unexpectedExceptionIsHandled` | `GlobalExceptionHandler は想定外例外を 500 の ProblemDetail へ変換する` |
| 18 | `should_useMaskedDetails_when_baseExceptionIsHandled` | `GlobalExceptionHandler は独自例外の details に SensitiveDataMasker の結果を使う` |
