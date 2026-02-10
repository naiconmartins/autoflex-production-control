import authReducer from "@/lib/store/slices/auth.slice";
import productReducer from "@/lib/store/slices/product.slice";
import productionCapacityReducer from "@/lib/store/slices/production-capacity.slice";
import rawMaterialReducer from "@/lib/store/slices/raw-material.slice";
import { configureStore } from "@reduxjs/toolkit";

export function createTestStore() {
  return configureStore({
    reducer: {
      auth: authReducer,
      product: productReducer,
      rawMaterial: rawMaterialReducer,
      productionCapacity: productionCapacityReducer,
    },
  });
}
