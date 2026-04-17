#!/bin/bash
# Start InventoryManagementService with two New Relic agents (two separate JVM processes)
# Each process reports to a different NR account.
#
# Required env vars:
#   NEW_RELIC_LICENSE_KEY_1 - Ingest license key for NR Account 1
#   NEW_RELIC_LICENSE_KEY_2 - Ingest license key for NR Account 2
#
# Usage:
#   export NEW_RELIC_LICENSE_KEY_1='your-key-here'
#   export NEW_RELIC_LICENSE_KEY_2='your-key-here'
#   ./start-dual-agent.sh

set -e

APP_JAR="build/libs/InventoryManagementService-1.0-SNAPSHOT.jar"
NR_AGENT="newrelic/newrelic.jar"

if [ -z "$NEW_RELIC_LICENSE_KEY_1" ] || [ -z "$NEW_RELIC_LICENSE_KEY_2" ]; then
  echo "ERROR: Set NEW_RELIC_LICENSE_KEY_1 and NEW_RELIC_LICENSE_KEY_2 env vars"
  exit 1
fi

if [ ! -f "$NR_AGENT" ]; then
  echo "NR agent not found. Downloading..."
  ./gradlew downloadNewrelic unzipNewrelic
fi

if [ ! -f "$APP_JAR" ]; then
  echo "App JAR not found. Building..."
  ./gradlew build -x test
fi

echo "Starting InventoryManagementService with NR Agent -> Account 1 (port 8080)..."
java -javaagent:$NR_AGENT \
  -Dnewrelic.config.file=newrelic/newrelic-account1.yml \
  -Dnewrelic.environment=production \
  -jar $APP_JAR \
  --server.port=8080 &
PID1=$!

echo "Starting InventoryManagementService with NR Agent -> Account 2 (port 8081)..."
java -javaagent:$NR_AGENT \
  -Dnewrelic.config.file=newrelic/newrelic-account2.yml \
  -Dnewrelic.environment=production \
  -jar $APP_JAR \
  --server.port=8081 &
PID2=$!

echo "Both instances running: PID1=$PID1 (Account1:8080), PID2=$PID2 (Account2:8081)"
echo "To stop: kill $PID1 $PID2"

wait $PID1 $PID2
