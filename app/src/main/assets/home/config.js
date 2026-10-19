// Edit this file to change what the home screen shows.
export default {
  // Apps on the main row, in this order, by package name.
  // "All apps" on the TV shows every app. Or run: adb shell pm list packages
  favorites: [
    "com.netflix.ninja",               // Netflix
    "com.hbo.hbonow",                  // Max
    "com.hulu.plus",                   // Hulu
    "com.disney.disneyplus",           // Disney+
    "com.amazon.firetv.youtube",       // YouTube
    "com.amazon.firetv.youtube.tv",    // YouTube TV
    "com.apple.atve.amazon.appletv",   // Apple TV
    "com.spotify.tv.android",          // Spotify
    "com.bloomberg.btva",              // Bloomberg
  ],

  // Apps never shown in "All apps".
  hidden: [
    "com.amazon.tv.launcher",
  ],

  // Weather from Open-Meteo (free, no account). Set to null to hide it.
  weather: {
    city: "Boston",
    latitude: 42.3601,
    longitude: -71.0589,
    units: "fahrenheit",               // or "celsius"
    refreshMinutes: 15,
  },

  // One image or a list: files in this folder, or https:// links.
  // With a list, the background fades to the next one every backgroundMinutes.
  background: [
    "fall-esplanade.jpg",
    "fall-common.jpg",
    "fall-public-garden.jpg",
  ],
  backgroundMinutes: 30,
};
