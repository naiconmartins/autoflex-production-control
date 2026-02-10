import { ComponentProviders } from "@/cypress/mocks/component-providers";
import { createTestStore } from "@/cypress/mocks/test-store";
import { setSession } from "@/store/slices/auth.slice";
import { setProducts } from "@/store/slices/product.slice";
import ProductPage from "./page";

function mountProductPage(store = createTestStore()) {
  cy.mount(
    <ComponentProviders store={store}>
      <ProductPage />
    </ComponentProviders>,
  );
}

describe("<ProductPage />", () => {
  it("dispatches fetch on mount", () => {
    const store = createTestStore();
    const dispatchSpy = cy.spy(store, "dispatch").as("dispatchSpy");

    mountProductPage(store);

    cy.get("@dispatchSpy").should("have.been.called");
  });

  it("renders loading state when user is not authenticated", () => {
    const store = createTestStore();

    mountProductPage(store);

    cy.contains("Loading").should("exist");
  });

  it("renders products page with table data when user is authenticated", () => {
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
      setProducts({
        content: [
          {
            id: 1,
            code: "P-001",
            name: "Hydraulic Pump",
            price: 120,
            rawMaterials: [],
          },
        ],
        page: 0,
        size: 10,
        totalElements: 1,
        totalPages: 1,
      }),
    );

    mountProductPage(store);

    cy.contains("Products").should("exist");
    cy.contains("Inventory").should("exist");
    cy.contains("New Product").should("exist");
    cy.get('input[placeholder="Filter products..."]').should("exist");
    cy.contains("Hydraulic Pump").should("exist");
  });

  it("renders empty state when user is authenticated and there are no products", () => {
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
      setProducts({
        content: [],
        page: 0,
        size: 10,
        totalElements: 0,
        totalPages: 1,
      }),
    );

    mountProductPage(store);

    cy.contains("Products").should("exist");
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
      setProducts({
        content: [
          {
            id: 1,
            code: "P-001",
            name: "Hydraulic Pump",
            price: 120,
            rawMaterials: [],
          },
        ],
        page: 0,
        size: 10,
        totalElements: 30,
        totalPages: 3,
      }),
    );
    const dispatchSpy = cy.spy(store, "dispatch").as("dispatchSpy");

    mountProductPage(store);

    cy.contains("span", "Go to next page").closest("button").click();
    cy.get("@dispatchSpy").should("have.been.called");
  });
});
