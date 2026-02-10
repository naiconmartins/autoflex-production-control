import { StoreProvider } from "./store-provider";

describe("<StoreProvider />", () => {
  it("renders children", () => {
    cy.mount(
      <StoreProvider>
        <div data-cy="store-provider-child">Child Content</div>
      </StoreProvider>,
    );

    cy.get('[data-cy="store-provider-child"]').should("exist");
  });
});
