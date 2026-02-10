import { DollarSign } from "lucide-react";
import KPICard from "./KPICard";

describe("<KPICard />", () => {
  it("renders title, value and subtitle", () => {
    cy.mount(
      <KPICard
        title="Total Value"
        value="$10,000.00"
        subtitle="Potential revenue"
      />,
    );

    cy.contains("Total Value").should("exist");
    cy.contains("$10,000.00").should("exist");
    cy.contains("Potential revenue").should("exist");
  });

  it("renders icon when provided", () => {
    cy.mount(<KPICard title="Revenue" value="$1,200.00" icon={DollarSign} />);

    cy.get("svg").should("exist");
    cy.contains("Revenue").should("exist");
  });

  it("applies primary variant styling", () => {
    cy.mount(<KPICard title="Primary" value="$500.00" variant="primary" />);

    cy.contains("Primary").should("have.class", "text-white");
    cy.contains("$500.00").should("have.class", "text-white");
  });
});
