import { ComponentProviders } from "@/cypress/mocks/component-providers";
import { createTestStore } from "@/cypress/mocks/test-store";
import UpdateProductForm from "./update-form";

describe("<UpdateProductForm />", () => {
  it("renders edit trigger icon", () => {
    const store = createTestStore();

    const row = {
      getValue: () => "1",
      original: {
        code: "P-001",
        name: "Hydraulic Pump",
        price: 100,
        rawMaterials: [],
      },
    } as any;

    cy.mount(
      <ComponentProviders store={store}>
        <UpdateProductForm row={row} />
      </ComponentProviders>,
    );

    cy.get("svg").should("exist");
  });

  it("renders mapper error message when update fails", () => {
    const store = createTestStore();
    cy.stub(store, "dispatch").callsFake(async () => ({
      success: false,
      error: "The service is temporarily unavailable. Please try again later.",
    }));

    const row = {
      getValue: () => "1",
      original: {
        code: "P-001",
        name: "Hydraulic Pump",
        price: 100,
        rawMaterials: [{ id: 1, requiredQuantity: 2 }],
      },
    } as any;

    cy.mount(
      <ComponentProviders store={store}>
        <UpdateProductForm row={row} />
      </ComponentProviders>,
    );

    cy.get("svg").first().click();
    cy.contains("button", "Save").click();
    cy.contains(
      "The service is temporarily unavailable. Please try again later.",
    ).should("exist");
  });
});
