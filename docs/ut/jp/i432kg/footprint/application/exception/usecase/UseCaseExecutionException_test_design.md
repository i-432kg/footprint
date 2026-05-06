# `UseCaseExecutionException` テスト仕様書

## 1. 基本情報

- 対象クラス: `UseCaseExecutionException`
- 対象パッケージ: `jp.i432kg.footprint.application.exception.usecase`
- 対応するテストクラス: `UseCaseExecutionExceptionTest`
- 作成者: Codex
- 作成日: 2026-04-18

## 2. 対象概要

- 何をするクラスか: use case 実行失敗系例外の共通基底として `ApplicationException` を継承する

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 基本保持 | `errorCode`, `message`, `details` を保持すること |
| 2 | 正常系 | cause 保持 | cause 付き constructor で `cause` を保持すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | 実行失敗情報を保持する | test subclass を生成する | `errorCode`, `message`, `details` が一致する |
| 2 | 正常系 | cause を保持する | cause 付き test subclass を生成する | `cause` が一致する |

## 5. 実装メモ

- 抽象クラスのため、テスト内部に最小実装クラスを置く

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_holdBasicProperties_when_createdWithoutCause` | `UseCaseExecutionException は実行失敗情報を保持する` |
| 2 | `should_holdCause_when_createdWithCause` | `UseCaseExecutionException は cause を保持する` |
