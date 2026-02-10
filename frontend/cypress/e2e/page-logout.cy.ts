describe("Sidebar Logout Integration", () => {
  it("logs out from sidebar user menu", () => {
    cy.on("uncaught:exception", (err) => {
      if (err.message.includes("NEXT_REDIRECT")) {
        return false;
      }
    });

    cy.visit("/login");

    cy.get('[data-cy="login-email"]').type("adm@autoflex.com");
    cy.get('[data-cy="login-password"]').type("adm");
    cy.get('[data-cy="login-submit"]').click();

    cy.location("pathname", { timeout: 15000 }).should("eq", "/");

    cy.get('[data-sidebar="footer"] [data-sidebar="menu-button"]').click();
    cy.contains("Log out", { timeout: 15000 }).click();

    cy.location("pathname", { timeout: 15000 }).should("eq", "/login");
    cy.get('[data-cy="login-form"]').should("exist");
  });
});
