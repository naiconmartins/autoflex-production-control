import { ComponentProviders } from "@/cypress/mocks/component-providers";
import { createTestStore } from "@/cypress/mocks/test-store";
import { ClientAuthBootstrap } from "./client-auth-bootstrap";

describe("<ClientAuthBootstrap />", () => {
  it("mounts without crashing", () => {
    const store = createTestStore();

    cy.mount(
      <ComponentProviders store={store}>
        <ClientAuthBootstrap />
      </ComponentProviders>,
    );

    cy.wrap(true).should("be.true");
  });
});
