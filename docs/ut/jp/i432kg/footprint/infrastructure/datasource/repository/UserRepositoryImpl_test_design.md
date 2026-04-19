# `UserRepositoryImpl` テスト仕様書

## 1. 基本情報

- 対象クラス: `UserRepositoryImpl`
- 対象メソッド: `existsById(UserId)`, `existsByEmail(EmailAddress)`, `saveUser(User)`
- 対象パッケージ: `jp.i432kg.footprint.infrastructure.datasource.repository`
- 対応するテストクラス: `UserRepositoryImplTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をする処理か: ユーザー存在確認・メール重複確認・ユーザー保存を `UserMapper` に委譲し、保存時は `Clock` を用いて mapper 用 entity を生成する
- 入力: `UserId`, `EmailAddress`, `User`
- 出力: `boolean`, `void`
- 主な副作用: `UserMapper` の `countByUserId`, `countByEmail`, `insert` 呼び出し

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | ユーザー存在確認 | `existsById` が `countByUserId` の結果を `> 0` 判定で返すこと |
| 2 | 正常系 | メール存在確認 | `existsByEmail` が `countByEmail` の結果を `> 0` 判定で返すこと |
| 3 | 正常系 | ユーザー保存 | `saveUser` が `Clock` に基づく `UserInsertEntity` を生成して `insert` に渡すこと |
| 4 | 異常系 | 例外再送出 | 各 mapper 呼び出しの `RuntimeException` を再送出すること |
| 5 | 境界値 | カウント 0 | `count=0` の場合に `false` を返すこと |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | ユーザーが存在する場合に `true` を返す | `countByUserId(userId)=1` | `existsById(userId)` が `true` |  |
| 2 | 境界値 | ユーザーが存在しない場合に `false` を返す | `countByUserId(userId)=0` | `existsById(userId)` が `false` |  |
| 3 | 正常系 | メールアドレスが登録済みの場合に `true` を返す | `countByEmail(email)=1` | `existsByEmail(email)` が `true` |  |
| 4 | 境界値 | メールアドレスが未登録の場合に `false` を返す | `countByEmail(email)=0` | `existsByEmail(email)` が `false` |  |
| 5 | 正常系 | ユーザーを保存する | 固定 `Clock`、`User` を入力 | `insert(UserInsertEntity)` が呼ばれ、`createdAt` / `updatedAt` が固定時刻に一致する | `isActive=false`, `isDisabled=false`, `disabledAt=null` も確認 |
| 6 | 異常系 | ユーザー存在確認失敗を再送出する | `countByUserId(userId)` が `RuntimeException` | 同じ例外を送出する |  |
| 7 | 異常系 | メール存在確認失敗を再送出する | `countByEmail(email)` が `RuntimeException` | 同じ例外を送出する |  |
| 8 | 異常系 | ユーザー保存失敗を再送出する | `insert(...)` が `RuntimeException` | 同じ例外を送出する |  |

## 5. 実装メモ

- モック化する依存: `UserMapper`, `Clock`
- 固定化が必要な値: `User`, `UserId`, `EmailAddress`, `Instant`, `ZoneId`
- `@DisplayName` 方針: `UserRepositoryImpl` の存在確認 / 保存 / 失敗を日本語で明記する
- 備考:
  - `Clock.fixed(...)` を使用し、`UserInsertEntity.from(user, clock)` と同等内容を captor で確認する
  - `userMapper.insert` の引数は `ArgumentCaptor` で取得して各項目を検証する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_returnTrue_when_userExists` | `UserRepositoryImpl.existsById はユーザーが存在する場合に true を返す` |
| 2 | `should_returnFalse_when_userDoesNotExist` | `UserRepositoryImpl.existsById はユーザーが存在しない場合に false を返す` |
| 3 | `should_returnTrue_when_emailExists` | `UserRepositoryImpl.existsByEmail はメールアドレスが登録済みの場合に true を返す` |
| 4 | `should_returnFalse_when_emailDoesNotExist` | `UserRepositoryImpl.existsByEmail はメールアドレスが未登録の場合に false を返す` |
| 5 | `should_saveUserWithClockTime_when_saveUserCalled` | `UserRepositoryImpl.saveUser は固定 Clock に基づく時刻でユーザーを保存する` |
| 6 | `should_rethrowException_when_existsByIdFails` | `UserRepositoryImpl.existsById は mapper 例外を再送出する` |
| 7 | `should_rethrowException_when_existsByEmailFails` | `UserRepositoryImpl.existsByEmail は mapper 例外を再送出する` |
| 8 | `should_rethrowException_when_saveUserFails` | `UserRepositoryImpl.saveUser は mapper 例外を再送出する` |
