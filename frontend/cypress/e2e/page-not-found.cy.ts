describe("Not Found Page Integration", () => {
  it("renders not found page on invalid route", () => {
    cy.visit("/login");

    cy.get('[data-cy="login-email"]').type("adm@autoflex.com");
    cy.get('[data-cy="login-password"]').type("adm");
    cy.get('[data-cy="login-submit"]').click();

    cy.location("pathname", { timeout: 15000 }).should("eq", "/");

    cy.visit("/invalid-route-for-e2e-404", { failOnStatusCode: false });

    cy.contains("404", { timeout: 15000 }).should("exist");
    cy.contains("Page not found", { timeout: 15000 }).should("exist");
    cy.contains("Back to dashboard", { timeout: 15000 }).should("exist");
  });
});
