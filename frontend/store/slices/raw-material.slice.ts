import { RawMaterial, RawMaterialPagination } from "@/interfaces/raw-material";
import { RawMaterialState } from "@/interfaces/raw-material-state";
import { createSlice, PayloadAction } from "@reduxjs/toolkit";

const initialState: RawMaterialState = {
  items: [],
  selectedItem: null,
  loading: false,
  error: null,
  hydrated: false,
  pagination: {
    page: 0,
    size: 10,
    totalElements: 0,
    totalPages: 0,
  },
};

const rawMaterialSlice = createSlice({
  name: "rawMaterial",
  initialState,
  reducers: {
    setRawMaterials: (state, action: PayloadAction<RawMaterialPagination>) => {
      state.items = action.payload.content;
      state.pagination = {
        page: action.payload.page,
        size: action.payload.size,
        totalElements: action.payload.totalElements,
        totalPages: action.payload.totalPages,
      };
      state.error = null;
      state.loading = false;
      state.hydrated = true;
    },
    addRawMaterial: (state, action: PayloadAction<RawMaterial>) => {
      state.items.push(action.payload);
      state.pagination.totalElements += 1;
      state.error = null;
    },
    updateRawMaterial: (state, action: PayloadAction<RawMaterial>) => {
      const index = state.items.findIndex(
        (item) => item.id === action.payload.id,
      );
      if (index !== -1) {
        state.items[index] = action.payload;
      }
      state.error = null;
    },
    removeRawMaterial: (state, action: PayloadAction<string | number>) => {
      state.items = state.items.filter((item) => item.id !== action.payload);
      state.pagination.totalElements -= 1;
      state.error = null;
    },
    setSelectedRawMaterial: (
      state,
      action: PayloadAction<RawMaterial | null>,
    ) => {
      state.selectedItem = action.payload;
    },
    setLoading: (state, action: PayloadAction<boolean>) => {
      state.loading = action.payload;
    },
    setError: (state, action: PayloadAction<string | null>) => {
      state.error = action.payload;
      state.loading = false;
    },
    clearRawMaterials: (state) => {
      state.items = [];
      state.selectedItem = null;
      state.error = null;
      state.loading = false;
      state.hydrated = false;
      state.pagination = {
        page: 0,
        size: 10,
        totalElements: 0,
        totalPages: 0,
      };
    },
    setHydrated: (state, action: PayloadAction<boolean>) => {
      state.hydrated = action.payload;
    },
  },
});

export const {
  setRawMaterials,
  addRawMaterial,
  updateRawMaterial,
  removeRawMaterial,
  setSelectedRawMaterial,
  setLoading,
  setError,
  clearRawMaterials,
  setHydrated,
} = rawMaterialSlice.actions;

export default rawMaterialSlice.reducer;
