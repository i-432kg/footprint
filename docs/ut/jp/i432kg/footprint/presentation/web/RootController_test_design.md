# `RootController` テスト仕様書

## 1. 基本情報

- 対象クラス: `RootController`
- 対象パッケージ: `jp.i432kg.footprint.presentation.web`
- 対応するテストクラス: `RootControllerTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をするクラスか: 画面遷移用の Web controller
- 主なメソッド: `addLoginUserToModel(...)`, `index()`, `login()`, `map()`, `mypage()`, `search()`, `timeline()`

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 認証済みユーザー | 認証ユーザーがいる場合に `displayUsername` を model に追加すること |
| 2 | 正常系 | 未認証 | 認証ユーザーが `null` の場合に model を変更しないこと |
| 3 | 正常系 | テンプレート名 | 各 GET ハンドラが期待どおりのテンプレート名を返すこと |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | 認証ユーザーを model に追加する | `userDetails.displayUsername=alice` | `displayUsername=alice` が入る |
| 2 | 正常系 | 未認証では model を変更しない | `userDetails=null` | `displayUsername` が追加されない |
| 3 | 正常系 | 各画面のテンプレート名を返す | なし | `index/timeline`, `login`, `map`, `mypage`, `search` を返す |

## 5. 実装メモ

- `Model` は `ExtendedModelMap` で十分
- テンプレート名は 1 テストにまとめてもよいが、可読性を優先して必要なら分ける

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_addDisplayUsernameToModel_when_userIsAuthenticated` | `RootController は認証済みユーザーの displayUsername を model に追加する` |
| 2 | `should_notModifyModel_when_userIsNotAuthenticated` | `RootController は未認証時に model を変更しない` |
| 3 | `should_returnExpectedTemplateNames_when_pageEndpointsAreCalled` | `RootController は各画面の期待テンプレート名を返す` |
