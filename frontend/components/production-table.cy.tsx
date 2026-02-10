import ProductionTable, { type ProductItem } from "./production-table";

describe("<ProductionTable />", () => {
  const items: ProductItem[] = [
    {
      productId: 1,
      productCode: "P-001",
      productName: "Hydraulic Pump",
      unitPrice: 125,
      producibleQuantity: 20,
      totalValue: 2500,
    },
    {
      productId: 2,
      productCode: "P-002",
      productName: "Rubber Seal",
      unitPrice: 50,
      producibleQuantity: 10,
      totalValue: 500,
    },
  ];

  it("renders table headers and rows", () => {
    cy.mount(<ProductionTable items={items} />);

    cy.contains("Product Breakdown").should("exist");
    cy.contains("Production capacity and estimated values").should("exist");
    cy.contains("Code").should("exist");
    cy.contains("Product").should("exist");
    cy.contains("Unit Price").should("exist");
    cy.contains("Producible Qty").should("exist");
    cy.contains("Total Value").should("exist");
    cy.contains("P-001").should("exist");
    cy.contains("Hydraulic Pump").should("exist");
    cy.contains("P-002").should("exist");
    cy.contains("Rubber Seal").should("exist");
  });

  it("formats currency values", () => {
    cy.mount(<ProductionTable items={items} />);

    cy.contains("$125.00").should("exist");
    cy.contains("$2,500.00").should("exist");
    cy.contains("$50.00").should("exist");
    cy.contains("$500.00").should("exist");
  });
});
