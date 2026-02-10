import { Form } from "@/components/ui/form";
import { productSchema } from "@/schemas/product-schema";
import { useForm } from "react-hook-form";
import z from "zod";
import ProductInfo from "./product-info";

type ProductFormValues = z.infer<typeof productSchema>;

function TestWrapper() {
  const form = useForm<ProductFormValues>({
    defaultValues: {
      code: "P-001",
      name: "Hydraulic Pump",
      price: 20,
      rawMaterials: [],
    },
  });

  return (
    <Form {...form}>
      <ProductInfo form={form} isSubmitting={false} />
    </Form>
  );
}

describe("<ProductInfo />", () => {
  it("renders form fields", () => {
    cy.mount(<TestWrapper />);

    cy.contains("New Product").should("exist");
    cy.contains("Code").should("exist");
    cy.contains("Name").should("exist");
    cy.contains("Price").should("exist");
  });
});
