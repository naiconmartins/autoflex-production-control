import Loading from "./loading";

describe("<Loading />", () => {
  it("renders loading state", () => {
    cy.mount(<Loading />);

    cy.contains("Loading").should("exist");
    cy.get('[role="status"][aria-label="Loading"]').should("exist");
  });
});
