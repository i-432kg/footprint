# `S3ImageUrlResolver` テスト仕様書

## 1. 基本情報

- 対象クラス: `S3ImageUrlResolver`
- 対象パッケージ: `jp.i432kg.footprint.presentation.helper`
- 対応するテストクラス: `S3ImageUrlResolverTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をするクラスか: S3 保存画像の presigned URL を解決する resolver
- 主な依存: `S3ObjectResolver`, `S3StorageProperties`, `S3Presigner`

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | bucket/key 解決 | `S3ObjectResolver` から取得した bucket/key を `GetObjectRequest` に反映すること |
| 2 | 正常系 | 有効期限設定 | `S3StorageProperties.getPresignedGetExpireMinutes()` を `GetObjectPresignRequest` に反映すること |
| 3 | 正常系 | URL 返却 | `S3Presigner.presignGetObject(...)` が返した URL 文字列をそのまま返すこと |
| 4 | 異常系 | 保存種別違反 | S3 以外の `StorageObject` を渡した場合に `IllegalArgumentException` を送出すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | S3 画像 URL を解決する | bucket/key/expireMinutes/presigned URL をモック | presigned URL 文字列を返す |
| 2 | 正常系 | presign request に bucket/key/expire を設定する | resolver/properties の戻り値を固定 | `GetObjectPresignRequest` の中身が期待どおり |
| 3 | 異常系 | LOCAL 保存オブジェクトを拒否する | `storageObject=LOCAL` | `IllegalArgumentException` |

## 5. 実装メモ

- `GetObjectPresignRequest` は `ArgumentCaptor` で受けて `signatureDuration` と `getObjectRequest` を確認する
- `S3Presigner.presignGetObject(...)` の戻りは mock で URL 文字列を固定する

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_resolvePresignedUrl_when_storageObjectIsS3` | `S3ImageUrlResolver は S3 保存画像の presigned URL を解決する` |
| 2 | `should_buildPresignRequestWithResolvedBucketKeyAndExpireMinutes_when_resolvingUrl` | `S3ImageUrlResolver は解決した bucket key と有効期限で presign request を組み立てる` |
| 3 | `should_throwException_when_storageObjectIsNotS3` | `S3ImageUrlResolver は S3 以外の保存種別を拒否する` |
