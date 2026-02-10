import { SidebarProviders } from "@/cypress/mocks/sidebar-providers";
import { AppSidebar } from "./app-sidebar";

describe("<AppSidebar />", () => {
  it("renders navigation blocks", () => {
    cy.viewport(1280, 720);

    cy.mount(
      <SidebarProviders>
        <AppSidebar
          user={{
            id: "1",
            email: "john@autoflex.com",
            firstName: "John",
            roles: ["ADMIN"],
            active: true,
            createdAt: "2026-01-01T00:00:00Z",
          }}
        />
      </SidebarProviders>,
    );

    cy.contains("Autoflex").should("exist");
    cy.contains("Production").should("exist");
    cy.contains("Inventory").should("exist");
    cy.contains("Raw Materials").should("exist");
    cy.contains("Products").should("exist");
  });
});
