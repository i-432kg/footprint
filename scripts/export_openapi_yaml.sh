#!/usr/bin/env bash

# OpenAPI YAML をローカル起動中の Spring Boot アプリから取得して保存する。
# 前提条件:
# - アプリが起動済みであること
# - local / dev プロファイルで /v3/api-docs.yaml へアクセスできること
# - curl が利用可能であること

set -euo pipefail

BASE_URL="${1:-http://localhost:8080}"
OUTPUT_PATH="${2:-docs/design/generated/openapi.yaml}"
API_DOCS_PATH="${API_DOCS_PATH:-/v3/api-docs.yaml}"

if ! command -v curl >/dev/null 2>&1; then
  echo "curl is required but not installed." >&2
  exit 1
fi

mkdir -p "$(dirname "$OUTPUT_PATH")"

echo "Exporting OpenAPI YAML from ${BASE_URL}${API_DOCS_PATH}"
curl --fail --silent --show-error \
  "${BASE_URL}${API_DOCS_PATH}" \
  --output "$OUTPUT_PATH"

echo "Saved OpenAPI YAML to ${OUTPUT_PATH}"
