import {
  createProductAction,
  deleteProductAction,
  fetchProductsAction,
  searchProductsAction,
  updateProductAction,
} from "@/actions/product.actions";
import { ProductRequest } from "@/interfaces/product";
import {
  addProduct,
  clearProductItems,
  removeProduct,
  setError,
  setLoading,
  setProducts,
  updateProduct,
} from "../slices/product.slice";
import { AppDispatch } from "../store";

export const fetchProducts = (params?: {
  page?: number;
  size?: number;
  sortBy?: string;
  direction?: string;
}) => {
  return async (dispatch: AppDispatch) => {
    try {
      dispatch(setLoading(true));
      const page = params?.page ?? 0;
      const size = params?.size ?? 10;
      const sortBy = params?.sortBy ?? "id";
      const direction = params?.direction ?? "asc";

      const result = await fetchProductsAction(page, size, sortBy, direction);

      if (result.success) {
        dispatch(setProducts(result.data));
      } else {
        dispatch(setError(result.error));
      }
    } catch (error: any) {
      dispatch(setError(error.message || "Erro ao buscar matérias-primas"));
    }
  };
};

export const searchProducts = (
  name: string,
  params?: {
    page?: number;
    size?: number;
    sortBy?: string;
    direction?: string;
  },
) => {
  return async (dispatch: AppDispatch) => {
    try {
      dispatch(clearProductItems());
      dispatch(setLoading(true));
      const page = params?.page ?? 0;
      const size = params?.size ?? 10;
      const sortBy = params?.sortBy ?? "id";
      const direction = params?.direction ?? "asc";

      const result = await searchProductsAction(
        name,
        page,
        size,
        sortBy,
        direction,
      );

      if (result.success) {
        dispatch(setProducts(result.data));
      } else {
        dispatch(setError(result.error));
      }
    } catch (error: any) {
      dispatch(setError(error.message || "Erro ao buscar matérias-primas"));
    }
  };
};

export const createProduct = (data: ProductRequest) => {
  return async (dispatch: AppDispatch) => {
    try {
      dispatch(setLoading(true));
      const result = await createProductAction(data);

      if (result.success) {
        dispatch(addProduct(result.data));
        dispatch(setLoading(false));
        return { success: true, data: result.data };
      } else {
        dispatch(setError(result.error));
        dispatch(setLoading(false));
        return {
          success: false,
          error: result.error,
          fieldErrors: result.fieldErrors,
        };
      }
    } catch (error: any) {
      dispatch(setError(error.message || "Erro ao criar matéria-prima"));
      dispatch(setLoading(false));
      throw error;
    }
  };
};

export const editProduct = (id: string, data: ProductRequest) => {
  return async (dispatch: AppDispatch) => {
    try {
      dispatch(setLoading(true));
      const result = await updateProductAction(id, data);

      if (result.success) {
        dispatch(updateProduct(result.data));
        dispatch(setLoading(false));
        return { success: true, data: result.data };
      } else {
        dispatch(setError(result.error));
        dispatch(setLoading(false));
        return {
          success: false,
          error: result.error,
          fieldErrors: result.fieldErrors,
        };
      }
    } catch (error: any) {
      dispatch(setError(error.message || "Erro ao atualizar matéria-prima"));
      dispatch(setLoading(false));
      throw error;
    }
  };
};

export const deleteProduct = (id: string) => {
  return async (dispatch: AppDispatch) => {
    try {
      dispatch(setLoading(true));
      const result = await deleteProductAction(id);

      if (result.success) {
        dispatch(removeProduct(id));
        dispatch(setLoading(false));
        return { success: true };
      } else {
        dispatch(setError(result.error));
        dispatch(setLoading(false));
        return { success: false, error: result.error };
      }
    } catch (error: any) {
      dispatch(setError(error.message || "Erro ao deletar matéria-prima"));
      dispatch(setLoading(false));
      throw error;
    }
  };
};
