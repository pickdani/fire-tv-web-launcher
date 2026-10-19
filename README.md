# Fire TV Web Launcher

An open-source custom launcher for Amazon Fire TV, Fire TV Stick, and Fire TV Edition TVs
(Insignia, Toshiba). The home screen is a web page, so you can change it with HTML, CSS, and
JavaScript.

- Shows your favorite apps, the time, the weather, and a background photo.
- Replaces the Amazon home screen when you push Home. No root needed.
- Has no ads and no tracking.
- Includes an optional script that disables Amazon ads, tracking, and bloatware over ADB.

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

- The Amazon home screen can show for a moment before Fire TV Web Launcher covers it.
- A Fire OS update can stop the Home button method from working.
- A force-stop of Fire TV Web Launcher turns off the Home button service. Run `tools/install.sh` again.

## Compared to other launchers

[Projectivy Launcher](https://play.google.com/store/apps/details?id=com.spocky.projengmenu)
and [LTvLauncher](https://github.com/leanbitlab-org/LtvLauncher) also replace the Fire TV home
screen. Projectivy is not open source. LTvLauncher's Home button method did not work on
Fire OS 8.1.8 in our tests. Fire TV Web Launcher is open source, and you design the whole screen yourself.

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md).

## License

[MIT](LICENSE.md). The photos have their own licenses (below).

## Credits

- Photos from Wikimedia Commons, cropped and resized. The app icon and banner come from the first photo.
  - [Charles River Esplanade](https://commons.wikimedia.org/wiki/File:Charles_River_Esplanade_Boston_November_2016_003.jpg)
    by King of Hearts, [CC BY-SA 4.0](https://creativecommons.org/licenses/by-sa/4.0)
  - [Boston Common in fall](https://commons.wikimedia.org/wiki/File:Boston_Common_in_fall.jpg)
    by Chase Elliott Clark, [CC BY 2.0](https://creativecommons.org/licenses/by/2.0)
  - [Boston Public Garden](https://commons.wikimedia.org/wiki/File:Boston_Public_Garden_November_2016_004.jpg)
    by King of Hearts, [CC BY-SA 4.0](https://creativecommons.org/licenses/by-sa/4.0)
- Weather data from [Open-Meteo](https://open-meteo.com), [CC BY 4.0](https://creativecommons.org/licenses/by/4.0).
