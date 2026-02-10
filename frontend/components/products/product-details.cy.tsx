import ProductDetails from "./product-details";

describe("<ProductDetails />", () => {
  it("renders details trigger", () => {
    const row = {
      original: {
        code: "P-001",
        name: "Hydraulic Pump",
        price: 100,
        rawMaterials: [],
      },
    } as any;

    cy.mount(<ProductDetails row={row} />);

    cy.get("svg").should("exist");
  });
});
