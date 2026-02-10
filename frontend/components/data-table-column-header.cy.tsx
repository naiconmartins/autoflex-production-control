import { DataTableColumnHeader } from "./data-table-column-header";

describe("<DataTableColumnHeader />", () => {
  it("renders plain title when sorting is disabled", () => {
    const column = {
      getCanSort: () => false,
    } as any;

    cy.mount(<DataTableColumnHeader column={column} title="Name" />);

    cy.contains("Name").should("exist");
  });

  it("renders sortable trigger when sorting is enabled", () => {
    const column = {
      getCanSort: () => true,
      getIsSorted: () => false,
      toggleSorting: () => {},
      toggleVisibility: () => {},
    } as any;

    cy.mount(<DataTableColumnHeader column={column} title="Price" />);

    cy.contains("Price").should("exist");
  });
});
