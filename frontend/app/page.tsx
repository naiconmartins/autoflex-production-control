"use client";

import KPICard from "@/components/KPICard";
import ProductionPieChart, {
  type ProductItem as PieItem,
} from "@/components/production-pie-chart";
import ProductionTable from "@/components/production-table";
import SidebarComponent from "@/components/sidebar-component";
import ValueBarChart, {
  type ProductItem as BarItem,
} from "@/components/value-bar-chart";
import { formatCurrency } from "@/lib/utils";
import { useAppDispatch, useAppSelector } from "@/store/hooks";
import { fetchProductionCapacity } from "@/store/thunks/production-capacity.thunks";
import { DollarSign } from "lucide-react";
import { useEffect, useMemo } from "react";

type ProductionCapacityItem = {
  productId: number;
  productCode: string;
  productName: string;
  unitPrice: number;
  producibleQuantity: number;
  totalValue: number;
};

type ProductionCapacityData = {
  items: ProductionCapacityItem[];
  grandTotalValue: number;
};

export default function Dashboard() {
  const dispatch = useAppDispatch();
  const user = useAppSelector((state) => state.auth.user);
  const { data, loading } = useAppSelector(
    (state) => state.productionCapacity,
  ) as {
    data: ProductionCapacityData | null;
    loading: boolean;
  };

  useEffect(() => {
    dispatch(fetchProductionCapacity());
  }, [dispatch]);

  const items = useMemo(() => data?.items ?? [], [data]);

  const { grandTotalValue, totalProducts, totalUnits, avgPrice } =
    useMemo(() => {
      const grandTotalValue = data?.grandTotalValue ?? 0;
      const totalProducts = items.length;
      const totalUnits = items.reduce(
        (acc, item) => acc + item.producibleQuantity,
        0,
      );
      const avgPrice = totalUnits > 0 ? grandTotalValue / totalUnits : 0;

      return { grandTotalValue, totalProducts, totalUnits, avgPrice };
    }, [data, items]);

  if (!user || loading || !data) {
    return (
      <div className="flex h-screen items-center justify-center">
        <div className="text-muted-foreground">Loading...</div>
      </div>
    );
  }

  return (
    <SidebarComponent user={user} link="Production" page="Production Capacity">
      <main className="bg-zinc-50 flex-1 pb-10">
        <section className="p-6 bg-zinc-50">
          <div className="flex flex-row items-center justify-between pb-6">
            <h1 className="text-2xl font-semibold">Production Capacity</h1>
          </div>

          <div className="h-gull p-6 bg-zinc-50 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            <KPICard
              title="Total Value"
              value={formatCurrency(grandTotalValue)}
              subtitle="Potential revenue"
              icon={DollarSign}
              variant="primary"
              delay={0}
            />
            <KPICard
              title="Products"
              value={String(totalProducts)}
              subtitle="Production lines"
              delay={0.05}
            />
            <KPICard
              title="Producible Units"
              value={String(totalUnits)}
              subtitle="Total capacity"
              delay={0.1}
            />
            <KPICard
              title="Average Price"
              value={formatCurrency(avgPrice)}
              subtitle="Per unit"
              delay={0.15}
            />
          </div>
        </section>

        <section className="p-6 bg-zinc-50 grid grid-cols-1 lg:grid-cols-3 gap-4">
          <div className="lg:col-span-2">
            <ValueBarChart
              items={items as unknown as BarItem[]}
              grandTotalValue={data.grandTotalValue}
            />
          </div>
          <div className="lg:col-span-1">
            <ProductionPieChart
              items={items as unknown as PieItem[]}
              grandTotalValue={data.grandTotalValue}
            />
          </div>
        </section>
        <section className="p-6 bg-zinc-50">
          <ProductionTable items={data.items} />
        </section>
      </main>
    </SidebarComponent>
  );
}
