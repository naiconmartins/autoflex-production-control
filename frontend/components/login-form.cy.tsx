import { LoginForm } from "@/components/login-form";
import { createAppRouterMock } from "@/cypress/mocks/app-router";
import { ComponentProviders } from "@/cypress/mocks/component-providers";
import authReducer from "@/lib/store/slices/auth.slice";
import { configureStore } from "@reduxjs/toolkit";

describe("<LoginForm />", () => {
  const router = createAppRouterMock();

  const createStore = (error: string | null = null) =>
    configureStore({
      reducer: {
        auth: authReducer,
      },
      preloadedState: {
        auth: {
          user: null,
          token: null,
          loading: false,
          error,
          hydrated: true,
        },
      },
    });

  const mountLoginForm = (error: string | null = null) => {
    const testStore = createStore(error);

    cy.mount(
      <ComponentProviders store={testStore} router={router}>
        <LoginForm />
      </ComponentProviders>,
    );
  };

  it("renders title and description", () => {
    mountLoginForm();
    cy.contains("h1", "Welcome back").should("exist");
    cy.contains("Enter your email and password to access your account").should(
      "exist",
    );
  });

  it("renders inputs and submit button", () => {
    mountLoginForm();
    cy.get('[data-cy="login-email"]')
      .should("have.attr", "type", "email")
      .and("have.attr", "required");
    cy.get('[data-cy="login-password"]')
      .should("have.attr", "type", "password")
      .and("have.attr", "required");
    cy.get('[data-cy="login-submit"]')
      .should("have.attr", "type", "submit")
      .and("contain.text", "Sign in");
  });

  it("renders store error message when available", () => {
    mountLoginForm("Invalid email or password.");
    cy.get('[data-cy="login-error"]')
      .should("exist")
      .and("contain.text", "Invalid email or password.");
  });

  it("renders mapper server error message from auth state", () => {
    mountLoginForm(
      "An unexpected server error occurred. Please try again later.",
    );
    cy.get('[data-cy="login-error"]')
      .should("exist")
      .and(
        "contain.text",
        "An unexpected server error occurred. Please try again later.",
      );
  });
});
