#!/bin/sh

API_LEVEL=$1
SKIN=$2

TARGET=""
if [ $API_LEVEL -ge 28 ]; then
    TARGET="google_apis_playstore"
else
    TARGET="default"
fi

set -x
set +e
echo "Creating emulator..."
echo no | $ANDROID_HOME/cmdline-tools/latest/bin/avdmanager create avd --force -n test --package "system-images;android-$API_LEVEL;$TARGET;x86_64"
echo "Starting emulator..."
$ANDROID_HOME/emulator/emulator -avd test -no-snapshot-save -no-window -no-metrics -gpu swiftshader_indirect -noaudio -no-boot-anim -camera-back none -skin $SKIN &

start_time=$(date +%s)
end_time=$((start_time + 60))
while [ -z $($ANDROID_HOME/platform-tools/adb shell getprop sys.boot_completed) ]; do
    current_time=$(date +%s)
    if [ $current_time -ge $end_time ]; then
        exit 1
    fi
done

./gradlew connectedCheck
