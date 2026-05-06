# `SensitiveDataMasker` テスト仕様書

## 1. 基本情報

- 対象クラス: `SensitiveDataMasker`
- 対象パッケージ: `jp.i432kg.footprint.logging.masking`
- 対応するテストクラス: `SensitiveDataMaskerTest`
- 作成者: Codex
- 作成日: 2026-04-26

## 2. 対象概要

- 何をするクラスか: ログと API エラーレスポンスの `details` を再帰的に走査し、機微情報をマスクするコンポーネント
- 主な責務: `Map` / `List` 再帰走査、`rejectedValue` の `target` 依存判定、型ベース判定、戻り値の不変化

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | `maskRejectedValue` キー判定 | `objectKey`, `fileName`, `password`, `token`, `email` 系の target に応じて値をマスクすること |
| 2 | 正常系 | `maskRejectedValue` null | `rejectedValue=null` の場合は null を返すこと |
| 3 | 正常系 | 型ベース判定 | `RawPassword`, `HashedPassword`, `EmailAddress` をキー名によらずマスクすること |
| 4 | 正常系 | `maskMap` 単層 | 単純な `details` で `rejectedValue` と直接キーの両方がマスクされること |
| 5 | 正常系 | `maskMap` ネスト | ネストした `Map` や `List<Map<...>>` でも再帰的にマスクされること |
| 6 | 正常系 | 非対象値 | 機微情報に該当しないキーや値は変更されないこと |
| 7 | 正常系 | 不変性 | 戻り値が不変 `Map` であること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | objectKey 系 `rejectedValue` をマスクする | `field=objectKey`, `rejectedValue=users/u/posts/p/images/i.jpg` | `********` を返す |
| 2 | 正常系 | fileName 系 `rejectedValue` をマスクする | `field=fileName`, `rejectedValue=my-private-photo.jpg` | `********` を返す |
| 3 | 正常系 | `rejectedValue` が null の場合は null を返す | `field=password`, `rejectedValue=null` | null を返す |
| 4 | 正常系 | `EmailAddress` を型ベースでマスクする | `field=userName`, `rejectedValue=EmailAddress.of(...)` | 先頭 1 文字以外がマスクされた文字列を返す |
| 5 | 正常系 | `RawPassword` を型ベースでマスクする | `field=userName`, `rejectedValue=RawPassword.of(...)` | `********` を返す |
| 6 | 正常系 | `details.target` に応じて `rejectedValue` をマスクする | `target=image.objectKey`, `rejectedValue=users/...`, `storageObjectKey=users/...` | `rejectedValue`, `storageObjectKey` の両方が `********` |
| 7 | 正常系 | ネストした `Map` を再帰的にマスクする | `details.errors[0].target=email`, `details.errors[0].rejectedValue=alice@example.com` | ネスト先の `rejectedValue` がマスクされる |
| 8 | 正常系 | `List` 内の単純値はキー判定でマスクしない | `key=errors`, `value=List.of("plain-text")` | 値がそのまま残る |
| 9 | 正常系 | 機微情報でない値は変更しない | `field=caption`, `rejectedValue=hello` | `hello` を返す |
| 10 | 正常系 | 戻り値は不変 `Map` である | `maskMap(...)` 実行後に put を試みる | `UnsupportedOperationException` |

## 5. 実装メモ

- `maskMap` は内部で `Map.copyOf(...)` を返すため、不変性確認では例外送出を使う
- ネスト `Map` は `Map<?, ?>` を `String` キーへ変換し直してから再帰呼び出しする実装であるため、`errors[0]` のような構造を用意して確認する
- `rejectedValue` の判定は `context.target` に依存するため、`target` を同階層に必ず含める
- 現時点の実装済みテストは No.1, No.2, No.6 に対応する 3 件

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_maskObjectKeyLikeValue_when_targetIndicatesObjectKey` | `SensitiveDataMasker は objectKey 系の値をマスクする` |
| 2 | `should_maskFileNameLikeValue_when_targetIndicatesFileName` | `SensitiveDataMasker は fileName 系の値をマスクする` |
| 3 | `should_returnNull_when_rejectedValueIsNull` | `SensitiveDataMasker は rejectedValue が null のとき null を返す` |
| 4 | `should_maskEmailAddressByType_when_valueIsEmailAddress` | `SensitiveDataMasker は EmailAddress を型ベースでマスクする` |
| 5 | `should_maskRawPasswordByType_when_valueIsRawPassword` | `SensitiveDataMasker は RawPassword を型ベースでマスクする` |
| 6 | `should_maskNestedStorageObjectKey_when_maskingMap` | `SensitiveDataMasker は details 内の storageObjectKey を再帰的にマスクする` |
| 7 | `should_maskNestedRejectedValue_when_nestedMapContainsTargetAndRejectedValue` | `SensitiveDataMasker はネストした Map 内の rejectedValue を再帰的にマスクする` |
| 8 | `should_keepPlainListItem_when_listContainsNonSensitiveScalar` | `SensitiveDataMasker は List 内の非機微単純値を変更しない` |
| 9 | `should_keepOriginalValue_when_targetIsNotSensitive` | `SensitiveDataMasker は機微情報でない値を変更しない` |
| 10 | `should_returnUnmodifiableMap_when_maskMapIsCalled` | `SensitiveDataMasker は不変 Map を返す` |
