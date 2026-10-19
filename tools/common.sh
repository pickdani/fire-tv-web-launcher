# Shared by the scripts in tools/. Not meant to be run by itself.

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
PAGE_SRC="$ROOT/app/src/main/assets/home"
PAGE_DEST=/sdcard/tvhome

# Connects to the TV at $1 (or $TV_IP). Sets $TV for tv_shell.
connect_tv() {
  ip="${1:-$TV_IP}"
  if [ -z "$ip" ]; then
    echo "Give the TV's IP address: $0 <tv-ip>, or set TV_IP." >&2
    echo "Find it on the TV in Settings > My Fire TV > About > Network." >&2
    exit 1
  fi
  TV="$ip:5555"
  adb connect "$TV" >/dev/null
  if [ "$(adb -s "$TV" get-state 2>/dev/null)" != device ]; then
    echo "Cannot reach $TV. Turn on ADB Debugging on the TV, and accept the prompt on the TV." >&2
    exit 1
  fi
}

tv_shell() {
  adb -s "$TV" shell "$@"
}

# Reloads TV Home. Never force-stop it: Android turns off the Home button service on force-stop.
reload_tv_home() {
  tv_shell am start -n dev.tvhome/.MainActivity --ez reload true >/dev/null 2>&1
}
