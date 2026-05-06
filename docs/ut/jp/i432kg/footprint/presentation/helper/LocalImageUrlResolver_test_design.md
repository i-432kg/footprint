# `LocalImageUrlResolver` テスト仕様書

## 1. 基本情報

- 対象クラス: `LocalImageUrlResolver`
- 対象パッケージ: `jp.i432kg.footprint.presentation.helper`
- 対応するテストクラス: `LocalImageUrlResolverTest`
- 作成者: Codex
- 作成日: 2026-04-19

## 2. 対象概要

- 何をするクラスか: LOCAL 保存画像の公開 URL を解決する resolver
- 主な依存/設定: `imageBaseUrl`

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | URL 連結 | `imageBaseUrl + objectKey` の形式で URL を返すこと |
| 2 | 異常系 | 保存種別違反 | LOCAL 以外の `StorageObject` を渡した場合に `IllegalArgumentException` を送出すること |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | LOCAL 画像 URL を解決する | `imageBaseUrl=/images/`, `storageObject=LOCAL` | `/images/{objectKey}` を返す |
| 2 | 異常系 | S3 保存オブジェクトを拒否する | `storageObject=S3` | `IllegalArgumentException` |

## 5. 実装メモ

- `imageBaseUrl` は reflection か setter 相当でテストから設定する
- `StorageObject.local(...)` / `StorageObject.s3(...)` を使って保存種別を作り分ける

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_resolveLocalImageUrl_when_storageObjectIsLocal` | `LocalImageUrlResolver は LOCAL 保存画像の公開 URL を解決する` |
| 2 | `should_throwException_when_storageObjectIsNotLocal` | `LocalImageUrlResolver は LOCAL 以外の保存種別を拒否する` |
