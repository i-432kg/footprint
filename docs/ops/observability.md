# 可観測性運用メモ

作成日: 2026-04-29

## 目的

本番初期運用における healthcheck とログ確認の一次参照先を明確にする。

## 運用方針

- healthcheck
  - Railway の deploy healthcheck を正式な確認手段とする
  - アプリケーション側の確認対象は `/actuator/health` とする
- logs
  - Railway のデプロイログ / 実行ログを一次確認先とする
  - 詳細な外部監視基盤は現時点では導入しない
- アプリログの見るキー
  - `traceId`
  - `event`
  - `errorCode`
  - `status`

## 補足

- 障害時の詳細な初動手順はケースバイケースで判断する
- CloudWatch などの外部監視基盤は必要になった段階で別途検討する
