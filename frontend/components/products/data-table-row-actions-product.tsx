"use client";

import { Product } from "@/interfaces/product";
import { type Row } from "@tanstack/react-table";
import DeleteProduct from "./dialog-delete";
import ProductDetails from "./product-details";
import UpdateProductForm from "./update-form/update-form";

interface DataTableRowActionsProps {
  title: string;
  row: Row<Product>;
}

export function DataTableRowActionsProduct({ row }: DataTableRowActionsProps) {
  return (
    <div className="flex flex-row items-center gap-6">
      <ProductDetails row={row} />
      <UpdateProductForm row={row} />
      <DeleteProduct row={row} />
    </div>
  );
}
