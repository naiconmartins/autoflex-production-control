"use client";

import { type ColumnDef } from "@tanstack/react-table";

import { Checkbox } from "../ui/checkbox";

import { RawMaterial } from "@/interfaces/raw-material";
import { DataTableColumnHeader } from "../data-table-column-header";
import { Badge } from "../ui/badge";
import { DataTableRowActions } from "./data-table-row-actions";

export const columns: ColumnDef<RawMaterial>[] = [
  {
    id: "select",
    header: ({ table }) => (
      <Checkbox
        checked={
          table.getIsAllPageRowsSelected() ||
          (table.getIsSomePageRowsSelected() && "indeterminate")
        }
        onCheckedChange={(value) => table.toggleAllPageRowsSelected(!!value)}
        aria-label="Select all"
        className="translate-y-0.5"
      />
    ),
    cell: ({ row }) => (
      <Checkbox
        checked={row.getIsSelected()}
        onCheckedChange={(value) => row.toggleSelected(!!value)}
        aria-label="Select row"
        className="translate-y-0.5"
      />
    ),
    enableSorting: false,
    enableHiding: false,
  },
  {
    accessorKey: "id",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="ID" />
    ),
    cell: ({ row }) => <div className="w-20">{row.getValue("id")}</div>,
    enableSorting: false,
    enableHiding: false,
  },
  {
    accessorKey: "code",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="CODE" />
    ),
    cell: ({ row }) => {
      return (
        <div className="flex gap-2">
          <span className="max-w-20 truncate font-medium">
            {row.getValue("code")}
          </span>
        </div>
      );
    },
    enableSorting: false,
    enableHiding: true,
  },
  {
    accessorKey: "name",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="NAME" />
    ),
    cell: ({ row }) => {
      return (
        <div className="flex gap-2">
          <span className="max-w-125 truncate font-medium">
            {row.getValue("name")}
          </span>
        </div>
      );
    },
  },
  {
    accessorKey: "status",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="STATUS" />
    ),
    cell: ({ row }) => {
      return (
        <div className="flex gap-2">
          {row.original.stockQuantity > 0 ? (
            <Badge
              variant="ghost"
              className="bg-emerald-50 text-emerald-500 text-sm rounded-sm"
            >
              In stock
            </Badge>
          ) : (
            <Badge
              variant="ghost"
              className="bg-rose-50 text-rose-500 text-sm rounded-sm"
            >
              Unavailable
            </Badge>
          )}
        </div>
      );
    },
    enableSorting: false,
    enableHiding: true,
  },
  {
    accessorKey: "stockQuantity",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="STOCK" />
    ),
    cell: ({ row }) => {
      return (
        <div className="flex gap-2">
          <span className="max-w-125 truncate font-medium">
            {row.getValue("stockQuantity")}
          </span>
        </div>
      );
    },
  },
  {
    id: "actions",
    cell: ({ row }) => (
      <DataTableRowActions title="Edit Raw Material" row={row} />
    ),
  },
];
