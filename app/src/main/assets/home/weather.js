// Current weather from Open-Meteo (https://open-meteo.com). Free, no account, no key.

import config from "./config.js";
import { ICONS } from "./icons.js";

const box = document.getElementById("weather");
const iconEl = document.getElementById("weather-icon");
const tempEl = document.getElementById("weather-temp");
const detailEl = document.getElementById("weather-detail");

// WMO weather codes: https://open-meteo.com/en/docs (see "WMO Weather interpretation codes").
const CONDITIONS = [
  { codes: [0], text: "Clear", day: "clear", night: "clearNight" },
  { codes: [1, 2], text: "Partly cloudy", day: "partlyCloudy", night: "partlyCloudyNight" },
  { codes: [3], text: "Cloudy", day: "cloudy" },
  { codes: [45, 48], text: "Fog", day: "fog" },
  { codes: [51, 53, 55, 56, 57], text: "Drizzle", day: "drizzle" },
  { codes: [61, 63, 65, 66, 67, 80, 81, 82], text: "Rain", day: "rain" },
  { codes: [71, 73, 75, 77, 85, 86], text: "Snow", day: "snow" },
  { codes: [95, 96, 99], text: "Thunderstorm", day: "storm" },
];

let lastUpdate = 0;

/** Fetches the weather if the last update is older than refreshMinutes (or always, if forced). */
export async function updateWeather(force = false) {
  const w = config.weather;
  if (!w) return;
  if (!force && Date.now() - lastUpdate < w.refreshMinutes * 60_000) return;

  const params = new URLSearchParams({
    latitude: w.latitude,
    longitude: w.longitude,
    current: "temperature_2m,weather_code,is_day",
    daily: "temperature_2m_max,temperature_2m_min,precipitation_probability_max",
    temperature_unit: w.units === "celsius" ? "celsius" : "fahrenheit",
    timezone: "auto",
    forecast_days: 1,
  });
  try {
    const res = await fetch("https://api.open-meteo.com/v1/forecast?" + params);
    if (!res.ok) throw new Error("HTTP " + res.status);
    show(await res.json(), w.city);
    lastUpdate = Date.now();
  } catch (e) {
    console.warn("weather failed, keeping the last value", e); // Try again on the next refresh.
  }
}

function show(data, city) {
  const { current, daily } = data;
  const condition = CONDITIONS.find((c) => c.codes.includes(current.weather_code));
  const icon = current.is_day ? condition?.day : condition?.night || condition?.day;
  const high = Math.round(daily.temperature_2m_max[0]);
  const low = Math.round(daily.temperature_2m_min[0]);
  const rain = daily.precipitation_probability_max[0];

  iconEl.innerHTML = ICONS[icon] || "";
  tempEl.textContent = Math.round(current.temperature_2m) + "°";
  detailEl.textContent = [city, condition?.text, `H ${high}°  L ${low}°`, rain >= 30 ? `${rain}% rain` : null]
    .filter(Boolean)
    .join(" · ");
  box.hidden = false;
}
