import { ProductionCapacity } from "@/interfaces/production-capacity";
import { ApiService } from "./api.service";

class ProductionCapacityService extends ApiService {
  constructor() {
    super();
  }
  async getCapacity(token: string): Promise<ProductionCapacity> {
    return this.request<ProductionCapacity>("production-capacity", {
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
  }
}

export const productionCapacityService = new ProductionCapacityService();
