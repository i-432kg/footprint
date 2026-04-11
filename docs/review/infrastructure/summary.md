# Infrastructure Review Summary

## Result

- `jp.i432kg.footprint.infrastructure` パッケージ配下のレビューで、差し戻し対象は 2 件です。
- 永続化 mapper の timestamp 決定責務と、seed 経路の `null` 境界違反が未解消です。

## Findings

- No.1: repository mapper が `LocalDateTime.now()` を再生成し、application/domain で決めた時刻や `lastLoginAt` の意味論を崩している。
- No.2: seed サービスが `CreateReplyCommand` に `null` を渡しており、`ParentReply` 導入後の null 境界方針と整合していない。

## Verification

- 実施: `./gradlew test`
- 結果: 成功
