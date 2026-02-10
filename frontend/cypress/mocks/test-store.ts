import { configureStore } from "@reduxjs/toolkit";
import authReducer from "@/store/slices/auth.slice";
import productReducer from "@/store/slices/product.slice";
import productionCapacityReducer from "@/store/slices/production-capacity.slice";
import rawMaterialReducer from "@/store/slices/raw-material.slice";

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
