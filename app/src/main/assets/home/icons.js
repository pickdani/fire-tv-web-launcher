// Minimal line icons for the weather (24 × 24, drawn with the text color).

const svg = (body) =>
  `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" ` +
  `stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">${body}</svg>`;

const sun =
  '<circle cx="12" cy="12" r="4"/>' +
  '<path d="M12 2.5v2M12 19.5v2M2.5 12h2M19.5 12h2M5.3 5.3l1.4 1.4M17.3 17.3l1.4 1.4M5.3 18.7l1.4-1.4M17.3 6.7l1.4-1.4"/>';
const moon = '<path d="M20 14.5A8 8 0 0 1 9.5 4a8 8 0 1 0 10.5 10.5z"/>';
const cloud = '<path d="M7 18h10a4 4 0 0 0 .6-7.95A6 6 0 0 0 6.1 11.1 3.5 3.5 0 0 0 7 18z"/>';

// A cloud moved up, to make room for rain or snow below it.
const raisedCloud = `<g transform="translate(0 -3)">${cloud}</g>`;
// A smaller cloud at the bottom right, with a small sun or moon behind it.
const frontCloud = `<g transform="translate(5 4) scale(.8)">${cloud}</g>`;
const smallSun = `<g transform="translate(1 1) scale(.6)">${sun}</g>`;
const smallMoon = `<g transform="translate(1 1) scale(.6)">${moon}</g>`;

export const ICONS = {
  clear: svg(sun),
  clearNight: svg(moon),
  partlyCloudy: svg(smallSun + frontCloud),
  partlyCloudyNight: svg(smallMoon + frontCloud),
  cloudy: svg(cloud),
  fog: svg('<path d="M4 9h16M6 13h12M4 17h16"/>'),
  drizzle: svg(raisedCloud + '<path d="M9 19v1M15 19v1"/>'),
  rain: svg(raisedCloud + '<path d="M8 18l-1 3M12 18l-1 3M16 18l-1 3"/>'),
  snow: svg(raisedCloud + '<path d="M8 19h.01M12 21h.01M16 19h.01"/>'),
  storm: svg(raisedCloud + '<path d="M12.5 15l-2 3.5h3L11.5 22"/>'),
};
