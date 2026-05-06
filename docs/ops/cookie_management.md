# Cookie 管理表

作成日: 2026-04-29

## 目的

認証・CSRF に利用する Cookie について、環境ごとの運用方針を継続参照できる形で整理する。

## 対象 Cookie

- `JSESSIONID`
  - 認証セッション保持用 Cookie
- `XSRF-TOKEN`
  - CSRF 対策用 Cookie

## 運用方針

- `JSESSIONID`
  - `HttpOnly=true`
  - `local` は `Secure=false`
  - `stg / prod` は `Secure=true`
  - `SameSite=Lax`
  - `Domain` は明示せず host-only cookie とする
- `XSRF-TOKEN`
  - `HttpOnly=false`
  - `local` は `Secure=false`
  - `stg / prod` は `Secure=true`
  - `SameSite=Lax`
  - `Domain` は明示せず host-only cookie とする

## 環境別設定表

| Cookie | 用途 | local | stg | prod | 備考 |
|---|---|---|---|---|---|
| `JSESSIONID` | 認証セッション保持 | `HttpOnly=true` `Secure=false` `SameSite=Lax` `Domain=host-only` | `HttpOnly=true` `Secure=true` `SameSite=Lax` `Domain=host-only` | `HttpOnly=true` `Secure=true` `SameSite=Lax` `Domain=host-only` | logout 時に削除する |
| `XSRF-TOKEN` | CSRF 対策 | `HttpOnly=false` `Secure=false` `SameSite=Lax` `Domain=host-only` | `HttpOnly=false` `Secure=true` `SameSite=Lax` `Domain=host-only` | `HttpOnly=false` `Secure=true` `SameSite=Lax` `Domain=host-only` | JavaScript から参照できる必要がある |

## 補足

- `SameSite=Strict` は将来の画面遷移や認証フロー変更で制約が強くなりやすいため採用しない
- `Secure=true` は HTTPS 前提の `stg / prod` で採用し、HTTP ローカル開発では採用しない
- `Domain` は誤って広く配布しないため、現時点では明示設定しない
- 実装は Spring Security と session cookie 設定で反映する
