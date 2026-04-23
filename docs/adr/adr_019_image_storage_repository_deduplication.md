# ADR: 画像 storage 実装の重複は段階的に整理し、大規模リファクタは後回しにする

## ステータス

Accepted

## 背景

`LocalImageRepositoryImpl` と `S3ImageRepositoryImpl` は、保存先 I/O は異なる一方で、次の処理が大きく重複している。

- 画像保存時の `FileType` 判定
- 元ファイル名を含めた拡張子の決定
- `ImageId` / `ObjectKey` / `StorageObject` の生成
- EXIF / GPS / 画像サイズを用いた `ImageMetadata` の構築
- 画像サイズ（width / height）の抽出

一方で、保存先ごとの差分も明確に存在する。

- local: `Path` 解決、一時ファイル保存、`Files.move(...)`
- s3: bucket / key 解決、`putObject` / `getObject` / `headObject` / `deleteObject`

現状の実装は動作しており、ただちに全面的な抽象化を行う必然性は高くない。
この状態で大規模なリファクタを先行すると、保存処理と画像解析処理の両方に影響するため、変更リスクが高い。

## 決定

画像 storage 実装の共通化は必要と判断するが、現時点では大規模リファクタは行わない。

将来の整理方針は次のとおりとする。

1. 共通化対象は「画像処理フロー」に限定する
   - `determineExtension(...)`
   - `byte[] -> ImageMetadata`
   - `byte[] -> Dimension`
2. 保存先依存の I/O は local / s3 の実装に残す
   - local のファイル操作
   - s3 の bucket/key 解決と SDK 呼び出し
3. 抽象化が必要になった場合は、`AbstractImageStorageRepository` のような基底クラスを検討する
   - ただし共通化するのはフローだけとし、`Path` や `S3Client` を基底クラスへ直接持ち込まない
4. リファクタは段階的に進める
   - 第1段階: `determineExtension(...)` の helper 化
   - 第2段階: `byte[] -> ImageMetadata` の helper 化
   - 第3段階: 必要であれば抽象基底クラス化

## 影響

### 良い影響

- 現行実装の動作を維持しながら、将来の整理方針を明文化できる
- 保存先依存処理と画像解析処理の責務分離を崩さずに共通化を進められる
- 大規模変更を避けることで、local / s3 双方の回帰リスクを抑えられる

### 注意点

- 当面は `LocalImageRepositoryImpl` と `S3ImageRepositoryImpl` に重複が残る
- 同じバグ修正や仕様変更を 2 箇所へ入れる必要がある

## 補足

- `ImageStorage` と `ImageMetadataExtractor` を 1 実装へ統合する方針は採らない
- local / s3 を `if` 分岐で 1 クラスに押し込む方針も採らない
- 値オブジェクトや `StorageObject` の生成責務は引き続き domain / application との境界を尊重する
