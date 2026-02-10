import { store } from "@/store/store";
import { ComponentProviders } from "@/cypress/mocks/component-providers";
import LoginPage from "./page";

describe("<LoginPage />", () => {
  const mountLoginPage = () => {
    cy.mount(
      <ComponentProviders store={store}>
        <LoginPage />
      </ComponentProviders>,
    );
  };

  it("renders the login page layout", () => {
    mountLoginPage();
    cy.get('[data-cy="login-page"]').should("exist");
    cy.get('[data-cy="login-content"]').should("exist");
    cy.get('[data-cy="login-form-container"]').should("exist");
    cy.get('[data-cy="login-image-wrapper"]').should("exist");
    cy.get('[data-cy="login-image"]').should("exist");
  });

  it("renders the brand section", () => {
    mountLoginPage();
    cy.get('[data-cy="login-logo"]').should("exist").and("contain.text", "Autoflex");
    cy.get('[data-cy="login-logo-icon"]').should("exist");
  });

  it("renders the form elements from login form component", () => {
    mountLoginPage();
    cy.get('[data-cy="login-form"]').should("exist");
    cy.get('[data-cy="login-email"]').should("exist");
    cy.get('[data-cy="login-password"]').should("exist");
    cy.get('[data-cy="login-submit"]').should("exist");
  });
});
