import { ComponentProviders } from "@/cypress/mocks/component-providers";
import { createTestStore } from "@/cypress/mocks/test-store";
import { setRawMaterials } from "@/store/slices/raw-material.slice";
import InsertProductForm from "./insert-form";

describe("<InsertProductForm />", () => {
  it("renders trigger button", () => {
    const store = createTestStore();

    cy.mount(
      <ComponentProviders store={store}>
        <InsertProductForm setLoading={() => {}} />
      </ComponentProviders>,
    );

    cy.contains("New Product").should("exist");
  });

  it("renders mapper error message on submit failure", () => {
    const store = createTestStore();
    store.dispatch(
      setRawMaterials({
        content: [{ id: 1, code: "RM-001", name: "Steel", stockQuantity: 50 }],
        page: 0,
        size: 10,
        totalElements: 1,
        totalPages: 1,
      }),
    );

    cy.stub(store, "dispatch").callsFake(async () => ({
      success: false,
      error: "This resource already exists.",
    }));

    cy.mount(
      <ComponentProviders store={store}>
        <InsertProductForm setLoading={() => {}} />
      </ComponentProviders>,
    );

    cy.contains("New Product").click();
    cy.get("input[name='code']").type("P-999");
    cy.get("input[name='name']").type("Test Product");
    cy.contains("button", "Add").first().click();
    cy.contains("button", "Save").click();

    cy.contains("This resource already exists.").should("exist");
  });
});
