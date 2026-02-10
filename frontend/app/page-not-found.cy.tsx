import { ComponentProviders } from "@/cypress/mocks/component-providers";
import { createTestStore } from "@/cypress/mocks/test-store";
import NotFound from "./not-found";

describe("<NotFound />", () => {
  it("renders 404 page with back link", () => {
    const store = createTestStore();

    cy.mount(
      <ComponentProviders store={store}>
        <NotFound />
      </ComponentProviders>,
    );

    cy.contains("404").should("exist");
    cy.contains("Page not found").should("exist");
    cy.contains("Back to dashboard")
      .should("exist")
      .and("have.attr", "href", "/");
  });
});
