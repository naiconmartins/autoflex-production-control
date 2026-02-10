import { Form } from "@/components/ui/form";
import { productSchema } from "@/schemas/product-schema";
import { useEffect } from "react";
import { useForm } from "react-hook-form";
import z from "zod";
import { SelectedMaterialsTable } from "./selected-materials-table";

type ProductFormValues = z.infer<typeof productSchema>;

function TestWrapper() {
  const form = useForm<ProductFormValues>({
    defaultValues: {
      code: "P-001",
      name: "Hydraulic Pump",
      price: 10,
      rawMaterials: [{ id: 1, requiredQuantity: 2 }],
    },
  });

  return (
    <Form {...form}>
      <SelectedMaterialsTable
        form={form}
        control={form.control}
        fields={form.watch("rawMaterials")}
        findMaterial={() => ({ id: 1, code: "RM-001", name: "Steel", stockQuantity: 100 })}
        onRemove={() => {}}
      />
    </Form>
  );
}

describe("<SelectedMaterialsTable />", () => {
  it("renders selected materials table", () => {
    cy.mount(<TestWrapper />);

    cy.contains("Selected Raw Materials").should("exist");
    cy.contains("Steel").should("exist");
  });

  it("renders form error message when raw materials has an error", () => {
    function ErrorWrapper() {
      const form = useForm<ProductFormValues>({
        defaultValues: {
          code: "P-001",
          name: "Hydraulic Pump",
          price: 10,
          rawMaterials: [{ id: 1, requiredQuantity: 2 }],
        },
      });

      useEffect(() => {
        form.setError("rawMaterials", {
          type: "manual",
          message: "Some fields are invalid. Please review your input.",
        });
      }, [form]);

      return (
        <Form {...form}>
          <SelectedMaterialsTable
            form={form}
            control={form.control}
            fields={form.watch("rawMaterials")}
            findMaterial={() => ({
              id: 1,
              code: "RM-001",
              name: "Steel",
              stockQuantity: 100,
            })}
            onRemove={() => {}}
          />
        </Form>
      );
    }

    cy.mount(<ErrorWrapper />);

    cy.contains("Some fields are invalid. Please review your input.").should(
      "exist",
    );
  });
});
