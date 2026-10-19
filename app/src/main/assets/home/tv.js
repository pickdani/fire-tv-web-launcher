// The Android side of the page (TvBridge.kt). In a desktop browser there is no window.TV,
// so a stand-in lets you preview the page with tools/dev.sh or any local web server.

import config from "./config.js";

const desktopStandIn = {
  // tv: false makes the cards show the app name, because there are no app pictures on a desktop.
  apps: () => JSON.stringify(config.favorites.map((pkg) => ({ pkg, label: pkg.split(".").pop(), tv: false }))),
  launch: (pkg) => console.log("launch", pkg) || true,
  openSettings: () => console.log("openSettings"),
  openStockHome: () => console.log("openStockHome"),
  guardEnabled: () => false,
  reload: () => location.reload(),
  version: () => "desktop",
};

export const TV = window.TV || desktopStandIn;

/** The URL of an app's banner or icon (served by HomeFiles.kt). */
export const iconUrl = (pkg) => "https://tvhome.local/icon/" + encodeURIComponent(pkg);
