# `LastLoginRecorder` テスト仕様書

## 1. 基本情報

- 対象クラス: `LastLoginRecorder`
- 対象メソッド: `recordSuccessfulLogin(UserId)`
- 対象パッケージ: `jp.i432kg.footprint.infrastructure.security`
- 対応するテストクラス: `LastLoginRecorderTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をする処理か: 認証成功時に `AuthMapper` を使って最終ログイン日時を更新する。更新失敗時は警告ログのみを残して処理を継続する
- 入力: `UserId`
- 出力: `void`
- 主な副作用: `AuthMapper.updateLastLoginAt(...)` 呼び出し、警告ログ出力

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 更新委譲 | 固定 `Clock` の現在時刻で `updateLastLoginAt` を呼ぶこと |
| 2 | 異常系 | 例外握りつぶし | `AuthMapper` が `RuntimeException` を送出しても再送出しないこと |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | 最終ログイン日時を更新する | 固定 `Clock`、`userId` | `updateLastLoginAt(userId, fixedNow)` が呼ばれる |  |
| 2 | 異常系 | 更新失敗時も例外を送出しない | `updateLastLoginAt(...)` が `RuntimeException` | 例外を送出せず処理終了する | ログ内容自体の厳密検証は不要 |

## 5. 実装メモ

- モック化する依存: `AuthMapper`
- 固定化が必要な値: `Clock.fixed(...)`, `UserId`
- `@DisplayName` 方針: 更新成功と更新失敗時継続を日本語で明記する
- 備考: 監査更新は非致命的であるという設計意図をテスト名に反映する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_updateLastLoginAt_when_recordSuccessfulLoginCalled` | `LastLoginRecorder.recordSuccessfulLogin は固定 Clock の時刻で最終ログイン日時を更新する` |
| 2 | `should_notThrow_when_updateLastLoginAtFails` | `LastLoginRecorder.recordSuccessfulLogin は更新失敗時も例外を送出しない` |
