import type { ProductionCapacity } from "@/interfaces/production-capacity";

type ActionResult<T = unknown> =
  | { success: true; data: T }
  | { success: false; status: number; error: string; fieldErrors?: Record<string, string[]> };

export async function getProductionCapacityAction(): Promise<ActionResult<ProductionCapacity>> {
  return {
    success: true,
    data: {
      items: [],
      grandTotalValue: 0,
    },
  };
}
