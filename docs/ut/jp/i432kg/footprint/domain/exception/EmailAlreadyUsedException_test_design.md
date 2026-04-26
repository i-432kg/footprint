# `EmailAlreadyUsedException` テスト仕様書

## 1. 基本情報

- 対象クラス: `EmailAlreadyUsedException`
- 対象メソッド: コンストラクタ
- 対象パッケージ: `jp.i432kg.footprint.domain.exception`
- 対応するテストクラス: `EmailAlreadyUsedExceptionTest`
- 作成者: Codex
- 作成日: 2026-04-16

## 2. 対象概要

- 何をする処理か: 既に使用済みのメールアドレスでユーザー作成しようとした場合の業務例外を生成する
- 入力: `EmailAddress`
- 出力: `EmailAlreadyUsedException`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 固定メッセージ | 既定の message を保持すること |
| 2 | 正常系 | details | `target=email`, `reason=already_used`, `rejectedValue=emailAddress` を details に保持すること |
| 3 | 正常系 | エラーコード | `EMAIL_ALREADY_USED` を保持すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 例外情報を保持する | `EmailAddress.of("user@example.com")` | `EMAIL_ALREADY_USED`, 固定 message, `target=email`, `reason=already_used`, `rejectedValue=emailAddress` |  |

## 5. 実装メモ

- モック化する依存: なし
- 固定化が必要な値: `EmailAddress`
- `@DisplayName` 方針: `EmailAlreadyUsedException の保持内容を記載する`
- 備考:
  - `details` は `target` / `reason` / `rejectedValue` の正規形を持つ

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_setMessageErrorCodeAndDetails_when_constructed` | `EmailAlreadyUsedException は message と errorCode と details を保持する` |
