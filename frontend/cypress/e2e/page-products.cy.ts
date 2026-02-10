describe("Products Page Integration", () => {
  const waitForUiUnlock = () => {
    cy.get("body", { timeout: 15000 }).should(($body) => {
      expect(getComputedStyle($body[0]).pointerEvents).not.to.eq("none");
    });
  };

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
    cy.visit("/products");
  });

  it("loads table and shows Dining Table Oak 1.80m", () => {
    cy.get("table tbody tr", { timeout: 15000 }).should(
      "have.length.at.least",
      1,
    );

    cy.contains("Dining Table Oak 1.80m", { timeout: 15000 }).should("exist");
  });

  it("filters products by search input", () => {
    cy.get('input[placeholder="Filter products..."]', {
      timeout: 15000,
    }).as("searchInput");

    cy.get("@searchInput").clear().type("Dining");
    cy.contains("Dining Table Oak 1.80m", { timeout: 15000 }).should("exist");

    cy.contains("button", "Reset").click();

    cy.get("@searchInput").clear().type("Non existing");
    cy.contains("No results.", { timeout: 15000 }).should("exist");

    cy.contains("button", "Reset").click();
  });

  it("shows validation error when saving product without raw materials", () => {
    const productCode = `CYP-${Date.now()}`;

    cy.contains("button", "New Product", { timeout: 15000 }).click();
    cy.contains("New Product", { timeout: 15000 }).should("exist");

    cy.get('input[name="code"]').clear().type(productCode);
    cy.get('input[name="name"]').clear().type("Product without materials");
    cy.get('input[name="price"]').clear().type("10.00");

    cy.contains("button", "Save").click();

    cy.contains("Select at least one raw material", { timeout: 15000 }).should(
      "exist",
    );
  });

  it("shows validation errors when saving product without code and name", () => {
    cy.contains("button", "New Product", { timeout: 15000 }).click();
    cy.contains("New Product", { timeout: 15000 }).should("exist");

    cy.get('input[name="code"]').clear();
    cy.get('input[name="name"]').clear();
    cy.get('input[name="price"]').clear().type("10.00");

    cy.contains("button", "Save").click();

    cy.contains("Product code is required", { timeout: 15000 }).should("exist");
    cy.contains("Product name is required", { timeout: 15000 }).should("exist");
  });

  it("shows conflict error when creating product with existing code", () => {
    const productName = `Product duplicate ${Date.now()}`;
    const addRawMaterial = (materialName: string) => {
      cy.get("@availableRawMaterialsCard")
        .find('input[placeholder="Search by id, code or name..."]')
        .clear()
        .type(materialName);

      cy.get("@availableRawMaterialsCard")
        .contains("div", materialName, { timeout: 15000 })
        .should("be.visible")
        .closest("div.grid")
        .within(() => {
          cy.contains("button", "Add").click();
        });
    };

    cy.contains("button", "New Product", { timeout: 15000 }).click();
    cy.contains("New Product", { timeout: 15000 }).should("exist");

    cy.get('input[name="code"]').clear().type("PROD-001");
    cy.get('input[name="name"]').clear().type(productName);
    cy.get('input[name="price"]').clear().type("10.00");

    cy.contains("h3", "Available Raw Materials")
      .closest("div.space-y-3")
      .as("availableRawMaterialsCard");

    addRawMaterial("Pine");
    addRawMaterial("Oak");

    cy.contains("button", "Save").click();

    cy.contains("This resource already exists", { timeout: 15000 }).should(
      "exist",
    );
  });

  it("shows conflict error when creating product with existing code MAD-001", () => {
    const productName = `Product duplicate MAD ${Date.now()}`;
    const addRawMaterial = (materialName: string) => {
      cy.get("@availableRawMaterialsCard")
        .find('input[placeholder="Search by id, code or name..."]')
        .clear()
        .type(materialName);

      cy.get("@availableRawMaterialsCard")
        .contains("div", materialName, { timeout: 15000 })
        .should("be.visible")
        .closest("div.grid")
        .within(() => {
          cy.contains("button", "Add").click();
        });
    };

    cy.contains("button", "New Product", { timeout: 15000 }).click();
    cy.contains("New Product", { timeout: 15000 }).should("exist");

    cy.get('input[name="code"]').clear().type("MAD-001");
    cy.get('input[name="name"]').clear().type(productName);
    cy.get('input[name="price"]').clear().type("10.00");

    cy.contains("h3", "Available Raw Materials")
      .closest("div.space-y-3")
      .as("availableRawMaterialsCard");

    addRawMaterial("Pine Wood Board 2.5x30x200cm");
    addRawMaterial("Oak Wood Board 2.5x30x200cm");

    cy.contains("button", "Save").click();

    cy.contains("This resource already exists", { timeout: 15000 }).should(
      "exist",
    );
  });

  it("creates a new product and validates it in search", () => {
    const productCode = `CYP-${Date.now()}`;
    const productName = "Product Test";
    const addRawMaterial = (materialName: string) => {
      cy.get("@availableRawMaterialsCard")
        .find('input[placeholder="Search by id, code or name..."]')
        .clear()
        .type(materialName);

      cy.get("@availableRawMaterialsCard")
        .contains("div", materialName, { timeout: 15000 })
        .should("be.visible")
        .closest("div.grid")
        .within(() => {
          cy.contains("button", "Add").click();
        });
    };

    cy.contains("button", "New Product", { timeout: 15000 }).click();
    cy.contains("New Product", { timeout: 15000 }).should("exist");

    cy.get('input[name="code"]').clear().type(productCode);
    cy.get('input[name="name"]').clear().type(productName);
    cy.get('input[name="price"]').clear().type("10.00");

    cy.contains("h3", "Available Raw Materials")
      .closest("div.space-y-3")
      .as("availableRawMaterialsCard");

    addRawMaterial("Pine");
    addRawMaterial("Oak");

    cy.contains("button", "Save").click();

    cy.get('input[placeholder="Filter products..."]', {
      timeout: 15000,
    })
      .clear()
      .type(productName);

    cy.contains(productName, { timeout: 15000 }).should("exist");
  });

  it("updates product name", () => {
    const updatedProductName = `Product Test Update ${Date.now()}`;

    cy.get('input[placeholder="Filter products..."]', {
      timeout: 15000,
    })
      .clear()
      .type("Product Test");

    cy.contains("tr", "Product Test", { timeout: 15000 }).within(() => {
      cy.get("#update-product").click();
    });

    cy.contains("Update Product", { timeout: 15000 }).should("exist");

    cy.get('input[name="name"]').clear().type(updatedProductName);

    cy.contains("button", "Save").click();

    waitForUiUnlock();
    cy.get('input[placeholder="Filter products..."]', {
      timeout: 15000,
    })
      .clear()
      .type(updatedProductName);
  });

  it("deletes the updated product", () => {
    cy.get('input[placeholder="Filter products..."]', {
      timeout: 15000,
    })
      .clear()
      .type("Product Test Update");

    cy.get("table tbody tr", { timeout: 15000 })
      .first()
      .within(() => {
        cy.get("#delete-product").click();
      });

    cy.contains("button", "Continue", { timeout: 15000 }).click();

    waitForUiUnlock();
    cy.contains("button", "Reset").click();
    cy.get('input[placeholder="Filter products..."]', {
      timeout: 15000,
    })
      .clear()
      .type("Product Test Update");

    cy.contains("No results.", { timeout: 15000 }).should("exist");
  });
});
