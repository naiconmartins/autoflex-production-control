"use client";

import { formatCurrency } from "@/lib/utils";
import { motion } from "framer-motion";
import { Cell, Pie, PieChart, ResponsiveContainer, Tooltip } from "recharts";
import { Card } from "./ui/card";

export interface ProductItem {
  productName: string;
  totalValue: number;
}

interface ProductionPieChartProps {
  items: ProductItem[];
  grandTotalValue: number;
}

const COLORS = ["#05656a", "#008086", "#00a5a9", "#00d1d2"];

type PieDatum = { name: string; value: number };

type TooltipItem = {
  name?: string;
  value?: number;
  payload?: PieDatum & { fill?: string; stroke?: string };
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
  const name = String(item.name ?? item.payload?.name ?? "");
  const value = Number(item.value ?? item.payload?.value ?? 0);
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

export default function ProductionPieChart({
  items,
  grandTotalValue,
}: ProductionPieChartProps) {
  const data: PieDatum[] = items.map((item) => ({
    name: item.productName.split(" ").slice(0, 2).join(" "),
    value: item.totalValue,
  }));

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5, delay: 0.25 }}
      className="h-112.5"
    >
      <Card className="h-full shadow-none rounded-sm p-4">
        <h2 className="text-lg font-semibold text-foreground mb-1">
          Value Distribution
        </h2>
        <p className="text-sm text-muted-foreground mb-4">Share by product</p>

        <ResponsiveContainer width="100%" height={220}>
          <PieChart>
            <Pie
              data={data}
              cx="50%"
              cy="50%"
              innerRadius={55}
              outerRadius={90}
              paddingAngle={3}
              dataKey="value"
              stroke="transparent"
              isAnimationActive={false}
            >
              {data.map((_, index) => (
                <Cell
                  key={index}
                  fill={COLORS[index % COLORS.length]}
                  stroke="transparent"
                />
              ))}
            </Pie>

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
          </PieChart>
        </ResponsiveContainer>

        <div className="grid grid-cols-2 gap-2 mt-2">
          {data.map((entry, index) => (
            <div key={index} className="flex items-center gap-2">
              <div
                className="w-3 h-3 rounded-full shrink-0"
                style={{ backgroundColor: COLORS[index % COLORS.length] }}
              />
              <span className="text-xs text-muted-foreground truncate">
                {entry.name}
              </span>
            </div>
          ))}
        </div>
      </Card>
    </motion.div>
  );
}
