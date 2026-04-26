# `MaskingTarget` テスト仕様書

## 1. 基本情報

- 対象クラス: `MaskingTarget`
- 対象パッケージ: `jp.i432kg.footprint.logging.masking`
- 対応するテストクラス: `MaskingTargetTest`（新規作成予定）
- 作成者: Codex
- 作成日: 2026-04-26

## 2. 対象概要

- 何をするクラスか: キー名やフィールド名からマスキング対象種別を判定する enum
- 主な責務: キーワード部分一致による対象判定、戦略への委譲

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | `matches` 大小文字差 | `toLowerCase()` により大文字混在キーでも判定できること |
| 2 | 正常系 | `matches` 部分一致 | `password`, `token`, `email`, `objectKey`, `fileName`, `storageObjectKey` を含むキーで該当ターゲットが true になること |
| 3 | 正常系 | `matches` 非該当 | 対象外キーでは全ターゲットが false になること |
| 4 | 正常系 | `mask` 委譲 | 対象ターゲットに紐づく `MaskingStrategy` で値を変換すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | `PASSWORD` は password 系キーに一致する | `rawPassword`, `client_secret` | `PASSWORD.matches(...)` が true |
| 2 | 正常系 | `TOKEN` は token 系キーに一致する | `refreshToken`, `apiCredential` | `TOKEN.matches(...)` が true |
| 3 | 正常系 | `EMAIL` は email 系キーに一致する | `email`, `contactEmailAddress` | `EMAIL.matches(...)` が true |
| 4 | 正常系 | `FILE` は objectKey / fileName 系キーに一致する | `objectKey`, `image.fileName`, `storageObjectKey` | `FILE.matches(...)` が true |
| 5 | 正常系 | 一致判定は大小文字差を吸収する | `FileName`, `ObjectKey` | 該当ターゲットが true |
| 6 | 正常系 | 対象外キーには一致しない | `userName` | 全ターゲットで false |
| 7 | 正常系 | `mask` は戦略へ委譲する | `EMAIL.mask("alice@example.com")` | `a********@example.com` を返す |

## 5. 実装メモ

- `matches` は部分一致実装のため、完全一致だけでなく接頭辞・接尾辞を含む文字列を用意する
- `FILE` のキーワードは `toLowerCase()` 判定により `fileName` でも `filename` として扱われる点を確認する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_matchPasswordLikeKey_when_passwordTargetReceivesPasswordKeyword` | `MaskingTarget.PASSWORD は password 系キーに一致する` |
| 2 | `should_matchTokenLikeKey_when_tokenTargetReceivesTokenKeyword` | `MaskingTarget.TOKEN は token 系キーに一致する` |
| 3 | `should_matchEmailLikeKey_when_emailTargetReceivesEmailKeyword` | `MaskingTarget.EMAIL は email 系キーに一致する` |
| 4 | `should_matchFileLikeKey_when_fileTargetReceivesObjectKeyOrFileNameKeyword` | `MaskingTarget.FILE は objectKey と fileName 系キーに一致する` |
| 5 | `should_matchIgnoringCase_when_targetReceivesUpperCaseKey` | `MaskingTarget.matches はキーの大小文字差を吸収する` |
| 6 | `should_notMatch_when_keyDoesNotContainAnyKeyword` | `MaskingTarget は対象外キーに一致しない` |
| 7 | `should_delegateMaskingToStrategy_when_maskIsInvoked` | `MaskingTarget.mask は対応する MaskingStrategy へ委譲する` |
