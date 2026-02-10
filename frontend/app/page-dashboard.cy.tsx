import { ComponentProviders } from "@/cypress/mocks/component-providers";
import { createTestStore } from "@/cypress/mocks/test-store";
import Dashboard from "./page";

describe("<Dashboard />", () => {
  it("renders loading state when user is not authenticated", () => {
    const store = createTestStore();

    cy.mount(
      <ComponentProviders store={store}>
        <Dashboard />
      </ComponentProviders>,
    );

    cy.contains("Loading...").should("exist");
  });
});
