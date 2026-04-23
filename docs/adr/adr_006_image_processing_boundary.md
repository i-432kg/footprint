# ADR: 画像保存とメタデータ抽出は domain ではなく application / infrastructure で扱う

## ステータス
Proposed

## 背景
現在の [`ImageRepository`](../../src/main/java/jp/i432kg/footprint/domain/repository/ImageRepository.java) は、domain の repository port でありながら次の技術要素を直接扱っている。

- `InputStream`
- `IOException`
- `ImageProcessingException`
- 画像ファイルの保存
- EXIF / 画像サイズなどのメタデータ抽出

これは domain の永続化抽象というより、I/O と外部ライブラリを伴う画像処理アダプタに近い。

また、[`PostCommandService`](../../src/main/java/jp/i432kg/footprint/application/command/PostCommandService.java) では、画像保存とメタデータ抽出をユースケース手順として扱っており、責務の実態も application / infrastructure 寄りである。

## 問題
- domain が `InputStream` や `IOException` などの技術詳細を知っている
- domain port が S3 / ローカルストレージ / 画像解析ライブラリに引っ張られている
- `ImageRepository` が「保存」と「解析」を同時に持ち、抽象度が低い
- CQRS の command use case に属する技術処理が domain に漏れている

## 決定
画像保存とメタデータ抽出は domain の repository port ではなく、application / infrastructure の責務として扱う。

段階的には以下の構成へ移行する。

1. 画像保存用ポートを application 側に置く
2. 画像メタデータ抽出用ポートを application 側に置く
3. domain は `Image` モデルとその生成ルールだけを持つ
4. infrastructure はストレージアクセスと画像解析の実装を持つ

## 設計方針

### 1. 保存と解析を分離する
1つのポートに以下を混在させない。

- 画像の保存
- 保存済み画像の解析

想定する分割例:

- `ImageStorage`
- `ImageMetadataExtractor`

## 2. 画像解析結果は application モデルで受ける
画像解析結果は domain model そのものではなく、application のデータキャリアで受ける。

その受け皿として [`ImageMetadata`](../../src/main/java/jp/i432kg/footprint/application/command/model/ImageMetadata.java) を用意する。

`ImageMetadata` が持つもの:

- `FileExtension`
- `Byte`
- `Pixel width`
- `Pixel height`
- `Location`
- `boolean hasEXIF`
- `LocalDateTime takenAt`

保存先そのものは `StorageObject` として別に扱い、最終的に application で `Image.of(...)` へ合成する。

### 3. domain は業務ルールだけに寄せる
domain の役割は次に限定する。

- `Image` の不変条件
- `StorageObject`, `FileExtension`, `Pixel` などの値オブジェクト
- 画像に関する業務ルール

一方で次は domain に置かない。

- ファイル読み書き
- S3 / ローカルファイルシステム分岐
- 画像解析ライブラリ依存
- `IOException` や SDK 例外の露出

### 4. 例外変換は application で行う
I/O や画像解析失敗は infrastructure 実装内または application で受け、ユースケース例外へ変換する。

domain はそれらの技術例外を直接知らない。

## 選択肢

### 案1: `ImageRepository` を domain に残し、例外だけ独自例外へ変える
不採用。

理由:
- `InputStream` や保存責務が残り、依然として技術詳細が domain に漏れる
- 問題の本質が「例外型」ではなく「責務と依存の位置」にあるため

### 案2: `ImageRepository` を application port へ移す
次善案として採用可能。

理由:
- 既存実装を活かしやすい
- まず `domain` から出すという目的は達成できる

ただし、保存と解析が同じポートに残るため、最終形としては分割した方がよい。

### 案3: 保存ポートと解析ポートを分ける
最終的な推奨案。

理由:
- 責務が明確
- 実装差分の影響範囲が小さい
- S3 / Local の差し替えやテストがしやすい

## 移行手順
1. `ImageMetadata` を追加する
2. application 側に画像保存・画像解析のポートを追加する
3. Local / S3 実装を新ポートへ移す
4. `PostCommandService` を新ポート利用へ変更する
5. 最後に `domain.repository.ImageRepository` を削除する

## 期待する効果
- domain から I/O と外部ライブラリ依存を排除できる
- Onion Architecture の依存方向が明確になる
- command use case の技術処理を application / infrastructure に集約できる
- 画像保存処理と画像解析処理を個別にテストしやすくなる
