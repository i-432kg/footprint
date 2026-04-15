# Application Re-Review

## Findings

### Low

1. レビュー資料が現状を反映していません。[findings.md](../findings.md#L6) では No.1-3 がまだ `未対応` のままで、[summary.md](../summary.md#L5) も「未整理の箇所があります」という旧結論のままです。一方で実装側では、画像 cleanup 補償、query service の `@Nullable` 化、query model の nullable 明示が入っています。[PostCommandService.java](../../../../src/main/java/jp/i432kg/footprint/application/command/PostCommandService.java#L75) [PostQueryService.java](../../../../src/main/java/jp/i432kg/footprint/application/query/PostQueryService.java#L23) [ReplyQueryService.java](../../../../src/main/java/jp/i432kg/footprint/application/query/ReplyQueryService.java#L40) [PostSummary.java](../../../../src/main/java/jp/i432kg/footprint/application/query/model/PostSummary.java#L40) [ReplySummary.java](../../../../src/main/java/jp/i432kg/footprint/application/query/model/ReplySummary.java#L35) [LocationSummary.java](../../../../src/main/java/jp/i432kg/footprint/application/query/model/LocationSummary.java#L17)

## Assessment

- 実装面では今回対応した No.1-3 に関して新たな問題は見つかりませんでした。
- `PostCommandService` の cleanup は一次障害を優先しつつ、補償処理失敗をログへ残す形になっており妥当です。
- query service と query model の nullability 契約も、現行の MyBatis 利用方針および ADR と整合しています。

## Verification

- 実施: `./gradlew test`
- 結果: 成功
