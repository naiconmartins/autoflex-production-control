import type { ProductionCapacity } from "@/interfaces/production-capacity";

type ThunkDispatch = (action: unknown) => unknown;

type ActionResult<T = unknown> =
  | { success: true; data?: T }
  | { success: false; error: string };

export const fetchProductionCapacity = () => {
  return async (
    _dispatch: ThunkDispatch,
  ): Promise<ActionResult<ProductionCapacity>> => {
    return {
      success: true,
      data: {
        items: [],
        grandTotalValue: 0,
      },
    };
  };
};
