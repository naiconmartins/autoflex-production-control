import { ComponentProviders } from "@/cypress/mocks/component-providers";
import { createTestStore } from "@/cypress/mocks/test-store";
import { DataTableRowActionsProduct } from "./data-table-row-actions-product";

describe("<DataTableRowActionsProduct />", () => {
  it("renders row actions icons", () => {
    const store = createTestStore();
    const row = {
      getValue: () => "1",
      original: {
        id: 1,
        code: "P-001",
        name: "Hydraulic Pump",
        price: 100,
        rawMaterials: [],
      },
    } as any;

    cy.mount(
      <ComponentProviders store={store}>
        <DataTableRowActionsProduct row={row} title="Actions" />
      </ComponentProviders>,
    );

    cy.get("svg").should("have.length.at.least", 3);
  });
});
