import { ComponentProviders } from "@/cypress/mocks/component-providers";
import { createTestStore } from "@/cypress/mocks/test-store";
import UpdateRawMaterialForm from "./update-form";

describe("<UpdateRawMaterialForm />", () => {
  it("renders edit trigger icon", () => {
    const store = createTestStore();
    const row = {
      getValue: (key: string) => {
        if (key === "id") return "1";
        if (key === "code") return "RM-001";
        if (key === "name") return "Steel";
        if (key === "stockQuantity") return 100;
        return "";
      },
    } as any;

    cy.mount(
      <ComponentProviders store={store}>
        <UpdateRawMaterialForm row={row} />
      </ComponentProviders>,
    );

    cy.get("svg").should("exist");
  });

  it("renders mapper error message when update fails", () => {
    const store = createTestStore();
    cy.stub(store, "dispatch").callsFake(async () => ({
      success: false,
      error: "An unexpected server error occurred. Please try again later.",
    }));

    const row = {
      getValue: (key: string) => {
        if (key === "id") return "1";
        if (key === "code") return "RM-001";
        if (key === "name") return "Steel";
        if (key === "stockQuantity") return 100;
        return "";
      },
    } as any;

    cy.mount(
      <ComponentProviders store={store}>
        <UpdateRawMaterialForm row={row} />
      </ComponentProviders>,
    );

    cy.get("svg").first().click();
    cy.contains("button", "Save").click();
    cy.contains(
      "An unexpected server error occurred. Please try again later.",
    ).should("exist");
  });
});
