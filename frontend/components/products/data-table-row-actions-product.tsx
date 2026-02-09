"use client";

import { Product } from "@/interfaces/product";
import { type Row } from "@tanstack/react-table";
import { Edit2 } from "lucide-react";
import DeleteProduct from "./dialog-delete";
import ProductDetails from "./product-details";

interface DataTableRowActionsProps {
  title: string;
  row: Row<Product>;
}

export function DataTableRowActionsProduct({ row }: DataTableRowActionsProps) {
  return (
    <div className="flex flex-row items-center gap-6">
      <ProductDetails row={row} />
      <Edit2 />
      <DeleteProduct row={row} />
    </div>
  );
}
