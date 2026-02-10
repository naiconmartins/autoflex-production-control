import { DataTableViewOptions } from "./data-table-view-options";

describe("<DataTableViewOptions />", () => {
  it("renders columns trigger", () => {
    const table = {
      getAllColumns: () => [
        {
          id: "name",
          accessorFn: () => "value",
          getCanHide: () => true,
          getIsVisible: () => true,
          toggleVisibility: () => {},
        },
      ],
    } as any;

    cy.mount(<DataTableViewOptions table={table} />);

    cy.contains("Columns").should("exist");
  });
});
