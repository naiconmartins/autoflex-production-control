import { Product, ProductPagination } from "@/interfaces/product";
import { ProductState } from "@/interfaces/product-state";
import { createSlice, PayloadAction } from "@reduxjs/toolkit";

const initialState: ProductState = {
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

const productSlice = createSlice({
  name: "Product",
  initialState,
  reducers: {
    setProducts: (state, action: PayloadAction<ProductPagination>) => {
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
    addProduct: (state, action: PayloadAction<Product>) => {
      state.items.push(action.payload);
      state.pagination.totalElements += 1;
      state.error = null;
    },
    updateProduct: (state, action: PayloadAction<Product>) => {
      const index = state.items.findIndex(
        (item) => item.id === action.payload.id,
      );
      if (index !== -1) {
        state.items[index] = action.payload;
      }
      state.error = null;
    },
    removeProduct: (state, action: PayloadAction<string | number>) => {
      state.items = state.items.filter((item) => item.id !== action.payload);
      state.pagination.totalElements -= 1;
      state.error = null;
    },
    setSelectedProduct: (state, action: PayloadAction<Product | null>) => {
      state.selectedItem = action.payload;
    },
    setLoading: (state, action: PayloadAction<boolean>) => {
      state.loading = action.payload;
    },
    clearProductItems: (state) => {
      state.items = [];
      state.pagination = {
        page: 0,
        size: state.pagination.size,
        totalElements: 0,
        totalPages: 0,
      };
      state.error = null;
    },
    setError: (state, action: PayloadAction<string | null>) => {
      state.error = action.payload;
      state.loading = false;
    },
    clearProducts: (state) => {
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
  setProducts,
  addProduct,
  updateProduct,
  removeProduct,
  setSelectedProduct,
  setLoading,
  clearProductItems,
  setError,
  clearProducts,
  setHydrated,
} = productSlice.actions;

export default productSlice.reducer;
