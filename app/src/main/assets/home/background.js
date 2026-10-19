// The background image. With a list of images, it fades to the next one every backgroundMinutes.

import config from "./config.js";

// Two layers, so one image can fade in over the other.
const layers = [document.getElementById("background-a"), document.getElementById("background-b")];
let front = 0;

export function startBackground() {
  const images = [].concat(config.background || []);
  if (images.length === 0) return;

  let index = 0;
  show(images[index]);
  if (images.length > 1 && config.backgroundMinutes > 0) {
    setInterval(() => {
      index = (index + 1) % images.length;
      show(images[index]);
    }, config.backgroundMinutes * 60_000);
  }
}

function show(src) {
  // Load the image first, so the screen never fades to black.
  const img = new Image();
  img.onload = () => {
    const back = layers[1 - front];
    back.style.backgroundImage = `url("${src}")`;
    back.classList.add("shown");
    layers[front].classList.remove("shown");
    front = 1 - front;
  };
  img.src = src;
}
