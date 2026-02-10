"use client";

import { formatCurrency } from "@/lib/utils";
import { motion } from "framer-motion";
import {
  Bar,
  BarChart,
  CartesianGrid,
  Cell,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import { Card } from "./ui/card";

export interface ProductItem {
  productName: string;
  totalValue: number;
}

interface ValueBarChartProps {
  items: ProductItem[];
  grandTotalValue: number;
}

const COLORS = ["#05656a", "#008086", "#00a5a9", "#00d1d2"];

type BarDatum = {
  name: string;
  fullName: string;
  value: number;
};

type TooltipItem = {
  value?: number;
  payload?: BarDatum;
};

type CustomTooltipProps = {
  active?: boolean;
  payload?: readonly TooltipItem[];
  grandTotalValue: number;
};

const CustomTooltip = ({
  active,
  payload,
  grandTotalValue,
}: CustomTooltipProps) => {
  if (!active || !payload || !payload.length) return null;

  const item = payload[0];
  const value = Number(item.value ?? 0);
  const name = String(item.payload?.name ?? "");
  const safeTotal = grandTotalValue > 0 ? grandTotalValue : 1;
  const percent = value / safeTotal;

  return (
    <div className="bg-[#eefffd] rounded-lg p-3 border border-border/50">
      <p className="text-sm font-medium text-foreground">{name}</p>
      <p className="text-sm text-primary font-semibold">
        {formatCurrency(value)}
      </p>
      <p className="text-xs text-muted-foreground">
        {(percent * 100).toFixed(1)}%
      </p>
    </div>
  );
};

export default function ValueBarChart({
  items,
  grandTotalValue,
}: ValueBarChartProps) {
  const data: BarDatum[] = [...items]
    .sort((a, b) => b.totalValue - a.totalValue)
    .map((item) => ({
      name: item.productName.split(" ").slice(0, 2).join(" "),
      fullName: item.productName,
      value: item.totalValue,
    }));

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5, delay: 0.2 }}
      className="h-112.5"
    >
      <Card className="h-full shadow-none rounded-sm p-4">
        <h2 className="text-lg font-semibold text-foreground mb-1">
          Total Value by Product
        </h2>
        <p className="text-sm text-muted-foreground mb-6">
          Potential production revenue
        </p>

        <ResponsiveContainer width="100%" height={260}>
          <BarChart data={data}>
            <CartesianGrid strokeDasharray="3 3" stroke="rgba(0,0,0,0.08)" />
            <XAxis
              dataKey="name"
              tick={{ fill: "rgba(17,24,39,0.65)", fontSize: 12 }}
              tickLine={false}
            />
            <YAxis
              tick={{ fill: "rgba(17,24,39,0.65)", fontSize: 11 }}
              tickLine={false}
              axisLine={false}
              tickFormatter={(v) => `${(v / 1000).toFixed(0)}k`}
            />
            <Tooltip
              content={(props) => (
                <CustomTooltip
                  active={Boolean((props as any).active)}
                  payload={
                    (props as any).payload as readonly TooltipItem[] | undefined
                  }
                  grandTotalValue={grandTotalValue}
                />
              )}
              cursor={false}
            />
            <Bar
              dataKey="value"
              radius={[6, 6, 0, 0]}
              maxBarSize={60}
              activeBar={false}
            >
              {data.map((_, index) => (
                <Cell key={index} fill={COLORS[index % COLORS.length]} />
              ))}
            </Bar>
          </BarChart>
        </ResponsiveContainer>
      </Card>
    </motion.div>
  );
}
