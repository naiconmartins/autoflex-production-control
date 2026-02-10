import { ProductionCapacity } from "@/interfaces/production-capacity";

export interface ProductionCapacityState {
  data: ProductionCapacity | null;
  loading: boolean;
  error: string | null;
  hydrated: boolean;
  selectedProductId: string | null;
}
