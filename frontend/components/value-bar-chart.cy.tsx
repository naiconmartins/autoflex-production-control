import ValueBarChart, { type ProductItem } from "./value-bar-chart";

describe("<ValueBarChart />", () => {
  const items: ProductItem[] = [
    { productName: "Hydraulic Pump", totalValue: 2000 },
    { productName: "Rubber Seal", totalValue: 1000 },
  ];

  it("renders chart title and subtitle", () => {
    cy.mount(<ValueBarChart items={items} grandTotalValue={3000} />);

    cy.contains("Total Value by Product").should("exist");
    cy.contains("Potential production revenue").should("exist");
  });

  it("renders bar chart and product labels", () => {
    cy.mount(<ValueBarChart items={items} grandTotalValue={3000} />);

    cy.get(".recharts-wrapper").should("exist");
    cy.get(".recharts-rectangle").should("have.length.at.least", 2);
    cy.contains("Hydraulic Pump").should("exist");
    cy.contains("Rubber Seal").should("exist");
  });
});
