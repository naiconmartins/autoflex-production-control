import { Form } from "@/components/ui/form";
import { productSchema } from "@/schemas/product-schema";
import { useForm } from "react-hook-form";
import z from "zod";
import ProductInfoUpdate from "./product-info";

type ProductFormValues = z.infer<typeof productSchema>;

function TestWrapper() {
  const form = useForm<ProductFormValues>({
    defaultValues: {
      code: "P-002",
      name: "Rubber Seal",
      price: 15,
      rawMaterials: [],
    },
  });

  return (
    <Form {...form}>
      <ProductInfoUpdate form={form} isSubmitting={false} />
    </Form>
  );
}

describe("<ProductInfoUpdate />", () => {
  it("renders update form fields", () => {
    cy.mount(<TestWrapper />);

    cy.contains("Update Product").should("exist");
    cy.contains("Code").should("exist");
    cy.contains("Name").should("exist");
    cy.contains("Price").should("exist");
  });
});
