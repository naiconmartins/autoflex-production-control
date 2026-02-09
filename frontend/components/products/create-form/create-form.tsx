"use client";

import { Button } from "@/components/ui/button";
import { DialogFooter } from "@/components/ui/dialog";
import {
  Drawer,
  DrawerContent,
  DrawerTitle,
  DrawerTrigger,
} from "@/components/ui/drawer";
import { Form } from "@/components/ui/form";
import { productSchema } from "@/schemas/product-schema";
import { useAppDispatch, useAppSelector } from "@/store/hooks";
import { createProduct, fetchProducts } from "@/store/thunks/product.thunks";
import { fetchRawMaterials } from "@/store/thunks/raw-material.thunks";
import { zodResolver } from "@hookform/resolvers/zod";
import { Plus } from "lucide-react";
import { useEffect, useState } from "react";
import { useFieldArray, useForm } from "react-hook-form";
import { z } from "zod";
import ProductInfo from "./product-info";
import { ProductRawMaterialsTable } from "./product-raw-materials-table";
import { SelectedMaterialsTable } from "./selected-materials-table";

export type ProductFormValues = z.infer<typeof productSchema>;

export default function CreateProductForm({
  setLoading,
}: {
  setLoading: (value: boolean) => void;
}) {
  const dispatch = useAppDispatch();
  const [open, setOpen] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [selectedMaterials, setSelectedMaterials] = useState<Map<number, any>>(
    new Map(),
  );

  const { items: rawMaterials, pagination } = useAppSelector(
    (state) => state.rawMaterial,
  );

  const form = useForm<ProductFormValues>({
    resolver: zodResolver(productSchema),
    defaultValues: {
      code: "",
      name: "",
      price: 0.01,
      rawMaterials: [],
    },
  });

  const { append, remove } = useFieldArray({
    control: form.control,
    name: "rawMaterials",
  });

  useEffect(() => {
    if (open) dispatch(fetchRawMaterials());
  }, [open, dispatch]);

  const watchedFields = form.watch("rawMaterials");
  const selectedIds = watchedFields.map((f: any) => f.id);

  const handlePageChange = (page: number) => {
    dispatch(fetchRawMaterials({ page }));
  };

  const findMaterial = (id: number) => {
    const fromLocal = selectedMaterials.get(id);
    if (fromLocal) return fromLocal;

    return rawMaterials?.find((rm) => rm.id === id);
  };

  const handleRemove = (index: number) => {
    const materialId = watchedFields[index]?.id;
    if (materialId) {
      setSelectedMaterials((prev) => {
        const newMap = new Map(prev);
        newMap.delete(materialId);
        return newMap;
      });
    }
    remove(index);
  };

  const addMaterial = (id: number) => {
    if (selectedIds.includes(id)) return;

    const material = rawMaterials?.find((rm) => rm.id === id);
    if (material) {
      setSelectedMaterials((prev) => new Map(prev).set(id, material));
    }

    append({ id, requiredQuantity: 0.01 });
    form.clearErrors("rawMaterials");
  };

  const onSubmit = async (values: ProductFormValues) => {
    setIsSubmitting(true);
    try {
      const result = await dispatch(createProduct(values));
      if (result.success) {
        setLoading(true);
        await dispatch(fetchProducts());
        form.reset();
        setSelectedMaterials(new Map());
        setOpen(false);
      } else {
        if (result.fieldErrors) {
          Object.entries(result.fieldErrors).forEach(([field, messages]) => {
            form.setError(field as any, {
              type: "manual",
              message: (messages as string[])[0],
            });
          });
        }
        form.setError("root", { type: "manual", message: result.error });
      }
    } catch (error: any) {
      form.setError("root", {
        type: "manual",
        message: error.message || "An unexpected error occurred",
      });
    } finally {
      setIsSubmitting(false);
      setLoading(false);
    }
  };

  return (
    <Drawer open={open} onOpenChange={setOpen} direction="bottom">
      <DrawerTrigger asChild>
        <Button variant="default">
          <Plus className="h-4 w-4" />
          New Product
        </Button>
      </DrawerTrigger>

      <DrawerContent className="data-[vaul-drawer-direction=bottom]:sm:h-screen data-[vaul-drawer-direction=bottom]:sm:max-h-none">
        <DrawerTitle />
        <div className="mx-auto grid w-full grid-cols-1 gap-6 px-6 py-4 sm:grid-cols-2">
          <div className="space-y-4">
            <Form {...form}>
              <form
                onSubmit={form.handleSubmit(onSubmit)}
                className="space-y-4"
              >
                <ProductInfo form={form} isSubmitting={isSubmitting} />

                <SelectedMaterialsTable
                  control={form.control}
                  fields={watchedFields}
                  findMaterial={findMaterial}
                  onRemove={handleRemove}
                  disabled={isSubmitting}
                  form={form}
                />

                <DialogFooter>
                  <Button
                    type="button"
                    variant="outline"
                    onClick={() => setOpen(false)}
                    disabled={isSubmitting}
                  >
                    Cancel
                  </Button>
                  <Button type="submit" disabled={isSubmitting}>
                    {isSubmitting ? "Saving..." : "Save"}
                  </Button>
                </DialogFooter>
              </form>
            </Form>
          </div>

          <ProductRawMaterialsTable
            materials={rawMaterials || []}
            selectedIds={selectedIds}
            onAdd={addMaterial}
            disabled={isSubmitting}
            pagination={pagination}
            onPageChange={handlePageChange}
          />
        </div>
      </DrawerContent>
    </Drawer>
  );
}
