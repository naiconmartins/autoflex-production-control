import { configureStore } from "@reduxjs/toolkit";
import authReducer from "./slices/auth.slice";
import productReducer from "./slices/product.slice";
import rawMaterialReducer from "./slices/raw-material.slice";

export const store = configureStore({
  reducer: {
    auth: authReducer,
    rawMaterial: rawMaterialReducer,
    product: productReducer,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
