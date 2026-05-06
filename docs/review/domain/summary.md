# Domain Review Summary

## Result

- 現時点で差し戻しが必要な未解決の指摘はありません。
- domain パッケージのレビューはクローズして問題ないと判断します。

## Assessment

- No.1: domain service から Spring / application 依存は外れており、Bean 化は config 側へ移されています。[UserDomainService.java](../../src/main/java/jp/i432kg/footprint/domain/service/UserDomainService.java#L1) [DomainServiceConfig.java](../../src/main/java/jp/i432kg/footprint/config/DomainServiceConfig.java#L1)
- No.2: ID 系値オブジェクトは `UlidValidation` 経由で同一の検証ロジックに揃っています。[UlidValidation.java](../../src/main/java/jp/i432kg/footprint/domain/helper/UlidValidation.java#L1)
- No.3: 例外 details と validation rejectedValue のマスキングは `SensitiveDataMasker` と `GlobalExceptionHandler` に集約され、`RawPassword` 自身の `toString()` マスキングも維持されています。[SensitiveDataMasker.java](../../src/main/java/jp/i432kg/footprint/logging/masking/SensitiveDataMasker.java#L1) [GlobalExceptionHandler.java](../../src/main/java/jp/i432kg/footprint/presentation/api/GlobalExceptionHandler.java#L1) [RawPassword.java](../../src/main/java/jp/i432kg/footprint/domain/value/RawPassword.java#L18)
- No.4: 画像保存/解析の技術ポートは domain から application へ移っており、境界の抽象度は改善されています。
- No.5: `Location.unknown()` / `ParentReply.root()` により、状態表現としての `null` は整理されています。ADR の nullable 境界方針とも整合しています。
- No.6: 値オブジェクトの accessor は `getValue()` に統一され、利用側も揃っています。
- No.7: `Image` に短辺 320px のバリデーションが追加されています。[Image.java](../../src/main/java/jp/i432kg/footprint/domain/model/Image.java#L95)

## Checks

- [No.4 / No.5 Check](./checks/2026-04-11-no4-no5-check.md)
- [High Re-Review](./checks/high-rereview.md)

## Verification

- 実施: `./gradlew test`
- 結果: 成功
