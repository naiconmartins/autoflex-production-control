import { GalleryVerticalEnd } from "lucide-react";
import { SidebarProviders } from "@/cypress/mocks/sidebar-providers";
import { TeamSwitcher } from "./team-switcher";

describe("<TeamSwitcher />", () => {
  it("renders active team info", () => {
    cy.mount(
      <SidebarProviders>
        <TeamSwitcher
          teams={[
            {
              name: "Autoflex",
              logo: GalleryVerticalEnd,
              plan: "Production Control",
            },
          ]}
        />
      </SidebarProviders>,
    );

    cy.contains("Autoflex").should("exist");
    cy.contains("Production Control").should("exist");
  });
});
