import { ComponentProviders } from "@/cypress/mocks/component-providers";
import { createTestStore } from "@/cypress/mocks/test-store";
import InsertRawMaterilForm from "./insert-form";

describe("<InsertRawMaterilForm />", () => {
  it("renders trigger button", () => {
    const store = createTestStore();

    cy.mount(
      <ComponentProviders store={store}>
        <InsertRawMaterilForm setLoading={() => {}} />
      </ComponentProviders>,
    );

    cy.contains("New Raw Material").should("exist");
  });

  it("renders mapper error message on submit failure", () => {
    const store = createTestStore();
    cy.stub(store, "dispatch").callsFake(async () => ({
      success: false,
      error: "Some fields are invalid. Please review your input.",
    }));

    cy.mount(
      <ComponentProviders store={store}>
        <InsertRawMaterilForm setLoading={() => {}} />
      </ComponentProviders>,
    );

    cy.contains("New Raw Material").click();
    cy.get("#code").type("RM-100");
    cy.get("#name").type("Copper");
    cy.get("#stockQuantity").clear().type("10");
    cy.contains("button", "Save").click();

    cy.contains("Some fields are invalid. Please review your input.").should(
      "exist",
    );
  });
});
