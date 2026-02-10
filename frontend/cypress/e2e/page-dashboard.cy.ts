describe("Dashboard Page Integration", () => {
  it("renders charts and table with loaded data", () => {
    cy.visit("/login");

    cy.get('[data-cy="login-email"]').type("adm@autoflex.com");
    cy.get('[data-cy="login-password"]').type("adm");
    cy.get('[data-cy="login-submit"]').click();

    cy.location("pathname", { timeout: 15000 }).should("eq", "/");

    cy.contains("Production Capacity", { timeout: 15000 }).should("exist");
    cy.contains("Total Value by Product").should("exist");
    cy.contains("Value Distribution").should("exist");
    cy.contains("Product Breakdown").should("exist");

    cy.get(".recharts-wrapper").should("have.length.at.least", 2);
    cy.get("table tbody tr", { timeout: 15000 }).should(
      "have.length.at.least",
      1,
    );
  });
});
