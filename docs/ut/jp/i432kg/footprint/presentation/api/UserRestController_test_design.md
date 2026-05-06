# `UserRestController` テスト仕様書

## 1. 基本情報

- 対象クラス: `UserRestController`
- 対象パッケージ: `jp.i432kg.footprint.presentation.api`
- 対応するテストクラス: `UserRestControllerTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をするクラスか: 現在ユーザーのプロフィール取得、自分の投稿/返信一覧取得、サインアップを提供する controller
- 主な依存: `PostQueryService`, `ReplyQueryService`, `UserQueryService`, `UserCommandService`, `Clock`, 各 response mapper

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 現在ユーザープロフィール取得 | `UserQueryService.getUserProfile(...)` と mapper を使って 200 を返すこと |
| 2 | 正常系 | 自分の投稿一覧取得 | `lastId` を `PostId` へ変換して `listMyPosts(...)` を呼ぶこと |
| 3 | 正常系 | 自分の返信一覧取得 | `lastId` を `ReplyId` へ変換して `listMyReplies(...)` を呼ぶこと |
| 4 | 正常系 | サインアップ | `SignUpRequest` を各値オブジェクトへ変換し `CreateUserCommand` を生成すること |
| 5 | 正常系 | 生年月日基準日 | `BirthDate.of(..., LocalDate.now(clock))` の基準日として固定 `Clock` を使うこと |
| 6 | 異常系 | 固定 Clock に対する未来日 | fixed `Clock` 基準で未来日となる `birthDate` を拒否すること |
| 7 | 正常系 | login 呼び出し | ユーザー作成後に `request.login(email, rawPassword)` を呼ぶこと |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | 現在ユーザープロフィールを取得する | 認証済みユーザーあり | status=200、mapper 結果を body に返す |
| 2 | 正常系 | 自分の投稿一覧を取得する | `lastId` あり | `PostId` 変換後に service を呼ぶ |
| 3 | 正常系 | 自分の返信一覧を取得する | `lastId` あり | `ReplyId` 変換後に service を呼ぶ |
| 4 | 正常系 | サインアップを受け付ける | 妥当な `SignUpRequest` | status=201、`CreateUserCommand` を生成して service を呼ぶ |
| 5 | 正常系 | 固定 Clock を使って生年月日を生成する | `clock` を fixed にする | command の `BirthDate` がその基準日で評価される |
| 6 | 異常系 | fixed `Clock` 基準で未来日の生年月日を拒否する | `birthDate=2026-04-20`, `clock=2026-04-19` | `InvalidValueException` が送出される |
| 7 | 正常系 | 作成後にログインする | `request.login(...)` が成功する | `userCommandService.createUser(...)` 後に login が呼ばれる |

## 5. 実装メモ

- `HttpServletRequest` はモックして `login(...)` 呼び出しを検証する
- `CreateUserCommand` は `ArgumentCaptor` で受けて、中の `BirthDate` / `EmailAddress` / `RawPassword` を確認する
- fixed `Clock` を使って `BirthDate.of(..., today)` の基準日を明確にする

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_returnCurrentUserProfile_when_getCurrentUserIsCalled` | `UserRestController は現在ユーザープロフィール取得時に mapper 結果を 200 で返す` |
| 2 | `should_returnMyPosts_when_getMyPostsIsCalled` | `UserRestController は自分の投稿一覧取得時に 200 を返す` |
| 3 | `should_returnMyReplies_when_getMyRepliesIsCalled` | `UserRestController は自分の返信一覧取得時に 200 を返す` |
| 4 | `should_createUser_when_createIsCalled` | `UserRestController はサインアップ情報から command を生成して 201 を返す` |
| 5 | `should_useFixedClockDate_when_creatingBirthDate` | `UserRestController は固定 Clock の当日を基準に BirthDate を生成する` |
| 6 | `should_throwException_when_birthDateIsFutureAgainstFixedClock` | `UserRestController は固定 Clock 基準で未来日の birthDate を拒否する` |
| 7 | `should_loginAfterUserCreation_when_createSucceeds` | `UserRestController はユーザー作成後に request.login を呼ぶ` |
