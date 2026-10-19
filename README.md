# TV Home

An open-source home screen for Fire TV. The home screen is a web page, so you can change it
with HTML, CSS, and JavaScript.

- Shows your favorite apps, the time, the weather, and a background photo.
- Replaces the Amazon home screen when you push Home. No root needed.
- Has no ads and no tracking.

Tested on Fire OS 8.1 (Android 11).

## Install

You need `adb`, Java 21, and the Android SDK.

1. On the TV, go to **Settings → My Fire TV → About**, and select the TV name 7 times.
2. Go to **Settings → My Fire TV → Developer Options**, and turn on **ADB Debugging**.
3. Find the TV's IP address in **Settings → My Fire TV → About → Network**.
4. Run:

   ```sh
   tools/install.sh <tv-ip>
   ```

5. Accept the "Allow USB debugging?" prompt on the TV.

> **Always update with `tools/install.sh`.** Android turns off the Home button service
> when the app updates, and the script turns it on again.

## Change the home screen

Edit the files in [`app/src/main/assets/home/`](app/src/main/assets/home/). Start with
[`config.js`](app/src/main/assets/home/config.js): it sets your apps, the weather city, and
the background photos. Then copy the page to the TV. You do not need to rebuild:

```sh
tools/push.sh <tv-ip>
```

To see changes on the TV as you work, run `tools/dev.sh <tv-ip> start`.

## Make the TV faster (optional)

[`tools/debloat.sh`](tools/debloat.sh) disables Amazon ads, tracking, and extras, and
speeds up animations. `tools/debloat.sh <tv-ip> restore` undoes it.

## Limits

- The Amazon home screen can show for a moment before TV Home covers it.
- A Fire OS update can stop the Home button method from working.
- A force-stop of TV Home turns off the Home button service. Run `tools/install.sh` again.

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md).

## License

[GPL-3.0](LICENSE). The photos have their own licenses: see [CREDITS.md](CREDITS.md).
