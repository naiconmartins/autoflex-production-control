import { DataTablePagination } from "./data-table-pagination";

describe("<DataTablePagination />", () => {
  it("renders page indicators and pagination controls", () => {
    const onPagination = cy.stub().as("onPagination");
    const table = {
      getFilteredSelectedRowModel: () => ({ rows: [] }),
      getFilteredRowModel: () => ({ rows: [{}, {}] }),
      getState: () => ({ pagination: { pageSize: 10 } }),
      setPageSize: () => {},
    } as any;

    cy.mount(
      <DataTablePagination
        table={table}
        page={1}
        totalPages={3}
        onPagination={onPagination}
      />,
    );

    cy.contains("Page 2 of 3").should("exist");
    cy.contains("span", "Go to previous page").closest("button").click();
    cy.get("@onPagination").should("have.been.calledWith", 0);
    cy.contains("span", "Go to next page").closest("button").click();
    cy.get("@onPagination").should("have.been.calledWith", 2);
  });
});
