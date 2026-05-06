# `DomainException` テスト仕様書

## 1. 基本情報

- 対象クラス: `DomainException`
- 対象メソッド: コンストラクタ
- 対象パッケージ: `jp.i432kg.footprint.domain.exception`
- 対応するテストクラス: `DomainExceptionTest`
- 作成者: Codex
- 作成日: 2026-04-16

## 2. 対象概要

- 何をする処理か: ドメイン例外の基底クラスとして `errorCode`, `message`, `details`, `cause` を保持する
- 入力: `ErrorCode`, `String`, `Map<String, Object>`, `Throwable`
- 出力: `DomainException`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 基本保持 | コンストラクタで渡した `message`, `errorCode`, `details` を保持すること |
| 2 | 正常系 | cause 保持 | cause 付きコンストラクタで渡した `cause` を保持すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 基本情報を保持する | `ErrorCode.DOMAIN_INVALID_VALUE`, `"test message"`, `{"key":"value"}` | 各 getter で取得できる | テスト用サブクラス使用 |
| 2 | 正常系 | cause を保持する | 上記 + `RuntimeException("boom")` | `getCause()` が同一インスタンス | テスト用サブクラス使用 |

## 5. 実装メモ

- モック化する依存: なし
- 固定化が必要な値: `ErrorCode`, `details`, `cause`
- `@DisplayName` 方針: `DomainException の保持責務を記載する`
- 備考:
  - `DomainException` は abstract のため、テストでは最小の具象サブクラスを使う

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_setMessageErrorCodeAndDetails_when_constructed` | `DomainException は message と errorCode と details を保持する` |
| 2 | `should_setCause_when_constructedWithCause` | `DomainException は cause 付きコンストラクタで cause を保持する` |
