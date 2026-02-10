import SidebarComponent from "./sidebar-component";

describe("<SidebarComponent />", () => {
  it("renders breadcrumb and page content", () => {
    cy.mount(
      <SidebarComponent
        user={{
          id: "1",
          email: "john@autoflex.com",
          firstName: "John",
          roles: ["ADMIN"],
          active: true,
          createdAt: "2026-01-01T00:00:00Z",
        }}
        link="Inventory"
        page="Products"
      >
        <div data-cy="sidebar-child">Page Body</div>
      </SidebarComponent>,
    );

    cy.contains("Inventory").should("exist");
    cy.contains("Products").should("exist");
    cy.get('[data-cy="sidebar-child"]').should("exist");
  });
});
