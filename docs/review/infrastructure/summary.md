# Infrastructure Review Summary

## Result

- `jp.i432kg.footprint.infrastructure` パッケージ配下の前回指摘 2 件は解消済みです。
- 今回の再レビューでは、`/api` の認証系エラーが `ProblemDetail` 契約を満たしていない新規指摘 1 件を追加しました。

## Findings

- No.1: `PostMapper` / `ReplyMapper` の永続化時刻上書きと、`UserMapper` の `lastLoginAt` 初期化は解消済み。
- No.2: seed サービスの `ParentReply.root()` 未使用は解消済みで、旧 null 経路の残存も確認されなかった。
- No.3: `ApiAuthenticationFailureHandler`、`ApiAuthenticationEntryPoint`、`ApiAccessDeniedHandler` が `sendError(...)` を直接返しており、`/api/login` 認証失敗、未認証 401、403 / CSRF 拒否が `ProblemDetail`、`application/problem+json`、`errorCode` / `details` を満たしていない。
- 追加レビュー: 認証成功時の `lastLoginAt` 更新構成は継続して問題なし。一方で認証失敗系レスポンスは設計書 / OpenAPI と実装の乖離あり。

## Verification

- 前回クローズ項目の確認時: `./gradlew test` 成功
- 今回の追加レビュー: 実装・設計書・既存テストの突合を実施。追加テストは未実施
