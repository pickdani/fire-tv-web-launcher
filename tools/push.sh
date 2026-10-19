#!/bin/sh
# Copies the home page to the TV and reloads Fire TV Web Launcher. No rebuild needed.
# Usage: tools/push.sh <tv-ip>
set -e
. "$(dirname "$0")/common.sh"

connect_tv "$1"
tv_shell mkdir -p "$PAGE_DEST"
adb -s "$TV" push "$PAGE_SRC/." "$PAGE_DEST/" >/dev/null
tv_shell pm grant dev.tvhome android.permission.READ_EXTERNAL_STORAGE
reload_tv_home
echo "Page copied to the TV."
