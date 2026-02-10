describe("Raw Materials Page Integration", () => {
  const filterInput = 'input[placeholder="Filter raw materials..."]';
  const pineName = "Pine Wood Board 2.5x30x200cm";
  const codeNumber = `${Date.now()}`;
  const createdCode = `E2E-RM-FIXED-${codeNumber}`;
  const createdName = `E2E Raw Material Fixed ${codeNumber}`;
  const createdStock = "25";
  const updatedStock = "30";

  const loginAndOpenRawMaterials = () => {
    cy.visit("/login");
    cy.get('[data-cy="login-email"]').type("adm@autoflex.com");
    cy.get('[data-cy="login-password"]').type("adm");
    cy.get('[data-cy="login-submit"]').click();

    cy.location("pathname", { timeout: 15000 }).should("eq", "/");
    cy.visit("/raw-materials");
    cy.location("pathname", { timeout: 15000 }).should("eq", "/raw-materials");
  };

  const searchByName = (value: string) => {
    cy.get(filterInput).clear().type(value);
  };

  const setNumberInputValue = (selector: string, value: string) => {
    cy.get(selector).then(($input) => {
      const input = $input[0] as HTMLInputElement;
      const nativeSetter = Object.getOwnPropertyDescriptor(
        HTMLInputElement.prototype,
        "value",
      )?.set;

      nativeSetter?.call(input, "");
      input.dispatchEvent(new Event("input", { bubbles: true }));
      nativeSetter?.call(input, value);
      input.dispatchEvent(new Event("input", { bubbles: true }));
      input.dispatchEvent(new Event("change", { bubbles: true }));
    });

    cy.get(selector).should("have.value", value);
  };

  const createRawMaterial = (code: string, name: string, stock: string) => {
    cy.contains("button", "New Raw Material").click();
    cy.contains("Create Raw Materials").should("exist");
    cy.get("#code").clear().type(code);
    cy.get("#name").clear().type(name);
    setNumberInputValue("#stockQuantity", stock);
    cy.contains("button", "Save").click();
    cy.contains("Create Raw Materials").should("not.exist");
  };

  const openEditDialogByCode = (code: string) => {
    cy.contains("tr", code, { timeout: 15000 }).within(() => {
      cy.get("svg.cursor-pointer:not(.text-rose-500)").first().click({ force: true });
    });

    cy.contains("Edit Raw Material", { timeout: 15000 }).should("exist");
  };

  const deleteRawMaterialByCode = (code: string) => {
    cy.contains("tr", code, { timeout: 15000 }).within(() => {
      cy.get("svg.cursor-pointer.text-rose-500").first().click({ force: true });
    });

    cy.contains("Are you absolutely sure?").should("exist");
    cy.contains("button", "Continue").click();
    cy.contains("Are you absolutely sure?").should("not.exist");
  };

  beforeEach(() => {
    cy.viewport(1280, 720);
    loginAndOpenRawMaterials();
  });

  it("loads the page and shows the table with Pine Wood Board", () => {
    cy.contains("Raw Materials", { timeout: 15000 }).should("exist");
    cy.get("table").should("exist");
    cy.contains("CODE").should("exist");
    cy.contains("NAME").should("exist");
    cy.contains("STOCK").should("exist");
    cy.contains("tr", pineName, { timeout: 15000 }).should("exist");
  });

  it("tests the search input with Pine and zzzz", () => {
    searchByName("Pine");
    cy.contains("tr", pineName, { timeout: 15000 }).should("exist");

    searchByName("zzzz");
    cy.contains("No results.", { timeout: 15000 }).should("exist");
  });

  it("creates a new raw material and verifies it appears in search", () => {
    searchByName(createdName);

    cy.get("body").then(($body) => {
      if (!$body.text().includes(createdCode)) {
        createRawMaterial(createdCode, createdName, createdStock);
      }
    });

    searchByName(createdName);
    cy.contains("tr", createdCode, { timeout: 15000 }).should("exist");
  });

  it("updates stock quantity of the created raw material and validates the value", () => {
    searchByName(createdName);

    cy.get("body").then(($body) => {
      if (!$body.text().includes(createdCode)) {
        createRawMaterial(createdCode, createdName, createdStock);
        searchByName(createdName);
      }
    });

    openEditDialogByCode(createdCode);

    cy.get('[role="dialog"]').within(() => {
      setNumberInputValue('input[type="number"]', updatedStock);

      cy.contains("button", "Save").click();
    });

    cy.contains("Edit Raw Material").should("not.exist");

    searchByName(createdName);

    cy.contains("tr", createdCode, { timeout: 15000 })
      .find("td")
      .eq(5)
      .should("contain.text", updatedStock);
  });

  it("deletes the created raw material", () => {
    searchByName(createdName);

    cy.get("body").then(($body) => {
      if (!$body.text().includes(createdCode)) {
        createRawMaterial(createdCode, createdName, updatedStock);
        searchByName(createdName);
      }
    });

    deleteRawMaterialByCode(createdCode);

    searchByName(createdName);
    cy.contains("No results.", { timeout: 15000 }).should("exist");
  });
});
