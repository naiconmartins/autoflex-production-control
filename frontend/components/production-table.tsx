"use client";

import { Card } from "@/components/ui/card";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { formatCurrency } from "@/lib/utils";
import { motion } from "framer-motion";

export interface ProductItem {
  productId: number;
  productCode: string;
  productName: string;
  unitPrice: number;
  producibleQuantity: number;
  totalValue: number;
}

interface ProductionTableProps {
  items: ProductItem[];
}

export default function ProductionTable({ items }: ProductionTableProps) {
  const sortedItems = [...items].sort((a, b) => b.totalValue - a.totalValue);
  const maxQty = Math.max(1, ...items.map((i) => i.producibleQuantity));

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5, delay: 0.3 }}
      className="h-full"
    >
      <Card className="p-4 shadow-none rounded-sm overflow-hidden">
        <div className="p-6 border-b">
          <h2 className="text-lg font-semibold text-foreground">
            Product Breakdown
          </h2>
          <p className="text-sm text-muted-foreground mt-1">
            Production capacity and estimated values
          </p>
        </div>

        <Table>
          <TableHeader>
            <TableRow className="hover:bg-transparent">
              <TableHead className="text-muted-foreground font-semibold text-xs uppercase tracking-wider">
                Code
              </TableHead>
              <TableHead className="text-muted-foreground font-semibold text-xs uppercase tracking-wider">
                Product
              </TableHead>
              <TableHead className="text-muted-foreground font-semibold text-xs uppercase tracking-wider text-right">
                Unit Price
              </TableHead>
              <TableHead className="text-muted-foreground font-semibold text-xs uppercase tracking-wider text-center">
                Producible Qty
              </TableHead>
              <TableHead className="text-muted-foreground font-semibold text-xs uppercase tracking-wider text-right">
                Total Value
              </TableHead>
            </TableRow>
          </TableHeader>

          <TableBody>
            {sortedItems.map((item, index) => (
              <TableRow key={item.productId} className="transition-colors">
                <TableCell className="text-muted-foreground">
                  {item.productCode}
                </TableCell>

                <TableCell className="font-medium text-foreground">
                  {item.productName}
                </TableCell>

                <TableCell className="text-right text-sm text-muted-foreground">
                  {formatCurrency(item.unitPrice)}
                </TableCell>

                <TableCell className="text-center">
                  <div className="flex items-center gap-3 justify-center">
                    <div className="w-20 h-2 rounded-full bg-muted overflow-hidden">
                      <motion.div
                        initial={{ width: 0 }}
                        animate={{
                          width: `${(item.producibleQuantity / maxQty) * 100}%`,
                        }}
                        transition={{
                          duration: 0.8,
                          delay: 0.5 + index * 0.1,
                        }}
                        className="h-full rounded-full bg-[#00a5a9]"
                      />
                    </div>
                    <span className="font-semibold text-foreground text-sm w-8">
                      {item.producibleQuantity}
                    </span>
                  </div>
                </TableCell>

                <TableCell className="text-right font-semibold text-foreground">
                  {formatCurrency(item.totalValue)}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </Card>
    </motion.div>
  );
}
