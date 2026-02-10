"use client";

import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import {
  FormControl,
  FormField,
  FormItem,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { RawMaterial } from "@/interfaces/raw-material";
import { productSchema } from "@/schemas/product-schema";
import { Trash2 } from "lucide-react";
import { Control, UseFormReturn } from "react-hook-form";
import z from "zod";

type ProductFormValues = z.infer<typeof productSchema>;

type Props = {
  form: UseFormReturn<ProductFormValues>;
  control: Control<ProductFormValues>;
  fields: ProductFormValues["rawMaterials"];
  findMaterial: (id: number) => RawMaterial | undefined;
  onRemove: (index: number) => void;
  disabled?: boolean;
};

export function SelectedMaterialsTable({
  form,
  control,
  fields,
  findMaterial,
  onRemove,
  disabled,
}: Props) {
  return (
    <Card className="p-4 border-none shadow-none">
      {form.formState.errors.rawMaterials?.message && (
        <p className="text-sm text-rose-600 mb-3 bg-rose-100 rounded-sm p-3">
          {form.formState.errors.rawMaterials.message}
        </p>
      )}
      <div className="space-y-3">
        <div>
          <h3 className="text-sm font-semibold">Selected Raw Materials</h3>
          <p className="text-xs text-muted-foreground">
            Materials required to produce this product
          </p>
        </div>

        <div className="rounded-md border">
          <div className="grid grid-cols-12 gap-2 border-b bg-muted/40 px-3 py-2 text-xs font-medium text-muted-foreground">
            <div className="col-span-7">NAME</div>
            <div className="col-span-4 text-right">QTY</div>
            <div className="col-span-1 text-right"></div>
          </div>

          <div>
            {fields && fields.length === 0 ? (
              <div className="px-3 py-3 text-sm text-muted-foreground">
                Select raw materials to associate with this product
              </div>
            ) : (
              fields?.map((field: any, index) => {
                const material = findMaterial(field.id);

                return (
                  <div
                    key={index}
                    className="grid grid-cols-12 items-center gap-2 px-3 py-2 border-b last:border-b-0"
                  >
                    <div className="col-span-7 text-sm truncate">
                      {material?.name || `Unknown ID ${field.id}`}
                    </div>
                    <div className="col-span-4">
                      <FormField
                        control={control}
                        name={`rawMaterials.${index}.requiredQuantity`}
                        render={({ field }) => (
                          <FormItem>
                            <FormControl>
                              <Input
                                type="number"
                                step="0.01"
                                min="0.01"
                                className="h-9 text-right"
                                {...field}
                                onChange={(e) =>
                                  field.onChange(Number(e.target.value))
                                }
                                disabled={disabled}
                              />
                            </FormControl>
                            <FormMessage className="text-xs" />
                          </FormItem>
                        )}
                      />
                    </div>
                    <div className="col-span-1 flex justify-end">
                      <Button
                        type="button"
                        variant="ghost"
                        size="icon"
                        onClick={() => onRemove(index)}
                        disabled={disabled}
                        className="hover:bg-rose-100"
                      >
                        <Trash2 className="h-4 w-4 text-rose-500" />
                      </Button>
                    </div>
                  </div>
                );
              })
            )}
          </div>
        </div>
      </div>
    </Card>
  );
}
