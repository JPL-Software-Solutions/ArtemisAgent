#!/bin/sh

API_LEVEL=$1
ORIENTATION=$2

set -x
set +e
echo "Starting instrumented tests..."
./gradlew connectedCheck &
TEST_PID=$!
sleep 10
echo "Starting the screen recording..."
adb exec-out "while true; do screenrecord --bugreport --output-format=h264 -; done" | ffmpeg -i - testRecording-$API_LEVEL-$ORIENTATION.mp4 &
RECORD_PID=$!
sleep 1
echo "Waiting for instrumented tests to finish..."
wait $TEST_PID
TEST_STATUS=$?
# Terminate the screen recording process
kill $RECORD_PID 2>/dev/null || true
wait $RECORD_PID 2>/dev/null || true
exit $TEST_STATUS
