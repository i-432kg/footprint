# `ResourceNotFoundException` テスト仕様書

## 1. 基本情報

- 対象クラス: `ResourceNotFoundException`
- 対象パッケージ: `jp.i432kg.footprint.application.exception.resource`
- 対応するテストクラス: `ResourceNotFoundExceptionTest`
- 作成者: Codex
- 作成日: 2026-04-18

## 2. 対象概要

- 何をするクラスか: resource not found 系例外の共通基底として `ApplicationException` を継承する

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 継承と保持 | `ApplicationException` の保持値を引き継ぐこと |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | not found 情報を保持する | test subclass を生成する | `errorCode`, `message`, `details` が一致する |

## 5. 実装メモ

- 抽象クラスのため、テスト内部に最小実装クラスを置く

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_holdBasicProperties_when_created` | `ResourceNotFoundException は not found 情報を保持する` |
