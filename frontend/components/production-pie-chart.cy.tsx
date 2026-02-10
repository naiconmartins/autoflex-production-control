import ProductionPieChart, { type ProductItem } from "./production-pie-chart";

describe("<ProductionPieChart />", () => {
  const items: ProductItem[] = [
    { productName: "Hydraulic Pump", totalValue: 2000 },
    { productName: "Rubber Seal", totalValue: 1000 },
  ];

  it("renders chart title and legend labels", () => {
    cy.mount(<ProductionPieChart items={items} grandTotalValue={3000} />);

    cy.contains("Value Distribution").should("exist");
    cy.contains("Share by product").should("exist");
    cy.contains("Hydraulic Pump").should("exist");
    cy.contains("Rubber Seal").should("exist");
  });

  it("renders pie chart structure", () => {
    cy.mount(<ProductionPieChart items={items} grandTotalValue={3000} />);

    cy.get(".recharts-wrapper").should("exist");
    cy.get(".recharts-sector").should("have.length.at.least", 2);
  });
});
