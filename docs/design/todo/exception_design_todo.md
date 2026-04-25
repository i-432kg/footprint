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
| EXC-01 | 例外階層の責務を固定する | Open | `BaseException` / `ApplicationException` / `DomainException` / `UseCaseExecutionException` / `ResourceNotFoundException` の責務と使い分けを明文化する | どの層で何を投げるか |
| EXC-02 | 具体例外の `details` 生成規約を統一する | Open | 具体例外は `target` / `reason` / `rejectedValue` を基本とし、例外的な追加項目のルールを決める | ADR-027 と連動 |
| EXC-03 | 例外メッセージの書式と用途を整理する | Open | `message` を人間向け説明としてどう保つか、ログ・API・テストでの扱いを整理する | 英語/日本語、可変値の含め方 |
| EXC-04 | `ErrorCode` と具体例外の対応を棚卸しする | Open | 1 例外 1 `ErrorCode` の原則、共通コードの許容範囲を見直す | `resolveStatus(...)` と整合 |
| EXC-05 | 生成ヘルパーの利用ルールを統一する | Open | `DetailBasedException.details(...)` を使うべき例外と独自 `Map.of(...)` を許容する例外を整理する | 実装のばらつき解消 |
| EXC-06 | resource not found 系 / validation 系 / use case 系の命名規則を整理する | Open | `XxxNotFoundException`, `XxxCommandFailedException`, `InvalidXxxException` の粒度を統一する | 命名の予測可能性 |
| EXC-07 | 例外設計の ADR を追加する | Open | 設計判断を ADR として固定する | 実装修正前に判断を確定 |
| EXC-08 | 例外クラス群のテスト観点を整理する | Open | 例外そのものの UT と `GlobalExceptionHandler` 側の責務境界を明確にする | message / details / errorCode |

## TODO 詳細

### 1. 例外階層の責務を固定する

状況:

- `BaseException` を基底として application / domain 例外がぶら下がっている
- ただし「domain で投げるべきか」「application で包むべきか」「resource not found はどこに属すべきか」の判断基準が文書化されていない

TODO:

- 各抽象例外の責務を定義する
- 各レイヤで投げてよい例外の範囲を整理する
- `GlobalExceptionHandler` が想定する例外階層と整合を取る

### 2. 具体例外の `details` 生成規約を統一する

状況:

- ADR-027 により `details` の正規形は定まった
- 一方で、具体例外クラスごとに `details(...)` を使うものと独自 `Map.of(...)` を使うものが混在してきた

TODO:

- 具体例外は原則 `target` / `reason` / `rejectedValue` を基本に構築する
- 追加項目 (`resourceId`, `min`, `max`, `expectedFormat` など) の使い方を整理する
- 正規形から外れる例外があれば例外ルールとして明記する

### 3. 例外メッセージの書式と用途を整理する

状況:

- 例外メッセージは英語のもの、日本語のもの、可変値を埋め込むものが混在している
- `ProblemDetail.detail`、ログ出力、デバッグ用途でどう使うかが明確ではない

TODO:

- `message` の主用途を決める
- 可変値をどこまで含めるか決める
- API 契約として安定させない項目を明確にする

### 4. `ErrorCode` と具体例外の対応を棚卸しする

状況:

- 例外ごとに `ErrorCode` を持つが、設計原則が明文化されていない
- 例外の粒度と `ErrorCode` の粒度が揃っているか再確認が必要

TODO:

- 具体例外と `ErrorCode` の対応表を作る
- 共通 `ErrorCode` を許容するか判断する
- HTTP ステータス対応との整合を確認する

### 5. 生成ヘルパーの利用ルールを統一する

状況:

- `DetailBasedException.details(...)` はあるが、全例外で一貫利用されていない
- そのため実装スタイルにばらつきが出やすい

TODO:

- 具体例外は原則 helper を利用する
- helper では表現しにくい場合だけ独自生成を許容する
- helper 側に追加すべき派生パターンがあるかを確認する

### 6. 命名規則を整理する

状況:

- `NotFound`, `Invalid`, `Failed`, `Mismatch` などの命名はあるが、どこまで分けるかが感覚的

TODO:

- resource not found 系の命名規則を固定する
- use case 失敗系の命名規則を固定する
- domain 不変条件違反系の命名規則を固定する

### 7. 例外設計の ADR を追加する

状況:

- 個別 ADR はあるが、例外クラス設計全体を統一する ADR はまだない

TODO:

- 例外階層
- `details` 生成規約
- `message` / `errorCode` / `detail` の役割分担
- helper 利用ルール

をまとめた ADR を作成する

### 8. テスト観点を整理する

状況:

- 例外変換は `GlobalExceptionHandlerTest` が主に担っている
- 一方で、例外クラス自体の `details` / `message` / `errorCode` を直接検証するテストは薄い

TODO:

- 例外クラス単体で確認すべき内容を決める
- handler 側で確認すべき内容との責務境界を決める
- テスト仕様書へ反映する

## 運用メモ

- `error_response_todo.md` は API 応答形式の TODO として扱う
- 本ファイルは独自例外クラス設計そのものの TODO として扱う
- まず設計判断を ADR 化し、その後に具体例外クラスの一括リファクタを進める
