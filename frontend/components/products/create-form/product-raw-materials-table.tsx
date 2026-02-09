"use client";

import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { ChevronLeft, ChevronRight } from "lucide-react";
import { useState } from "react";

type RawMaterial = {
  id: number;
  code: string;
  name: string;
  stockQuantity: number;
};

type Pagination = {
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
};

export function ProductRawMaterialsTable({
  materials,
  selectedIds,
  onAdd,
  disabled,
  pagination,
  onPageChange,
}: {
  materials: RawMaterial[];
  selectedIds: number[];
  onAdd: (id: number) => void;
  disabled?: boolean;
  pagination: Pagination;
  onPageChange: (page: number) => void;
}) {
  const [search, setSearch] = useState("");

  const filtered = materials.filter((rm) => {
    const query = search.toLowerCase();
    return (
      rm.code.toLowerCase().includes(query) ||
      rm.name.toLowerCase().includes(query) ||
      String(rm.id).includes(query)
    );
  });

  const canGoPrevious = pagination.page > 0;
  const canGoNext = pagination.page < pagination.totalPages - 1;

  return (
    <Card className="p-4 h-fit shadow-none border-none">
      <div className="space-y-3">
        <div>
          <h3 className="text-sm font-semibold">Available Raw Materials</h3>
          <p className="text-xs text-muted-foreground">
            Select materials to add to the product
          </p>
        </div>

        <Input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search by id, code or name..."
          disabled={disabled}
        />

        <div className="rounded-md border">
          <div className="grid grid-cols-12 gap-2 border-b bg-muted/40 px-3 py-2 text-xs font-medium text-muted-foreground">
            <div className="col-span-2">ID</div>
            <div className="col-span-3">CODE</div>
            <div className="col-span-5">NAME</div>
            <div className="col-span-2 text-right">ACTION</div>
          </div>

          <div className="max-h-100 overflow-y-auto">
            {filtered.length === 0 ? (
              <div className="px-3 py-3 text-sm text-muted-foreground">
                No materials found
              </div>
            ) : (
              filtered.map((rm) => {
                const isSelected = selectedIds.includes(rm.id);
                return (
                  <div
                    key={rm.id}
                    className="grid grid-cols-12 items-center gap-2 px-3 py-2 border-b last:border-b-0 hover:bg-muted/50"
                  >
                    <div className="col-span-2 text-sm text-muted-foreground">
                      {rm.id}
                    </div>
                    <div className="col-span-3 text-sm font-medium truncate">
                      {rm.code}
                    </div>
                    <div className="col-span-5 text-sm truncate">{rm.name}</div>
                    <div className="col-span-2 flex justify-end">
                      <Button
                        type="button"
                        variant={isSelected ? "outline" : "secondary"}
                        size="sm"
                        disabled={disabled || isSelected}
                        onClick={() => onAdd(rm.id)}
                        className="hover:bg-blue-100 hover:text-blue-500"
                      >
                        {isSelected ? "Added" : "Add"}
                      </Button>
                    </div>
                  </div>
                );
              })
            )}
          </div>
        </div>

        <div className="flex items-center justify-between text-sm">
          <div className="text-muted-foreground">
            Page {pagination.page + 1} of {pagination.totalPages} -{" "}
            {pagination.totalElements} total
          </div>
          <div className="flex gap-2">
            <Button
              type="button"
              variant="outline"
              size="sm"
              onClick={() => onPageChange(pagination.page - 1)}
              disabled={!canGoPrevious || disabled}
            >
              <ChevronLeft className="h-4 w-4" />
              Previous
            </Button>
            <Button
              type="button"
              variant="outline"
              size="sm"
              onClick={() => onPageChange(pagination.page + 1)}
              disabled={!canGoNext || disabled}
            >
              Next
              <ChevronRight className="h-4 w-4" />
            </Button>
          </div>
        </div>
      </div>
    </Card>
  );
}
