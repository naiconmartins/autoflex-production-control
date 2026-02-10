import type { ColumnDef } from "@tanstack/react-table";
import { DataTable } from "./data-table";

type RowData = {
  id: number;
  name: string;
};

describe("<DataTable />", () => {
  const columns: ColumnDef<RowData>[] = [
    {
      accessorKey: "id",
      header: "ID",
      cell: ({ row }) => row.original.id,
    },
    {
      accessorKey: "name",
      header: "Name",
      cell: ({ row }) => row.original.name,
    },
  ];

  const data: RowData[] = [
    { id: 1, name: "Hydraulic Pump" },
    { id: 2, name: "Rubber Seal" },
  ];

  it("renders rows", () => {
    cy.mount(
      <DataTable
        columns={columns}
        data={data}
        loading={false}
        page={0}
        totalPages={2}
        placeholder="Filter..."
        column="name"
        onPagination={() => {}}
      />,
    );

    cy.contains("Hydraulic Pump").should("exist");
    cy.contains("Rubber Seal").should("exist");
  });

  it("renders loading state", () => {
    cy.mount(
      <DataTable
        columns={columns}
        data={[]}
        loading
        page={0}
        totalPages={1}
        placeholder="Filter..."
        column="name"
        onPagination={() => {}}
      />,
    );

    cy.contains("Loading").should("exist");
  });

  it("renders empty state when there is no data", () => {
    cy.mount(
      <DataTable
        columns={columns}
        data={[]}
        loading={false}
        page={0}
        totalPages={1}
        placeholder="Filter..."
        column="name"
        onPagination={() => {}}
      />,
    );

    cy.contains("No results.").should("exist");
  });
});
