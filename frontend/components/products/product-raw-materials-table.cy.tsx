import { ProductRawMaterialsTable } from "./product-raw-materials-table";

describe("<ProductRawMaterialsTable />", () => {
  it("renders materials and handles add", () => {
    const onAdd = cy.stub().as("onAdd");

    cy.mount(
      <ProductRawMaterialsTable
        materials={[
          { id: 1, code: "RM-001", name: "Steel", stockQuantity: 100 },
          { id: 2, code: "RM-002", name: "Rubber", stockQuantity: 80 },
        ]}
        selectedIds={[]}
        onAdd={onAdd}
        pagination={{ page: 0, size: 10, totalElements: 2, totalPages: 1 }}
        onPageChange={() => {}}
      />,
    );

    cy.contains("Available Raw Materials").should("exist");
    cy.contains("Steel").should("exist");
    cy.contains("Rubber").should("exist");
    cy.contains("button", "Add").first().click();
    cy.get("@onAdd").should("have.been.called");
  });
});
