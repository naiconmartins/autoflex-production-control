import { ProductionCapacity } from "@/interfaces/production-capacity";
import { ProductionCapacityState } from "@/interfaces/production-capacity-state";
import { createSlice, PayloadAction } from "@reduxjs/toolkit";

const initialState: ProductionCapacityState = {
  data: null,
  loading: false,
  error: null,
  hydrated: false,
  selectedProductId: null,
};

const productionCapacitySlice = createSlice({
  name: "productionCapacity",
  initialState,
  reducers: {
    setCapacity: (
      state,
      action: PayloadAction<{ data: ProductionCapacity }>,
    ) => {
      state.data = action.payload.data;
      state.error = null;
      state.loading = false;
      state.hydrated = true;
    },
    clearCapacity: (state) => {
      state.data = null;
      state.error = null;
      state.loading = false;
      state.hydrated = false;
      state.selectedProductId = null;
    },
    setLoading: (state, action: PayloadAction<boolean>) => {
      state.loading = action.payload;
    },
    setError: (state, action: PayloadAction<string | null>) => {
      state.error = action.payload;
      state.loading = false;
    },
    setHydrated: (state, action: PayloadAction<boolean>) => {
      state.hydrated = action.payload;
    },
  },
});

export const { setCapacity, clearCapacity, setLoading, setError, setHydrated } =
  productionCapacitySlice.actions;

export default productionCapacitySlice.reducer;
