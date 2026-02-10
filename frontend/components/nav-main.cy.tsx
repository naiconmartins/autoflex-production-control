import { Factory } from "lucide-react";
import { SidebarProviders } from "@/cypress/mocks/sidebar-providers";
import { NavMain } from "./nav-main";

describe("<NavMain />", () => {
  it("renders groups and sub-items", () => {
    cy.mount(
      <SidebarProviders>
        <NavMain
          items={[
            {
              title: "Production",
              url: "/",
              icon: Factory,
              isActive: true,
              items: [{ title: "Production Capacity", url: "/" }],
            },
          ]}
        />
      </SidebarProviders>,
    );

    cy.contains("Production").should("exist");
    cy.contains("Production Capacity").should("exist");
  });
});
