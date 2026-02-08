"use client";

import { type Table } from "@tanstack/react-table";
import { X } from "lucide-react";

import { DataTableViewOptions } from "./data-table-view-options";
import { Button } from "./ui/button";
import { Input } from "./ui/input";

interface DataTableToolbarProps<TData> {
  table: Table<TData>;
  placeholder: string;
  column: string;
}

export function DataTableToolbar<TData>({
  table,
  placeholder,
  column,
}: DataTableToolbarProps<TData>) {
  const isFiltered = table.getState().columnFilters.length > 0;

  return (
    <div className="flex items-center justify-between">
      <div className="flex flex-1 items-center gap-2">
        <Input
          placeholder={placeholder}
          value={(table.getColumn(column)?.getFilterValue() as string) ?? ""}
          onChange={(event) =>
            table.getColumn(column)?.setFilterValue(event.target.value)
          }
          className="h-12 w-37.5 lg:w-150"
        />
        {isFiltered && (
          <Button
            variant="ghost"
            size="sm"
            onClick={() => table.resetColumnFilters()}
          >
            Reset
            <X />
          </Button>
        )}
      </div>
      <div className="flex items-center gap-2">
        <DataTableViewOptions table={table} />
      </div>
    </div>
  );
}
