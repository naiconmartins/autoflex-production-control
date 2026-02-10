describe("Login Page Integration", () => {
  it("renders login page elements", () => {
    cy.visit("/login");

    cy.get('[data-cy="login-page"]').should("exist");
    cy.get('[data-cy="login-logo"]').should("contain.text", "Autoflex");
    cy.get('[data-cy="login-form"]').should("exist");
    cy.get('[data-cy="login-email"]').should("exist");
    cy.get('[data-cy="login-password"]').should("exist");
    cy.get('[data-cy="login-submit"]').should("exist");
  });

  it("keeps user on login when submitting empty form", () => {
    cy.visit("/login");

    cy.get('[data-cy="login-submit"]').click();
    cy.location("pathname").should("eq", "/login");
    cy.get('[data-cy="login-email"]').then(($input) => {
      expect(($input[0] as HTMLInputElement).checkValidity()).to.eq(false);
    });
  });

  it("validates invalid email format", () => {
    cy.visit("/login");

    cy.get('[data-cy="login-email"]').type("invalid-email");
    cy.get('[data-cy="login-password"]').type("123456");
    cy.get('[data-cy="login-submit"]').click();

    cy.location("pathname").should("eq", "/login");
    cy.get('[data-cy="login-email"]').then(($input) => {
      expect(($input[0] as HTMLInputElement).checkValidity()).to.eq(false);
    });
  });

  it("redirects authenticated user away from login", () => {
    cy.setCookie("token", "mock-token");
    cy.visit("/login");

    cy.location("pathname").should("eq", "/");
  });

  it("logs in with valid credentials and redirects to dashboard", () => {
    cy.visit("/login");

    cy.get('[data-cy="login-email"]').type("adm@autoflex.com");
    cy.get('[data-cy="login-password"]').type("adm");
    cy.get('[data-cy="login-submit"]').click();

    cy.location("pathname", { timeout: 15000 }).should("eq", "/");
    cy.getCookie("token").should("exist");
  });

});
