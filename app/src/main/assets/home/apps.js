// The favorites row and the "All apps" grid.

import config from "./config.js";
import { TV, iconUrl } from "./tv.js";

const favoritesRow = document.getElementById("favorites");
const allSection = document.getElementById("all-section");
const allGrid = document.getElementById("all-apps");
const favoritesSection = document.getElementById("favorites-section");
const allButton = document.getElementById("btn-all");

let apps = [];

export function loadApps() {
  try {
    apps = JSON.parse(TV.apps());
  } catch {
    apps = [];
  }
  renderFavorites();
  if (isAllShown()) renderAll();
}

export const isAllShown = () => !allSection.hidden;

export function showAll(show) {
  allSection.hidden = !show;
  favoritesSection.hidden = show;
  allButton.textContent = show ? "Back" : "All apps";
  if (show) renderAll();
  focusFirst();
}

export function focusFirst() {
  const container = isAllShown() ? allGrid : favoritesRow;
  (container.querySelector(".app") || allButton).focus();
}

function renderFavorites() {
  const focused = document.activeElement?.dataset.pkg;
  const byPkg = new Map(apps.map((app) => [app.pkg, app]));
  const cards = config.favorites.filter((pkg) => byPkg.has(pkg)).map((pkg) => appCard(byPkg.get(pkg)));
  favoritesRow.replaceChildren(...cards);
  // Keep the focus on the same app after a refresh.
  favoritesRow.querySelector(`[data-pkg="${focused}"]`)?.focus();
}

function renderAll() {
  const hidden = new Set(config.hidden);
  const cards = apps
    .filter((app) => !hidden.has(app.pkg))
    .sort((a, b) => a.label.localeCompare(b.label))
    .map(appCard);
  allGrid.replaceChildren(...cards);
}

// Apps made for TV have a wide banner. Other apps have a square icon, so they also show their name.
function appCard(app) {
  const card = document.createElement("button");
  card.className = app.tv ? "app" : "app icon-only";
  card.dataset.pkg = app.pkg;
  card.style.backgroundImage = `url("${iconUrl(app.pkg)}")`;
  card.setAttribute("aria-label", app.label);
  if (!app.tv) {
    const label = document.createElement("span");
    label.className = "label";
    label.textContent = app.label;
    card.append(label);
  }
  card.addEventListener("click", () => TV.launch(app.pkg));
  return card;
}
