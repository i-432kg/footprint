#!/usr/bin/env bash

# Spring Boot アプリを bootRun で起動し、OpenAPI YAML を取得して保存してから停止する。
# 前提条件:
# - ローカル起動に必要な依存サービス（例: MySQL）が利用可能であること
# - local / dev プロファイルで /v3/api-docs.yaml へアクセスできること
# - curl が利用可能であること
# 使い方:
# - ./scripts/run_bootrun_and_export_openapi_yaml.sh
# - ./scripts/run_bootrun_and_export_openapi_yaml.sh http://localhost:8081 docs/design/generated/local-openapi.yaml
# 引数:
# - 第1引数: OpenAPI を取得するアプリのベース URL（省略時: http://localhost:8080）
# - 第2引数: 保存先パス（省略時: docs/design/generated/openapi.yaml）

set -euo pipefail

BASE_URL="${1:-http://localhost:8080}"
OUTPUT_PATH="${2:-docs/design/generated/openapi.yaml}"
API_DOCS_PATH="${API_DOCS_PATH:-/v3/api-docs.yaml}"
HEALTH_PATH="${HEALTH_PATH:-/actuator/health}"
WAIT_SECONDS="${WAIT_SECONDS:-120}"
BOOTRUN_LOG_PATH="${BOOTRUN_LOG_PATH:-/tmp/footprint-bootrun.log}"

APP_PID=""

cleanup() {
  if [[ -n "$APP_PID" ]] && kill -0 "$APP_PID" >/dev/null 2>&1; then
    kill "$APP_PID" >/dev/null 2>&1 || true
    wait "$APP_PID" >/dev/null 2>&1 || true
  fi
}

trap cleanup EXIT

if ! command -v curl >/dev/null 2>&1; then
  echo "curl is required but not installed." >&2
  exit 1
fi

mkdir -p "$(dirname "$OUTPUT_PATH")"
mkdir -p "$(dirname "$BOOTRUN_LOG_PATH")"

echo "Starting Spring Boot with ./gradlew bootRun"
./gradlew bootRun >"$BOOTRUN_LOG_PATH" 2>&1 &
APP_PID=$!

echo "Waiting for application startup at ${BASE_URL}"
SECONDS_WAITED=0
until curl --fail --silent "${BASE_URL}${HEALTH_PATH}" >/dev/null 2>&1 \
  && curl --fail --silent "${BASE_URL}${API_DOCS_PATH}" >/dev/null 2>&1; do
  if ! kill -0 "$APP_PID" >/dev/null 2>&1; then
    echo "bootRun exited before OpenAPI export completed." >&2
    echo "Last bootRun log lines:" >&2
    tail -n 50 "$BOOTRUN_LOG_PATH" >&2 || true
    exit 1
  fi

  if (( SECONDS_WAITED >= WAIT_SECONDS )); then
    echo "Timed out waiting for application startup after ${WAIT_SECONDS} seconds." >&2
    echo "Last bootRun log lines:" >&2
    tail -n 50 "$BOOTRUN_LOG_PATH" >&2 || true
    exit 1
  fi

  sleep 2
  SECONDS_WAITED=$((SECONDS_WAITED + 2))
done

echo "Exporting OpenAPI YAML from ${BASE_URL}${API_DOCS_PATH}"
curl --fail --silent --show-error \
  "${BASE_URL}${API_DOCS_PATH}" \
  --output "$OUTPUT_PATH"

echo "Saved OpenAPI YAML to ${OUTPUT_PATH}"
echo "bootRun log: ${BOOTRUN_LOG_PATH}"
