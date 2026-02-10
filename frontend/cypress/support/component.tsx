import { mount } from "cypress/react";
import "./commands";

declare global {
  namespace Cypress {
    interface Chainable {
      mount: typeof mount;
    }
  }
}

Cypress.Commands.add("mount", (component, options) => {
  const style = document.createElement("link");
  style.rel = "stylesheet";
  style.href = "https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4";
  document.head.appendChild(style);

  return mount(component, options);
});
