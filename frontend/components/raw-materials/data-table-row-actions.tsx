"use client";

import { type Row } from "@tanstack/react-table";
import DeleteRawMaterial from "./dialog-delete";
import UpdateRawMaterialForm from "./update-form";

interface DataTableRowActionsProps<TData> {
  title: string;
  row: Row<TData>;
}

export function DataTableRowActions<TData>({
  row,
}: DataTableRowActionsProps<TData>) {
  return (
    <div className="flex flex-row items-center gap-6">
      <UpdateRawMaterialForm row={row} />
      <DeleteRawMaterial row={row} />
    </div>
  );
}
