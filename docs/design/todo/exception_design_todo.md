# 例外設計 TODO

作成日: 2026-04-26

対象:

- `src/main/java/jp/i432kg/footprint/application/exception/**`
- `src/main/java/jp/i432kg/footprint/domain/exception/**`
- `src/main/java/jp/i432kg/footprint/exception/**`
- `src/main/java/jp/i432kg/footprint/presentation/api/GlobalExceptionHandler.java`
- `docs/adr/adr_003_exception.md`
- `docs/adr/adr_024_problem_detail_error_response_policy.md`
- `docs/adr/adr_027_problem_detail_details_structure.md`

## 目的

独自例外クラスが場当たり的に追加されてきた結果、責務、命名、`details` の持ち方、メッセージ粒度に統一感がない。

この TODO では、`ProblemDetail` 応答そのものではなく、例外クラス設計を統一するための論点を整理する。

## 対応状況

| ID | 項目 | ステータス | 対応内容 | 備考 |
|---|---|---|---|---|
| EXC-01 | 例外階層の責務を固定する | Closed | 基底例外へ JavaDoc を追加し、`project_conventions.md` に `DomainException` / `ResourceNotFoundException` / `UseCaseExecutionException` の送出基準と包み替え基準を明文化した | 各レイヤで何をそのまま返し、何を包むかを固定 |
| EXC-02 | 具体例外の `details` 生成規約を統一する | Closed | ADR-027 に基づき、resource not found 系、validation 系、use case 系、業務例外系の `details` を `target` / `reason` 中心へ揃えた | `rejectedValue` は任意項目 |
| EXC-03 | 例外メッセージの書式と用途を整理する | Closed | `message` は人間向け説明、非契約、英語統一、機微情報非含有、`errorCode` / `details` で機械判定する方針を `project_conventions.md` に明文化した | helper と個別文面の使い分けも整理 |
| EXC-04 | `ErrorCode` と具体例外の対応を棚卸しする | Closed | 未使用 `ErrorCode` を削除し、現実装の例外と `ErrorCode` の対応表を整理した | 現時点では粒度・HTTP ステータス対応とも妥当と判断 |
| EXC-05 | 生成ヘルパーの利用ルールを統一する | Closed | `DetailBasedException.details(...)` の overload を整理し、具体例外は helper または専用基底 helper を使う形へ揃えた | `ResourceNotFoundException` は専用 helper |
| EXC-06 | resource not found 系 / validation 系 / use case 系の命名規則を整理する | Closed | 例外命名の草案を作成し、`project_conventions.md` に `NotFound`, `InvalidValue`, `InvalidModel`, `Mismatch`, `AlreadyUsed`, `CommandFailed` の使い分けを反映した | 命名の予測可能性を確保 |
| EXC-07 | 例外設計の ADR を追加する | Closed | 例外詳細の本命設計を typed details DTO とし、リリース前は `Map<String, Object>` を維持する方針を ADR-028 に整理した | リリース後の段階移行方針 |
| EXC-08 | 例外クラス群のテスト観点を整理する | Closed | 例外単体テストと `GlobalExceptionHandler` / `ContentType` テスト、UT 仕様書を更新し、責務境界を整理した | message / details / errorCode / ProblemDetail |

## TODO 詳細

### 1. 例外階層の責務を固定する

対応済み:

- `BaseException`, `ApplicationException`, `DomainException`, `UseCaseExecutionException`, `ResourceNotFoundException` に JavaDoc を追加した
- `ResourceNotFoundException` は `not_found` 構造、`UseCaseExecutionException` はユースケース失敗構造という責務をコード上で明文化した
- `docs/project_conventions.md` に以下を追記した
  - `DomainException` の送出基準
  - `ResourceNotFoundException` の送出基準
  - `UseCaseExecutionException` の送出基準
  - application 層での包み替え基準
  - presentation 層で独自例外を増やさない原則

### 2. 具体例外の `details` 生成規約を統一する

対応済み:

- resource not found 系は `target` / `reason=not_found` / `resourceId` に統一した
- validation / domain 例外は `target` / `reason` / `rejectedValue` を基本に揃えた
- `ReplyPostMismatchException` は `expectedPostId` / `actualPostId` を持つ例外ルールへ整理した
- 500 系 `UseCaseExecutionException` は `rejectedValue` を持たず、`target` / `reason` のみ返す運用へ整理した

### 3. 例外メッセージの書式と用途を整理する

対応済み:

- `message` は人間向け説明とする
- `message` は API 契約の安定識別子として扱わない
- クライアントの機械判定は `errorCode` と `details` を使う
- `message` は原則英語で統一する
- 機微情報は `message` に含めない
- `rejectedValue` や補足値は原則 `details` に寄せる
- 汎用例外は helper による共通書式、個別業務例外は独自文面を許容する

反映先:

- `docs/project_conventions.md`

### 4. `ErrorCode` と具体例外の対応を棚卸しする

対応済み:

- 未使用の `FILE_STORAGE_ERROR`, `PERSISTENCE_ERROR` を削除した
- `adr_003_exception.md` と `GlobalExceptionHandler.resolveStatus(...)` の整合を取り直した
- 現実装の `具体例外 -> ErrorCode -> 想定 HTTP ステータス` 対応表を作成した
- 現時点の粒度は実装・運用上問題ないと判断し、追加の分割は行わない

現実装での対応表:

| レイヤ | 具体例外 | `ErrorCode` | 想定 HTTP ステータス | 備考 |
|---|---|---|---|---|
| application | `PostNotFoundException` | `POST_NOT_FOUND` | `404 Not Found` | `ResourceNotFoundException` 派生 |
| application | `ReplyNotFoundException` | `REPLY_NOT_FOUND` | `404 Not Found` | `ResourceNotFoundException` 派生 |
| application | `UserNotFoundException` | `USER_NOT_FOUND` | `404 Not Found` | `ResourceNotFoundException` 派生 |
| application | `PostCommandFailedException` | `POST_COMMAND_FAILED` | `500 Internal Server Error` | `imageSaveFailed`, `imageMetadataExtractFailed`, `persistenceFailed` を内包 |
| application | `ReplyCommandFailedException` | `REPLY_COMMAND_FAILED` | `500 Internal Server Error` | `saveFailed`, `increaseReplyCountFailed` を内包 |
| application | `UserCommandFailedException` | `USER_COMMAND_FAILED` | `500 Internal Server Error` | `saveFailed` を内包 |
| domain | `InvalidValueException` | `DOMAIN_INVALID_VALUE` | `400 Bad Request` | 値オブジェクト単位の入力不正 |
| domain | `InvalidModelException` | `DOMAIN_INVALID_MODEL` | `400 Bad Request` | モデル不変条件違反 |
| domain | `ReplyPostMismatchException` | `REPLY_POST_MISMATCH` | `400 Bad Request` | 関係不整合 |
| domain | `EmailAlreadyUsedException` | `EMAIL_ALREADY_USED` | `409 Conflict` | 重複禁止 |
| presentation | `Exception` | `UNEXPECTED_ERROR` | `500 Internal Server Error` | `GlobalExceptionHandler` の汎用ハンドラで付与 |

### 5. 生成ヘルパーの利用ルールを統一する

対応済み:

- `DetailBasedException.details(...)` に `rejectedValue` なしの overload を追加した
- `ReplyPostMismatchException` を helper ベースへ整理し、ダミー `rejectedValue` を廃止した
- 具体例外は `DetailBasedException` helper または `ResourceNotFoundException` 専用 helper を利用する形へ揃えた

### 6. 命名規則を整理する

対応済み:

- 例外命名の草案を作成した
- `docs/project_conventions.md` に以下の使い分けを反映した
  - `XxxNotFoundException`
  - `InvalidValueException`
  - `InvalidModelException`
  - `XxxMismatchException`
  - `XxxAlreadyUsedException`
  - `XxxCommandFailedException`

補足:

- 現実装の具体例外名は、上記草案と整合しているため追加リネームは行っていない

### 7. 例外設計の ADR を追加する

対応済み:

- 個別 ADR はあるが、例外クラス設計全体を統一する ADR はまだない

- ADR-028 で、例外詳細の本命設計を typed details DTO とする方針を追加した
- 当面は `BaseException` の `Map<String, Object>` を維持し、リリース後に段階移行する方針を明記した

今後の課題:

- 例外階層
- `details` 生成規約
- `message` / `errorCode` / `detail` の役割分担
- helper 利用ルール

をさらに具体化する補助 ADR が必要なら追加する

### 8. テスト観点を整理する

対応済み:

- 例外クラス単体テストを `details` / `message` / `errorCode` 観点で更新した
- `GlobalExceptionHandlerTest` と `GlobalExceptionHandlerContentTypeTest` により、`ProblemDetail` 変換責務との境界を整理した
- UT 仕様書も例外単体と handler 側の責務に合わせて更新した

## 運用メモ

- `error_response_todo.md` は API 応答形式の TODO として扱う
- 本ファイルは独自例外クラス設計そのものの TODO として扱う
- まず設計判断を ADR 化し、その後に具体例外クラスの一括リファクタを進める
