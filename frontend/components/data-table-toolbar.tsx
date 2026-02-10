"use client";

import { type Table } from "@tanstack/react-table";
import { X } from "lucide-react";
import * as React from "react";

import { DataTableViewOptions } from "./data-table-view-options";
import { Button } from "./ui/button";
import { Input } from "./ui/input";

interface DataTableToolbarProps<TData> {
  table: Table<TData>;
  placeholder: string;
  column: string;
  onSearch?: (value: string) => void;
  onResetSearch?: () => void;
}

export function DataTableToolbar<TData>({
  table,
  placeholder,
  column,
  onSearch,
  onResetSearch,
}: DataTableToolbarProps<TData>) {
  const [searchValue, setSearchValue] = React.useState("");
  const isServerSearch = Boolean(onSearch);
  const isFiltered = isServerSearch
    ? searchValue.trim().length > 0
    : table.getState().columnFilters.length > 0;

  const handleInputChange = (value: string) => {
    if (isServerSearch) {
      setSearchValue(value);
    } else {
      table.getColumn(column)?.setFilterValue(value);
    }

    const trimmed = value.trim();

    if (trimmed.length > 0) {
      onSearch?.(trimmed);
    }

    if (trimmed.length === 0) {
      onResetSearch?.();
    }
  };

  const handleReset = () => {
    if (isServerSearch) {
      setSearchValue("");
    } else {
      table.resetColumnFilters();
    }

    onResetSearch?.();
  };

  return (
    <div className="flex items-center justify-between">
      <div className="flex flex-1 items-center gap-2">
        <Input
          placeholder={placeholder}
          value={
            isServerSearch
              ? searchValue
              : ((table.getColumn(column)?.getFilterValue() as string) ?? "")
          }
          onChange={(event) => handleInputChange(event.target.value)}
          className="h-12 w-37.5 lg:w-150"
        />
        {isFiltered && (
          <Button variant="ghost" size="sm" onClick={handleReset}>
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
