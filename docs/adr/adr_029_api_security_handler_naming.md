# ADR: API 向け Security Handler は入口の役割で命名し、具体的な副作用は委譲先へ分離する

## ステータス

Accepted

## 背景

`SecurityConfig` では Spring Security の拡張点として、認証成功・認証失敗・未認証アクセス・認可失敗に対応する Handler / EntryPoint を差し込んでいる。

現状のクラス名には、`AuthLoggingAccessDeniedHandler` や `LastLoginUpdatingAuthenticationSuccessHandler` のように、当初の主な副作用をそのまま表したものがある。

このうち `LastLoginUpdatingAuthenticationSuccessHandler` は、最終ログイン日時更新だけでなく認証成功ログも出力しており、クラス名と実装の責務表現にずれが生じ始めている。

一方で、これらのクラスは `SecurityConfig` 上では `/api/**` 向けの HTTP 応答制御の入口として配線されている。
今後、認証成功や失敗に付随する監査ログ、メトリクス、通知などの副作用が増える可能性があるたびに Handler 名を都度変更すると、設定コードと設計意図の追跡性が落ちやすい。

ただし、単に名前だけを汎用化すると、Handler 自身が多機能化しやすくなり、何をしているクラスかが不明瞭になる。

## 決定

`SecurityConfig` で API 向けに利用する Spring Security の入口クラスは、個別の副作用名ではなく API 境界での役割を表す名前で命名する。

採用する命名は次のとおり。

1. `ApiAuthenticationFailureHandler`
2. `ApiAuthenticationEntryPoint`
3. `ApiAccessDeniedHandler`
4. `ApiAuthenticationSuccessHandler`

あわせて、広い名前を採用する場合の実装方針を次のとおり定める。

1. Handler / EntryPoint 自身は Spring Security の入口としての制御に留める
2. 最終ログイン日時更新、監査ログ、メトリクス送信などの具体的な副作用は、責務ごとの collaborator へ委譲する
3. 委譲先のクラス名は `LastLoginRecorder` のように具体的な責務を表す
4. `Default...` や `...Impl` のような、役割が読めない汎用名は採用しない

## 理由

- `SecurityConfig` 上では「API 認証成功時の入口」「API 認可失敗時の入口」として読む方が自然であり、設定の意図が分かりやすい
- 将来副作用が追加されても、入口クラス名を毎回変更せずに済む
- 一方で副作用を Handler に抱え込むと肥大化しやすいため、広い名前を採るなら内部は委譲前提にする必要がある
- 具体的な処理内容は collaborator の名前で追えるため、オーケストレータとしての Handler と業務副作用の責務境界を保ちやすい

## 命名ルール

- API 向けに `SecurityConfig` へ配線する Spring Security 拡張点は、`Api` 接頭辞で用途境界を表す
- 入口クラス名には、単一のログ出力や更新処理だけを反映しない
- 具体的な副作用が増えても、まずは委譲先の追加で吸収し、入口クラス名の変更は原則行わない
- API 用と画面遷移用で振る舞いが分かれる場合は、境界ごとに別クラスを作り、それぞれの用途を名前に出す

## 既存 ADR との関係

- [adr_013_last_login_recording.md](./adr_013_last_login_recording.md) で定めた「認証成功の入口は `AuthenticationSuccessHandler` とし、更新知識は専用サービスへ分離する」方針は維持する
- 本 ADR は、その入口クラスの命名を API 境界で安定させる判断を追加するものである

## 影響

### 良い影響

- `SecurityConfig` の配線意図が読みやすくなる
- 将来の副作用追加時に、クラス名変更の連鎖を減らせる
- 入口クラスと業務副作用の責務分離を明文化できる

### 注意点

- `ApiAuthenticationSuccessHandler` のような広い名前は、委譲設計なしで使うと何でも載るクラスになりやすい
- 将来 `/api/**` 以外でも共通利用するようになった場合は、`Api` 接頭辞が実態とずれないかを再評価する

## 不採用案

### 既存の具体名をそのまま維持する

今回は不採用。

`LastLoginUpdatingAuthenticationSuccessHandler` のように副作用が増えた時点で、名前と実装の乖離が発生しやすい。
API 境界の入口として安定した命名に寄せた方が、設定コードの可読性と将来拡張の両立がしやすい。

### さらに広い `SecurityAuthenticationSuccessHandler` のような名前にする

今回は不採用。

現状の利用箇所は `SecurityConfig` の `/api/**` 向け応答制御であり、まずは `Api` という用途境界までを名前に出す方が適切である。
必要以上に抽象化すると、利用コンテキストが見えにくくなる。
