# Contributing

Thanks for your help. Bug reports, ideas, and pull requests are all welcome.

## How it works

TV Home has two parts:

1. **The home page** (HTML, CSS, JavaScript) in `app/src/main/assets/home/`. It draws
   everything on the screen. Most changes go here.
2. **A small Android app** (Kotlin) in `app/src/main/kotlin/dev/tvhome/`. It shows the page in
   a full-screen WebView and gives the page a few functions to list and open apps.

```
app/src/main/
├── assets/home/           The home page
│   ├── config.js          Settings that users change: apps, weather city, photos
│   ├── main.js            Starts the page, clock, calls from Android
│   ├── apps.js            Favorites row and "All apps" grid
│   ├── weather.js         Weather from Open-Meteo
│   ├── icons.js           Weather icons (SVG)
│   ├── background.js      Background photos
│   ├── remote.js          Arrow-key focus for the TV remote
│   ├── tv.js              Link to Android, and a stand-in for desktop browsers
│   ├── index.html
│   └── style.css
├── kotlin/dev/tvhome/
│   ├── MainActivity.kt    The full-screen WebView
│   ├── TvBridge.kt        The window.TV functions
│   ├── HomeFiles.kt       Serves the page and the app icons to the WebView
│   └── HomeGuardService.kt  Brings TV Home back when the Amazon home screen opens
└── res/xml/home_guard.xml What the Home button service can see
tools/                     Scripts to install, push the page, live-edit, and debloat
```

### The Home button

Fire OS handles the Home button before apps can see it. So `HomeGuardService` is an
accessibility service that watches for the Amazon home screen to open, and then opens
TV Home on top of it. It gets only package names of windows. It cannot read the screen.
It runs in its own process, so it stays alive when Android frees the WebView's memory.

## Page API

The page calls Android through `window.TV` (see `TvBridge.kt`):

| Call | Result |
|---|---|
| `TV.apps()` | JSON text: a list of `{pkg, label, tv}`. `tv` is true for apps made for TV. |
| `TV.launch(pkg)` | Opens the app. Returns false if it cannot. |
| `TV.openSettings()` | Opens the Fire TV settings |
| `TV.openStockHome()` | Opens the Amazon home screen until the user opens another app |
| `TV.guardEnabled()` | True if the Home button service is on |
| `TV.reload()` | Loads the page again |
| `TV.version()` | The app version |

`https://tvhome.local/icon/<pkg>` is the app's banner (320 × 180) or icon (192 × 192).

Android calls these functions on the page if they exist: `onShow()`, `onHide()`,
`onHome()` (Home pushed while TV Home is in front), `onBack()`, and `onAppsChanged()`.

## Set up

You need `adb`, Java 21, and the Android SDK (or Android Studio). On macOS:

```sh
brew install openjdk@21 android-platform-tools
brew install --cask android-commandlinetools
```

Build the APK with `./gradlew assembleRelease`, or build and install it with
`tools/install.sh <tv-ip>`.

## Work on the page

- `tools/dev.sh <tv-ip> start` serves the page from your computer, and the TV loads it from
  there. After a change, run `tools/dev.sh <tv-ip> reload`.
- You can also open the page in a desktop browser. Run `python3 -m http.server` in
  `app/src/main/assets/home/` and open `http://localhost:8000`. `tv.js` gives the page a
  stand-in for `window.TV`.
- Lay out the page for a 1920 × 1080 screen.
- Use plain JavaScript modules. Do not add a build step or a framework.

## Code style

- Keep it simple. A new contributor must understand each file in a few minutes.
- Write short comments that say *why*, not *what*.
- Kotlin: follow the [Kotlin style guide](https://kotlinlang.org/docs/coding-conventions.html).
- Do not add libraries unless there is no simple way without them.
- Do not add tracking, analytics, or personal data (names, emails, IP addresses).
- Do not run `am force-stop dev.tvhome`. Android then turns off the Home button service.
  To reload the page, run `adb shell am start -n dev.tvhome/.MainActivity --ez reload true`.

## Test a change

Before you open a pull request, check these on a Fire TV:

- [ ] The home page shows, and the arrow keys move between all buttons.
- [ ] An app opens, and Home brings TV Home back.
- [ ] "All apps" opens, and Back closes it.
- [ ] After `tools/install.sh`, the Home button still works.

GitHub builds the APK for each pull request. Download it from the **Actions** tab.

## Report a bug

Open an issue. Give the Fire TV model and the Fire OS version
(**Settings → My Fire TV → About**), what you did, and what happened.
