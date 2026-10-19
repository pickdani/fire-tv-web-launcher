#!/bin/sh
# Builds Fire TV Web Launcher, installs it, turns on the Home button service, and copies the page.
# Usage: tools/install.sh <tv-ip>
#
# Always install with this script. Android turns the Home button service off when the app
# updates, and this script turns it on again.
set -e
. "$(dirname "$0")/common.sh"
GUARD=dev.tvhome/dev.tvhome.HomeGuardService

connect_tv "$1"
(cd "$ROOT" && ./gradlew -q assembleRelease)
adb -s "$TV" install -r "$ROOT/app/build/outputs/apk/release/app-release.apk"

# Adds the guard to the enabled accessibility services and keeps the others.
enable_guard() {
  current=$(tv_shell settings get secure enabled_accessibility_services | tr -d '\r')
  case "$current" in
    *"$GUARD"*) ;;
    "" | null) tv_shell settings put secure enabled_accessibility_services "$GUARD" ;;
    *) tv_shell settings put secure enabled_accessibility_services "$current:$GUARD" ;;
  esac
  tv_shell settings put secure accessibility_enabled 1
}

guard_running() {
  tv_shell dumpsys accessibility | grep -q "Bound services:{Service\[label=Fire TV Web Launcher"
}

# Android clears the setting while it finishes the update, so wait, set it, and check it.
for attempt in 1 2 3 4 5; do
  sleep 3
  enable_guard
  sleep 2
  if guard_running; then
    echo "Home button service: on"
    break
  fi
  if [ "$attempt" = 5 ]; then
    echo "Warning: the Home button service did not start. Run this script again." >&2
  fi
done

"$ROOT/tools/push.sh" "${TV%:5555}"
