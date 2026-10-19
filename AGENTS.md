# Notes for AI agents

TV Home is a custom launcher (home screen) for Amazon Fire TV. It works without root. The
screen is a web page in a WebView, and a small Kotlin app gives the page access to the apps.

Read [CONTRIBUTING.md](CONTRIBUTING.md) first. It shows the file layout and the page API.

## Rules

- Do not run `am force-stop dev.tvhome`. Android then turns off the Home button service.
  To reload the page, run `am start -n dev.tvhome/.MainActivity --ez reload true`.
- Always install with `tools/install.sh <tv-ip>`. It turns the Home button service on again.
- Keep the page in plain JavaScript modules. Do not add a build step or a framework.
- Do not add tracking, analytics, or personal data (names, emails, IP addresses).

## Build and check

```sh
./gradlew assembleRelease
```

You can open the page in a desktop browser: run `python3 -m http.server` in
`app/src/main/assets/home/`. `tv.js` gives the page a stand-in for `window.TV`.
