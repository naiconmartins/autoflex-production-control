describe("Raw Materials Page Integration", () => {
  let updatedRawMaterialName = "Raw Test Update";

  const loginAsAdmin = () => {
    cy.session("admin-user", () => {
      cy.visit("/login");

      cy.get('[data-cy="login-email"]').type("adm@autoflex.com");
      cy.get('[data-cy="login-password"]').type("adm");
      cy.get('[data-cy="login-submit"]').click();

      cy.location("pathname", { timeout: 15000 }).should("eq", "/");
    });
  };

  beforeEach(() => {
    loginAsAdmin();
    cy.visit("/raw-materials");
  });

  it("loads table and shows Pine Wood Board 2.5x30x200cm", () => {
    cy.get("table tbody tr", { timeout: 15000 }).should(
      "have.length.at.least",
      1,
    );

    cy.contains("Pine Wood Board 2.5x30x200cm", { timeout: 15000 }).should(
      "exist",
    );
  });

  it("filters raw materials by search input", () => {
    cy.get('input[placeholder="Filter raw materials..."]', {
      timeout: 15000,
    }).as("searchInput");

    cy.get("@searchInput").clear().type("Pine ");
    cy.contains("Pine Wood Board 2.5x30x200cm", { timeout: 15000 }).should(
      "exist",
    );

    cy.get("@searchInput").clear().type("NotFound-XYZ");
    cy.contains("No results.", { timeout: 15000 }).should("exist");

    cy.contains("button", "Reset").click();
    cy.contains("Pine Wood Board 2.5x30x200cm", { timeout: 15000 }).should(
      "exist",
    );
  });

  it("creates a new raw material", () => {
    const rawMaterialCode = `TES-${Date.now()}`;

    cy.contains("button", "New Raw Material", { timeout: 15000 }).click();

    cy.get("input#code").clear().type(rawMaterialCode);
    cy.get("input#name").clear().type("Raw Test");
    cy.get("input#stockQuantity").clear().type("10");

    cy.contains("button", "Save").click();

    cy.get('input[placeholder="Filter raw materials..."]', {
      timeout: 15000,
    })
      .clear()
      .type("Test");

    cy.contains("Raw Test", { timeout: 15000 }).should("exist");
  });

  it("shows validation errors when saving raw material with empty inputs", () => {
    cy.contains("button", "New Raw Material", { timeout: 15000 }).click();

    cy.get("input#code").clear();
    cy.get("input#name").clear();
    cy.get("input#stockQuantity").clear();

    cy.contains("button", "Save").click();

    cy.contains("Code is required", { timeout: 15000 }).should("exist");
    cy.contains("Name is required", { timeout: 15000 }).should("exist");
    cy.contains("Stock quantity must be at least 1", {
      timeout: 15000,
    }).should("exist");
  });

  it("shows conflict error when creating raw material with existing code MAD-001", () => {
    cy.contains("button", "New Raw Material", { timeout: 15000 }).click();

    cy.get("input#code").clear().type("MAD-001");
    cy.get("input#name").clear().type(`Raw Material Duplicate ${Date.now()}`);
    cy.get("input#stockQuantity").clear().type("10");

    cy.contains("button", "Save").click();

    cy.contains("This resource already exists", { timeout: 15000 }).should(
      "exist",
    );
  });

  it("updates raw material name from Raw Test to Raw Test Update", () => {
    updatedRawMaterialName = `Raw Test Update ${Date.now()}`;

    cy.get('input[placeholder="Filter raw materials..."]', {
      timeout: 15000,
    })
      .clear()
      .type("Raw Test");

    cy.contains("tr", "Raw Test", { timeout: 15000 }).within(() => {
      cy.get("#edit-dialog").click();
    });

    cy.get("#raw-material-edit-dialog", { timeout: 15000 }).should("exist");
    cy.get("#raw-material-edit-dialog input#name")
      .clear()
      .type(updatedRawMaterialName);
    cy.get("#raw-material-edit-dialog").contains("button", "Save").click();

    cy.contains("button", "Reset").click();
    cy.get('input[placeholder="Filter raw materials..."]', {
      timeout: 15000,
    })
      .clear()
      .type(updatedRawMaterialName);

    cy.contains(updatedRawMaterialName, { timeout: 15000 }).should("exist");
  });

  it("delete raw material", () => {
    cy.get('input[placeholder="Filter raw materials..."]', {
      timeout: 15000,
    })
      .clear()
      .type(updatedRawMaterialName);

    cy.contains("tr", updatedRawMaterialName, { timeout: 15000 }).within(() => {
      cy.get("#delete-raw-material").click();
    });

    cy.contains("button", "Continue", { timeout: 15000 }).click();

    cy.contains("button", "Reset").click();
    cy.get('input[placeholder="Filter raw materials..."]', {
      timeout: 15000,
    })
      .clear()
      .type(updatedRawMaterialName);

    cy.contains("No results.", { timeout: 15000 }).should("exist");
  });
});
