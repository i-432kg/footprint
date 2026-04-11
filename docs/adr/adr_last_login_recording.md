# ADR: 最終ログイン日時更新は AuthenticationSuccessHandler を入口にし、更新処理は専用サービスへ分離する

## ステータス
Accepted

## 背景
`lastLoginAt` は「ユーザー作成日時」ではなく、「認証成功を最後に完了した日時」を表すべきである。

そのため、以下を満たす必要がある。

- ユーザー作成時には `lastLoginAt` を記録しない
- 認証成功時にのみ `lastLoginAt` を更新する
- ログイン応答制御と監査用 timestamp 更新の責務を混ぜすぎない

一方で、本プロジェクトは Spring Security のフォームログインを利用しており、認証成功時の入口は `AuthenticationSuccessHandler` が最も自然である。

## 決定
1. 認証成功のトリガは `AuthenticationSuccessHandler` を使う
2. `AuthenticationSuccessHandler` は principal から `UserId` を取り出し、専用サービスへ委譲するだけにする
3. `lastLoginAt` の更新知識と失敗時ログ出力は専用サービス `LastLoginRecorder` に集約する
4. `users.last_login_at` はユーザー作成時には更新せず、認証成功時にのみ更新する

## 理由

- `AuthenticationSuccessHandler` は認証成功時だけ確実に呼ばれるため、更新トリガとして適切
- handler に mapper 直接呼び出しや `now()` 判定を持たせると、レスポンス制御と監査更新の責務が混ざる
- 専用サービスへ寄せることで、今後 `lastLoginAt` の更新条件や監査処理が増えても handler を肥大化させずに済む
- 監査用更新が失敗しても、認証成功自体は維持したい。そのため更新失敗はサービス側でログ化し、ログイン応答は継続する

## 運用ルール

- 認証成功時の副作用追加は、まず handler で受けてから専用サービスへ委譲する
- 認証 UI と無関係な更新知識を handler に持ち込まない
- `lastLoginAt` は未ログイン状態を表せるよう、ユーザー作成時は `NULL` を維持する

## この方針で避けるもの

- 認証成功 handler の肥大化
- 監査更新ロジックの UI 層への混入
- ユーザー作成日時と最終ログイン日時の意味論混同
