#!/bin/sh
# Optional: makes a Fire TV faster. It disables Amazon ads, tracking, and extras, and sets
# animations to 0.5x. Disabled packages stay on the TV, and "restore" turns them on again.
#
# Usage: tools/debloat.sh <tv-ip> [disable|restore] [--alexa]
#   --alexa  also disables Alexa (frees about 75 MB). The voice button stops working.
#
# Amazon protects some packages, and a Fire OS update can turn packages on again.
# The script skips packages that are protected or not on the TV.
set -e
. "$(dirname "$0")/common.sh"

PACKAGES="
com.amazon.tv.acr
com.amazon.hybridadidservice
com.amazon.sneakpeek
com.amazon.tv.easyupgrade
com.amazon.wirelessmetrics.service
com.amazon.device.telemetry.emitter
com.amazon.perfc
com.amazon.perfcollection
com.amazon.dp.logger
com.amazon.logan
com.amazon.media.recommendations
com.amazon.shoptv.client
com.amazon.bueller.photos
com.amazon.bueller.music
com.amazon.gamehub
com.amazon.minitv.android.app
com.amazon.ods.kindleconnect
com.amazon.storm.lightning.tutorial
com.amazon.tmm.tutorial
com.amazon.tv.releasenotes
"
ALEXA="
com.amazon.vizzini
com.amazon.alexa.externalmediaplayer.fireos
com.amazon.alexamediaplayer.runtime.ftv
"

ACTION="${2:-disable}"
[ "$3" = "--alexa" ] && PACKAGES="$PACKAGES $ALEXA"

connect_tv "$1"
for pkg in $PACKAGES; do
  if [ "$ACTION" = restore ]; then
    result=$(tv_shell pm enable "$pkg" 2>&1 | head -1)
  else
    result=$(tv_shell pm disable-user --user 0 "$pkg" 2>&1 | head -1)
  fi
  case "$result" in
    *"new state"*) echo "$pkg: ${result##*new state: }" ;;
    *"protected"*) echo "$pkg: skipped (protected by Amazon)" ;;
    *) echo "$pkg: skipped (not on this TV)" ;;
  esac
done

SCALE=0.5
[ "$ACTION" = restore ] && SCALE=1.0
for key in window_animation_scale transition_animation_scale animator_duration_scale; do
  tv_shell settings put global "$key" "$SCALE"
done
echo "animations: ${SCALE}x"
