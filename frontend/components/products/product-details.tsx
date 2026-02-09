"use client";

import { Button } from "@/components/ui/button";
import {
  Drawer,
  DrawerClose,
  DrawerContent,
  DrawerDescription,
  DrawerFooter,
  DrawerHeader,
  DrawerTitle,
  DrawerTrigger,
} from "@/components/ui/drawer";
import { Product } from "@/interfaces/product";
import { Row } from "@tanstack/react-table";
import { ArrowRightToLine, Eye } from "lucide-react";
import { Card } from "../ui/card";
import { Separator } from "../ui/separator";

type Props = { row: Row<Product> };

export default function ProductDetails({ row }: Props) {
  return (
    <Drawer direction="right">
      <DrawerTrigger asChild>
        <Eye className="w-4 h-4 cursor-pointer" />
      </DrawerTrigger>
      <DrawerContent className="data-[vaul-drawer-direction=right]:sm:w-200 data-[vaul-drawer-direction=right]:sm:max-w-none">
        <div className="mx-auto w-full">
          <DrawerHeader>
            <div className="py-4">
              <DrawerTitle className="flex flex-row items-center gap-4">
                <ArrowRightToLine className="w-4 h-4 text-zinc-500" />
                Product Details
              </DrawerTitle>
            </div>
            <Separator />
            <DrawerDescription />
          </DrawerHeader>
          <div className="p-4 pb-0 space-y-6">
            <Card className="rounded-sm p-4 shadow-none">
              <h1 className="text-base font-semibold">Informations</h1>
              <div className="flex flex-row justify-between">
                <div>
                  <p className="text-xs text-zinc-500">Code</p>
                  <span className="text-sm text-foreground">
                    {row.original.code}
                  </span>
                </div>
                <div>
                  <p className="text-xs text-zinc-500">Name</p>
                  <span className="text-sm text-foreground">
                    {row.original.name}
                  </span>
                </div>
                <div>
                  <p className="text-xs text-zinc-500">Price</p>
                  <span className="text-sm text-foreground">
                    {new Intl.NumberFormat("en-US", {
                      style: "currency",
                      currency: "USD",
                    }).format(row.original.price)}
                  </span>
                </div>
              </div>
            </Card>
            <Card className="rounded-sm p-4 shadow-none">
              <h2 className="text-base font-semibold">
                Required Raw Materials
              </h2>
              <table className="w-full table-fixed border-collapse">
                <thead>
                  <tr className="border-b text-left text-xs text-muted-foreground">
                    <th className="w-15 pb-2 font-medium">ID</th>
                    <th className="w-30 pb-2 font-medium">CODE</th>
                    <th className="pb-2 font-medium w-fit">NAME</th>
                    <th className="w-30 pb-2 text-right font-medium">
                      REQUIRED QTY
                    </th>
                  </tr>
                </thead>

                <tbody>
                  {row.original.rawMaterials.map((rawMaterial) => (
                    <tr
                      key={rawMaterial.id}
                      className="border-b last:border-b-0 hover:bg-muted/50"
                    >
                      <td className="py-2 text-sm text-muted-foreground">
                        {rawMaterial.id}
                      </td>
                      <td className="py-2 text-sm font-medium">
                        {rawMaterial.code}
                      </td>
                      <td className="py-2 text-sm truncate">
                        {rawMaterial.name}
                      </td>
                      <td className="py-2 text-sm text-right font-medium">
                        {rawMaterial.requiredQuantity}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </Card>
          </div>
          <DrawerFooter className="flex items-end">
            <DrawerClose asChild>
              <Button variant="outline" className="w-20">
                Close
              </Button>
            </DrawerClose>
          </DrawerFooter>
        </div>
      </DrawerContent>
    </Drawer>
  );
}
