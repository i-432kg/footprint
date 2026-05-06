# `UserDomainService` テスト仕様書

## 1. 基本情報

- 対象クラス: `UserDomainService`
- 対象メソッド: `isExistUser(UserId)`, `ensureEmailNotAlreadyUsed(EmailAddress)`
- 対象パッケージ: `jp.i432kg.footprint.domain.service`
- 対応するテストクラス: `UserDomainServiceTest`
- 作成者: Codex
- 作成日: 2026-04-16

## 2. 対象概要

- 何をする処理か: ユーザー存在確認と、メールアドレス重複利用の防止を行う
- 入力: `UserId`, `EmailAddress`
- 出力: `boolean`, 例外送出の有無
- 主な副作用: `UserRepository.existsById(...)`, `UserRepository.existsByEmail(...)` 呼び出し

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | `isExistUser` 委譲 | repository の boolean 戻り値をそのまま返すこと |
| 2 | 正常系 | メール未使用 | `existsByEmail(...)` が `false` の場合に例外を送出しないこと |
| 3 | 異常系 | メール使用済み | `existsByEmail(...)` が `true` の場合に `EmailAlreadyUsedException` を送出すること |
| 4 | 正常系 | 委譲 | 入力した `UserId` / `EmailAddress` をそのまま repository に渡すこと |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | repository が存在ありを返す | `UserId`, `existsById(...) -> true` | `true` を返す |  |
| 2 | 正常系 | repository が存在なしを返す | `UserId`, `existsById(...) -> false` | `false` を返す |  |
| 3 | 正常系 | メールアドレスが未使用の場合 | `EmailAddress`, `existsByEmail(...) -> false` | 例外なし |  |
| 4 | 異常系 | メールアドレスが使用済みの場合 | `EmailAddress`, `existsByEmail(...) -> true` | `EmailAlreadyUsedException` |  |
| 5 | 正常系 | 入力した `UserId` / `EmailAddress` を repository に渡す | `UserId`, `EmailAddress` | 同じ引数で repository が呼ばれる | verify 観点 |

## 5. 実装メモ

- モック化する依存: `UserRepository`
- 固定化が必要な値: 妥当な `UserId`, `EmailAddress`
- `@DisplayName` 方針: `UserDomainService の存在確認と重複防止を分けて記載する`
- 備考: `ensureEmailNotAlreadyUsed(...)` は `existsByEmail(...)` の戻り値に応じて例外送出有無が切り替わるため、両分岐を確認する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_returnTrue_when_userExists` | `UserDomainService.isExistUser は repository が true を返す場合に true を返す` |
| 2 | `should_returnFalse_when_userDoesNotExist` | `UserDomainService.isExistUser は repository が false を返す場合に false を返す` |
| 3 | `should_notThrowException_when_emailIsNotUsed` | `UserDomainService.ensureEmailNotAlreadyUsed はメールアドレスが未使用の場合に例外を送出しない` |
| 4 | `should_throwEmailAlreadyUsedException_when_emailIsAlreadyUsed` | `UserDomainService.ensureEmailNotAlreadyUsed はメールアドレスが使用済みの場合に重複利用例外を送出する` |
| 5 | `should_delegateToRepositoryWithGivenArguments_when_checkingUserOrEmail` | `UserDomainService は入力した UserId と EmailAddress を repository に渡す` |
