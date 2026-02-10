import { ComponentProviders } from "@/cypress/mocks/component-providers";
import { createTestStore } from "@/cypress/mocks/test-store";
import { setSession } from "@/store/slices/auth.slice";
import { setRawMaterials } from "@/store/slices/raw-material.slice";
import RawMaterialsPage from "./page";

function mountRawMaterialsPage(store = createTestStore()) {
  cy.mount(
    <ComponentProviders store={store}>
      <RawMaterialsPage />
    </ComponentProviders>,
  );
}

describe("<RawMaterialsPage />", () => {
  it("dispatches fetch on mount", () => {
    const store = createTestStore();
    const dispatchSpy = cy.spy(store, "dispatch").as("dispatchSpy");

    mountRawMaterialsPage(store);

    cy.get("@dispatchSpy").should("have.been.called");
  });

  it("renders loading state when user is not authenticated", () => {
    const store = createTestStore();

    mountRawMaterialsPage(store);

    cy.contains("Loading").should("exist");
  });

  it("renders raw materials page with table data when user is authenticated", () => {
    const store = createTestStore();

    store.dispatch(
      setSession({
        user: {
          id: "1",
          email: "john@autoflex.com",
          firstName: "John",
          roles: ["ADMIN"],
          active: true,
          createdAt: "2026-01-01T00:00:00Z",
        },
        token: "mock-token",
      }),
    );

    store.dispatch(
      setRawMaterials({
        content: [
          {
            id: 1,
            code: "RM-001",
            name: "Steel",
            stockQuantity: 90,
          },
        ],
        page: 0,
        size: 10,
        totalElements: 1,
        totalPages: 1,
      }),
    );

    mountRawMaterialsPage(store);

    cy.contains("Raw Materials").should("exist");
    cy.contains("Inventory").should("exist");
    cy.contains("New Raw Material").should("exist");
    cy.get('input[placeholder="Filter raw materials..."]').should("exist");
    cy.contains("Steel").should("exist");
  });

  it("renders empty state when user is authenticated and there are no raw materials", () => {
    const store = createTestStore();

    store.dispatch(
      setSession({
        user: {
          id: "1",
          email: "john@autoflex.com",
          firstName: "John",
          roles: ["ADMIN"],
          active: true,
          createdAt: "2026-01-01T00:00:00Z",
        },
        token: "mock-token",
      }),
    );

    store.dispatch(
      setRawMaterials({
        content: [],
        page: 0,
        size: 10,
        totalElements: 0,
        totalPages: 1,
      }),
    );

    mountRawMaterialsPage(store);

    cy.contains("Raw Materials").should("exist");
    cy.contains("Inventory").should("exist");
    cy.contains("No results.").should("exist");
    cy.contains("Page 1 of 1").should("exist");
  });

  it("dispatches pagination fetch when clicking next page", () => {
    const store = createTestStore();
    store.dispatch(
      setSession({
        user: {
          id: "1",
          email: "john@autoflex.com",
          firstName: "John",
          roles: ["ADMIN"],
          active: true,
          createdAt: "2026-01-01T00:00:00Z",
        },
        token: "mock-token",
      }),
    );
    store.dispatch(
      setRawMaterials({
        content: [
          {
            id: 1,
            code: "RM-001",
            name: "Steel",
            stockQuantity: 90,
          },
        ],
        page: 0,
        size: 10,
        totalElements: 30,
        totalPages: 3,
      }),
    );
    const dispatchSpy = cy.spy(store, "dispatch").as("dispatchSpy");

    mountRawMaterialsPage(store);

    cy.contains("span", "Go to next page").closest("button").click();
    cy.get("@dispatchSpy").should("have.been.called");
  });
});
