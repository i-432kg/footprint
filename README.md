# footprint

## ローカル起動手順

### 1. 環境変数ファイルを作成

```bash
cp .env.example .env
```

必要に応じて `.env` の値を変更してください。

### 2. MySQL を起動

```bash
docker compose up -d
```

### 3. バックエンドを local プロファイルで起動
