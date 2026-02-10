import { ComponentProviders } from "@/cypress/mocks/component-providers";
import { createTestStore } from "@/cypress/mocks/test-store";
import { DataTableRowActions } from "./data-table-row-actions";

describe("<DataTableRowActions />", () => {
  it("renders action icons", () => {
    const store = createTestStore();
    const row = {
      getValue: () => "1",
    } as any;

    cy.mount(
      <ComponentProviders store={store}>
        <DataTableRowActions row={row} title="Actions" />
      </ComponentProviders>,
    );

    cy.get("svg").should("have.length.at.least", 2);
  });
});
