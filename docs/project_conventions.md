# プロジェクト規約

作成日: 2026-04-26

## 1. 目的と適用範囲

### 1.1 目的

- 本規約は、本プロジェクトにおける実装判断、レビュー判断、ドキュメント更新判断を統一するための基準とする
- 見た目の統一だけでなく、責務分担、例外設計、ログ設計、テスト方針などの設計判断も対象とする

### 1.2 適用範囲

- 対象リポジトリ: `footprint`
- 対象成果物:
  - Java 実装コード
  - テストコード
  - 設計資料
  - ADR
  - TODO 管理資料

### 1.3 関連資料

- ADR 一覧: `docs/adr/README.md`
- UT 仕様書ルール: `docs/ut/README.md`
- 設計 TODO: `docs/design/todo`

## 2. アーキテクチャ方針

### 2.1 レイヤ構成

- `domain`
- `application`
- `presentation`
- `infrastructure`

### 2.2 依存方向

- `presentation -> application -> domain`
- `infrastructure -> application / domain`
- `domain` は他レイヤへ依存しない

### 2.3 各レイヤの責務

- `domain`
  - 値オブジェクト、ドメインモデル、不変条件、業務ルール
- `application`
  - ユースケース実行、トランザクション境界、外部依存との調停
- `presentation`
  - HTTP 入出力、最低限バリデーション、DTO 変換
- `infrastructure`
  - DB、ストレージ、Security、外部サービスとの接続

### 2.4 禁止事項

- `domain` から framework 実装へ依存しない
- `presentation` に業務ルールを持ち込まない
- `infrastructure` に業務判断を持ち込まない

## 3. ディレクトリ構成

### 3.1 基本方針

- パッケージはレイヤ責務に従って配置する
- クラス名だけでなく、配置ディレクトリからも責務が読める構成とする

### 3.2 主な配置先

- `src/main/java/jp/i432kg/footprint/domain/**`
- `src/main/java/jp/i432kg/footprint/application/**`
- `src/main/java/jp/i432kg/footprint/presentation/**`
- `src/main/java/jp/i432kg/footprint/infrastructure/**`
- `src/test/java/jp/i432kg/footprint/**`
- `docs/adr/**`
- `docs/design/**`
- `docs/ut/**`

### 3.3 新規追加時の判断基準

- [ ] どのレイヤ責務か明確か
- [ ] 既存の近い概念と同じ階層に置けているか
- [ ] 命名だけでなく配置も一貫しているか

## 4. 命名規則

### 4.1 クラス名

- クラス名は役割が読める名詞とする
- 抽象基底には責務を示す語を含める

例:

- `PostCommandService`
- `ReplyQueryService`
- `GlobalExceptionHandler`

### 4.2 メソッド名

- 動詞始まりで、何をするかを明確にする
- boolean は `is`, `has`, `exists` で始める

### 4.3 例外クラス名

- resource 不在: `XxxNotFoundException`
- 単一値検証: `InvalidValueException`
- モデル不変条件: `InvalidModelException`
- 関係不整合: `XxxMismatchException`
- 重複禁止: `XxxAlreadyUsedException`
- application 書き込み失敗: `XxxCommandFailedException`

### 4.4 テストメソッド名

- `should_<expected>_when_<condition>` を基本とする
- `@DisplayName` は日本語で観点が読めるようにする

## 5. コーディングスタイル

### 5.1 `final`

- 引数には原則 `final` を付与する
- ローカル変数も再代入しないものは `final` を優先する

### 5.2 factory / constructor

- public な生成入口は static factory を優先する
- private constructor には non-null 前提の内部表現を渡す

### 5.3 可読性

- 複雑な分岐は早期 return を優先する
- 過度な共通化より、責務が読みやすいことを優先する

### 5.4 Lombok

- 定型コード削減の範囲で利用する
- 可読性や契約が不明瞭になる使い方は避ける

## 6. Nullability ルール

### 6.1 基本方針

- パッケージ単位で `NullMarked` を前提とする
- `@Nullable` は外部入力境界に限定して付与する

### 6.2 `@Nullable` を付ける場所

- request / mapper / adapter の境界
- 値オブジェクトの public factory 引数

### 6.3 non-null 前提

- domain 内部
- private constructor
- private helper

## 7. 値オブジェクト・ドメインモデル方針

### 7.1 値オブジェクト

- 単一の意味ある値を包むものを値オブジェクトとする

例:

- `EmailAddress`
- `PostId`
- `Latitude`

### 7.2 ドメインモデル

- 値オブジェクトを組み合わせて業務上の意味を持つものをドメインモデルとする

例:

- `Location`
- `BoundingBox`

### 7.3 ID の使い分け

- `id`: DB 内部主キー
- `public_id`: API や外部公開で使用する識別子

## 8. バリデーション方針

### 8.1 Controller

- 必須
- 型
- HTTP パラメータとしての最低限チェック

### 8.2 Domain

- 業務ルール
- 値範囲
- 不変条件
- 複合条件

### 8.3 application

- 参照整合
- ユースケース内の存在確認
- 実行順制御

## 9. 例外設計

### 9.1 例外階層

- `BaseException`
- `ApplicationException`
- `DomainException`
- `UseCaseExecutionException`
- `ResourceNotFoundException`

### 9.1.1 送出基準

- `DomainException`
  - 値オブジェクト、ドメインモデル、ドメインサービスが業務ルール違反を検知したときに送出する
  - 対象は単一値の不正、不変条件違反、関係不整合、重複禁止など
  - application 層で catch して別例外へ包み替えるのではなく、原則そのまま上位へ伝播させる
- `ResourceNotFoundException`
  - application 層が参照対象の存在確認を行い、必要なリソースが見つからないときに送出する
  - 対象は投稿、返信、ユーザーなどの参照不在
  - domain 層では送出しない
- `UseCaseExecutionException`
  - application service がユースケース実行中に継続不能な失敗を検知したときに送出する
  - 対象は永続化失敗、外部 I/O 失敗、補償処理を含む技術的失敗
  - 業務ルール違反を表す目的では使わない
- `ApplicationException`
  - application 層固有の異常を表す基底として扱う
  - 具体例外は `ResourceNotFoundException` と `UseCaseExecutionException` を優先し、直接派生は例外的な場合に限る
- `BaseException`
  - 直接送出しない
  - `ErrorCode`, `details`, `message` を持つ独自例外の共通基底としてのみ扱う

### 9.1.2 包み替え基準

- domain 起因の妥当性エラーは `DomainException` のまま返す
- repository / storage / framework 由来の実行失敗は application 層で `UseCaseExecutionException` へ包む
- 参照不在は application 層で `ResourceNotFoundException` へ変換する
- presentation 層では原則独自例外を新設せず、framework validation 例外と `GlobalExceptionHandler` で扱う
- 上記に当てはまらない例外は `Exception` として `UNEXPECTED_ERROR` へ流す

### 9.2 `ErrorCode`

- 独自例外は `ErrorCode` を必ず持つ
- 未使用の `ErrorCode` は残さない

### 9.3 `details`

- 正規項目は `target`, `reason`, `rejectedValue`
- `rejectedValue` は任意
- 追加項目は `resourceId`, `min`, `max`, `expectedFormat` など必要時のみ

### 9.4 `message`

- 人間向け説明とする
- クライアントの機械判定に利用させない

### 9.4.1 `message` の運用ルール

- `message` は API 契約の安定識別子として扱わない
- クライアントは `message` ではなく `errorCode` と `details` を用いて機械判定する
- `message` は原則英語で統一する
- 機微情報は `message` に含めない
- `rejectedValue` や補足値は原則 `details` に寄せる
- `ProblemDetail.detail` は原則として例外の `message` を利用するため、ログと API の両方に出る前提で保守する

### 9.4.2 helper と個別文面の使い分け

- 汎用 validation 系と use case failure 系は helper による共通書式を優先する
- 個別業務例外は、意味が明確になる場合に限って独自文面を許容する
- 想定内例外は簡潔な説明にとどめ、内部実装の詳細は `message` に含めない

## 10. API 設計ルール

### 10.1 基本方針

- request / response DTO を明示する
- domain モデルを直接返さない

### 10.2 エラー応答

- 標準形式は `ProblemDetail`
- Content-Type は `application/problem+json`
- 機械判定は `errorCode`
- 補足情報は `details`

### 10.3 ページング

- seek pagination を基本とする
- 境界条件は `created_at DESC, id DESC` と整合させる

## 11. DB 設計ルール

### 11.1 主キー / 外部キー

- `id` は内部主キー
- `public_id` は公開識別子
- 採用方針と例外は設計書に明記する

### 11.2 状態系カラム

- 未使用状態カラムも意味論を定義してから追加する

### 11.3 派生値

- `child_count` のような派生値は更新責務を明記する

## 12. ログ・マスキング方針

### 12.1 ログレベル

- `INFO`: 正常主要イベント
- `WARN`: 想定内異常
- `ERROR`: 想定外または継続困難

### 12.2 機微情報

- password, token, email, objectKey, fileName などは生で出さない
- `SensitiveDataMasker` と ADR-008 に従う

### 12.3 例外ログ

- API 例外ログは `errorCode` と `details` を基本とする
- 500 系 `UseCaseExecutionException` は `rejectedValue` を持たない

### 12.4 seed ログ例外

- local / stg の fixed seed scenario に限り、固定ダミーデータの詳細出力を許容する

## 13. 外部 I/O と補償処理

### 13.1 外部 I/O

- ストレージ、DB、Security などの失敗は application / infrastructure で責務分担する

### 13.2 cleanup

- 補償処理失敗は一次障害を上書きしない
- 元例外を優先し、cleanup 失敗はログへ残す

## 14. テスト方針

### 14.1 単体テスト

- service / helper / exception / value object 単位で責務を検証する

### 14.2 handler / controller

- `ProblemDetail` 変換、HTTP ステータス、DTO 変換を確認する
- `Content-Type` のような HTTP 契約は必要に応じて MockMvc 補足テストで確認する

### 14.3 命名

- テストメソッド名は `should_..._when_...`
- `@DisplayName` は日本語

## 15. ドキュメント作成ルール

### 15.1 ADR

- 設計判断が将来の実装・レビューに影響する場合は ADR を作成する

### 15.2 TODO

- 未解決の設計差分、後続課題は TODO で管理する

### 15.3 UT 仕様書

- 対象クラスごとに `*_test_design.md` を作成する
- パッケージサマリー `00_summary.md` を合わせて管理する

## 16. コメント・JavaDoc ルール

### 16.1 コメント

- コメントは「なぜそうするか」を補うために書く
- コードを読めば分かる逐語説明は避ける

### 16.2 JavaDoc

- public class
- public / protected method
- 契約や失敗条件が重要な factory / helper

を優先対象とする

## 17. 設定値・profile 運用

### 17.1 profile

- `local`
- `stg`
- `prod`

の責務を分ける

### 17.2 seed

- `local` / `stg` の seed 実行条件、cleanup 条件、ログ例外ルールを明記する

## 18. セキュリティ方針

### 18.1 認証

- 原則 API は認証必須とする
- 例外公開 API は明示する

### 18.2 画像配信

- 本番系は presigned URL を基本とする
- local の `/images/**` はローカル配信用途とする

### 18.3 情報露出

- レスポンスとログの双方で機微情報制御を行う

## 19. 規約違反時の判断

### 19.1 一時例外

- 規約から外れる必要がある場合は、理由を TODO または ADR に残す

### 19.2 恒久例外

- 恒久的な例外ルールは規約本文へ反映する

## 20. 付録

### 20.1 参照 ADR

- 例外設計: ADR-003, ADR-024, ADR-027, ADR-028
- ログ / マスキング: ADR-007, ADR-008
- nullability: ADR-009, ADR-010, ADR-011
- JavaDoc: ADR-012

### 20.2 今後の追記候補

- 代表コード例
- アンチパターン集
- レビュー観点チェックリスト
