# `jp.i432kg.footprint.domain.exception` パッケージ UT仕様書サマリー

## 1. 対象概要

- 対象パッケージ: `jp.i432kg.footprint.domain.exception`
- 対象クラス: `DomainException`, `EmailAlreadyUsedException`, `InvalidModelException`, `InvalidValueException`, `ReplyPostMismatchException`
- 個別仕様書: 各クラスごとに `ファイル名_test_design.md` を作成

## 2. 横断観点

| No. | 観点 | 内容 |
|---|---|---|
| 1 | エラーコード | 各例外が意図した `ErrorCode` を保持すること |
| 2 | メッセージ | API レスポンスやログで使う `message` が期待どおりに組み立てられること |
| 3 | details | `target`, `reason`, `rejectedValue` や業務識別子など、呼び出し側で参照する `details` が正しく格納されること |
| 4 | 基底クラス | `DomainException` が `DetailBasedException` として `errorCode`, `message`, `details`, `cause` を保持できること |
| 5 | 例外種別 | 値オブジェクト違反は `InvalidValueException`、モデル不変条件違反は `InvalidModelException` になること |

## 3. グルーピング方針

- 基底例外系: `DomainException`
  - コンストラクタ経由で保持する基本情報と `cause` の伝搬を確認する
- 汎用バリデーション例外系: `InvalidValueException`, `InvalidModelException`
  - static factory ごとの `message` と `details` の組み立てを確認する
- 業務例外系: `EmailAlreadyUsedException`, `ReplyPostMismatchException`
  - 業務ルール違反時の固定メッセージとドメイン値の埋め込みを確認する

## 4. テスト実装メモ

- モック化する依存: なし
- `DomainException` はテスト用の具象サブクラスを作って protected コンストラクタを確認する
- `InvalidValueException` は `required`, `blank`, `tooLong`, `tooShort`, `outOfRange`, `invalidFormat`, `invalid` を個別に確認する
- `ReplyPostMismatchException` は `details(...)` により `target=reply`, `reason=post_mismatch` が入る点を確認する
- 各テストメソッドには `@DisplayName` を付与し、日本語の見出しで観点を明示する
