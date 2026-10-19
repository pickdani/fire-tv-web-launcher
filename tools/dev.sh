#!/bin/sh
# Live editing: the TV loads the page from this computer, so you see changes without a push.
#   tools/dev.sh <tv-ip> start    serve the page and point the TV at this computer
#   tools/dev.sh <tv-ip> reload   show your latest changes on the TV
#   tools/dev.sh <tv-ip> stop     point the TV back at its own copy of the page
set -e
. "$(dirname "$0")/common.sh"
PORT=8765

connect_tv "$1"
case "$2" in
  start)
    # This computer's address on the home network (macOS, then Linux).
    ip=$(ipconfig getifaddr en0 2>/dev/null || hostname -I | cut -d' ' -f1)
    tv_shell mkdir -p "$PAGE_DEST"
    tv_shell pm grant dev.tvhome android.permission.READ_EXTERNAL_STORAGE
    tv_shell "echo http://$ip:$PORT/index.html > $PAGE_DEST/dev-url.txt"
    reload_tv_home
    echo "The TV loads http://$ip:$PORT/ now. Press Ctrl+C to stop the server."
    cd "$PAGE_SRC" && exec python3 -m http.server "$PORT"
    ;;
  reload)
    reload_tv_home
    ;;
  stop)
    tv_shell rm -f "$PAGE_DEST/dev-url.txt"
    reload_tv_home
    echo "The TV uses its own copy of the page again."
    ;;
  *)
    echo "Usage: $0 <tv-ip> start|reload|stop" >&2
    exit 1
    ;;
esac
