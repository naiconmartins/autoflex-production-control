import { DataTableToolbar } from "./data-table-toolbar";

describe("<DataTableToolbar />", () => {
  it("renders input and reset button when filtered", () => {
    const setFilterValue = cy.stub().as("setFilterValue");
    const resetColumnFilters = cy.stub().as("resetColumnFilters");

    const table = {
      getState: () => ({ columnFilters: [{ id: "name", value: "abc" }] }),
      getColumn: () => ({
        getFilterValue: () => "abc",
        setFilterValue,
      }),
      resetColumnFilters,
      getAllColumns: () => [],
    } as any;

    cy.mount(
      <DataTableToolbar table={table} placeholder="Filter products..." column="name" />,
    );

    cy.get('input[placeholder="Filter products..."]').should("have.value", "abc");
    cy.contains("Reset").click();
    cy.get("@resetColumnFilters").should("have.been.calledOnce");
  });
});
