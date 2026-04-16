# `ObjectKey` テスト仕様書

## 1. 基本情報

- 対象クラス: `ObjectKey`
- 対象メソッド: `of(String)`
- 対象パッケージ: `jp.i432kg.footprint.domain.value`
- 対応するテストクラス: `ObjectKeyTest`
- 作成者: Codex
- 作成日: 2026-04-15

## 2. 対象概要

- 何をする処理か: ストレージ上のオブジェクトキーを安全なパス表現として検証・正規化する
- 入力: `String`
- 出力: `ObjectKey`
- 主な副作用: なし

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | 正規化 | 前後空白を trim して保持すること |
| 2 | 正常系 | 妥当なキー | 英数字と `._/-` を用いた安全なキーを受け入れること |
| 3 | 異常系 | 必須 | `null`、blank を拒否すること |
| 4 | 境界値 | 長さ | 1024文字以下を受け入れ、1025文字超を拒否すること |
| 5 | 異常系 | パス安全性 | 絶対パス、Windows パス、二重スラッシュ、`.` / `..`、末尾スラッシュ、空セグメントを拒否すること |
| 6 | 異常系 | 許可文字 | 空白など許可外文字を拒否すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 | 備考 |
|---|---|---|---|---|---|
| 1 | 正常系 | trim して保持する | `" users/123/posts/456/image.jpg "` | `users/123/posts/456/image.jpg` を保持 |  |
| 2 | 正常系 | 妥当なキーを受け入れる | `users/123/images/abc_01.jpg` | 生成成功 |  |
| 3 | 異常系 | `null` を拒否する | `null` | `required("object_key")` |  |
| 4 | 異常系 | blank を拒否する | `" "` | `blank("object_key")` |  |
| 5 | 境界値 | 最大長ちょうどを受け入れる | 1024文字 | 生成成功 |  |
| 6 | 異常系 | 最大長超過を拒否する | 1025文字 | `tooLong(...)` |  |
| 7 | 異常系 | 先頭スラッシュを拒否する | `/abc` | `invalid(...)` |  |
| 8 | 異常系 | バックスラッシュを拒否する | `abc\\def` | `invalid(...)` |  |
| 9 | 異常系 | 二重スラッシュを拒否する | `abc//def` | `invalid(...)` |  |
| 10 | 異常系 | `.` または `..` セグメントを拒否する | `a/./b`, `a/../b`, `./a`, `../a` | `invalid(...)` |  |
| 11 | 異常系 | 末尾スラッシュを拒否する | `a/b/` | `invalid(...)` |  |
| 12 | 異常系 | 許可外文字を拒否する | `a b/c.jpg` | `invalidFormat(...)` |  |

## 5. 実装メモ

- `@DisplayName` 方針: `ObjectKey.of の安全性観点を優先して記載する`

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_trimObjectKey_when_valueHasLeadingOrTrailingSpaces` | `ObjectKey.of は前後空白を除去して保持する` |
| 2 | `should_createObjectKey_when_valueIsSafePath` | `ObjectKey.of は安全なパス形式の値を受け入れる` |
| 3 | `should_throwException_when_objectKeyIsNullOrBlank` | `ObjectKey.of は null または空白のみを拒否する` |
| 4 | `should_throwException_when_objectKeyExceedsMaxLength` | `ObjectKey.of は最大長を超える値を拒否する` |
| 5 | `should_throwException_when_objectKeyStartsWithSlash` | `ObjectKey.of は絶対パス表現を拒否する` |
| 6 | `should_throwException_when_objectKeyContainsBackslash` | `ObjectKey.of は Windows パスを含む値を拒否する` |
| 7 | `should_throwException_when_objectKeyContainsInvalidSegments` | `ObjectKey.of は . や .. を含むセグメントを拒否する` |
| 8 | `should_throwException_when_objectKeyContainsUnsupportedCharacters` | `ObjectKey.of は許可外文字を含む値を拒否する` |
