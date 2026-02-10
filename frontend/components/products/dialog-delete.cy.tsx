import { ComponentProviders } from "@/cypress/mocks/component-providers";
import { createTestStore } from "@/cypress/mocks/test-store";
import DeleteProduct from "./dialog-delete";

describe("<DeleteProduct />", () => {
  it("renders delete trigger", () => {
    const store = createTestStore();
    const row = {
      getValue: () => "1",
    } as any;

    cy.mount(
      <ComponentProviders store={store}>
        <DeleteProduct row={row} />
      </ComponentProviders>,
    );

    cy.get("svg").should("exist");
  });
});
