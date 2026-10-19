// Moves the focus with the arrow keys on the remote.
// The WebView does not do this by itself, so each arrow key picks the nearest button in that direction.

const DIRECTIONS = { ArrowLeft: [-1, 0], ArrowRight: [1, 0], ArrowUp: [0, -1], ArrowDown: [0, 1] };

export function enableRemoteKeys(focusFirst) {
  document.addEventListener("keydown", (event) => {
    const direction = DIRECTIONS[event.key];
    if (!direction) return;
    event.preventDefault();
    moveFocus(direction, focusFirst);
  });
}

function moveFocus([dx, dy], focusFirst) {
  const current = document.activeElement;
  const buttons = [...document.querySelectorAll("button")].filter((b) => b.offsetParent !== null);
  if (!buttons.includes(current)) return focusFirst();

  const from = center(current);
  let best = null;
  let bestScore = Infinity;
  for (const button of buttons) {
    if (button === current) continue;
    const to = center(button);
    const along = (to.x - from.x) * dx + (to.y - from.y) * dy; // Distance in the arrow's direction.
    const across = Math.abs((to.x - from.x) * dy) + Math.abs((to.y - from.y) * dx); // Distance to the side.
    if (along <= 1) continue; // Not in that direction.
    const score = along + across * 3; // Prefer buttons in a straight line.
    if (score < bestScore) {
      bestScore = score;
      best = button;
    }
  }
  best?.focus();
  best?.scrollIntoView({ block: "nearest", inline: "nearest" });
}

function center(element) {
  const r = element.getBoundingClientRect();
  return { x: r.left + r.width / 2, y: r.top + r.height / 2 };
}
