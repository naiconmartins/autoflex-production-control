import { SidebarProviders } from "@/cypress/mocks/sidebar-providers";
import { NavUser } from "./nav-user";

describe("<NavUser />", () => {
  it("renders user name and email", () => {
    cy.mount(
      <SidebarProviders>
        <NavUser
          user={{
            name: "Chris Nolan",
            email: "chris@autoflex.com",
            avatar: "/7294793.avif",
          }}
        />
      </SidebarProviders>,
    );

    cy.contains("Chris Nolan").should("exist");
    cy.contains("chris@autoflex.com").should("exist");
  });
});
