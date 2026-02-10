import type { Product, ProductRequest } from "@/interfaces/product";

type ThunkDispatch = (action: unknown) => unknown;

type ActionResult<T = unknown> =
  | { success: true; data?: T }
  | { success: false; error: string; fieldErrors?: Record<string, string[]> };

export const fetchProducts = () => {
  return async (_dispatch: ThunkDispatch): Promise<ActionResult> => {
    return { success: true, data: undefined };
  };
};

export const searchProducts = (_name: string) => {
  return async (_dispatch: ThunkDispatch): Promise<ActionResult> => {
    return { success: true, data: undefined };
  };
};

export const createProduct = (_data: ProductRequest) => {
  return async (_dispatch: ThunkDispatch): Promise<ActionResult<Product>> => {
    return {
      success: true,
      data: {
        id: 1,
        code: "P-001",
        name: "Mock Product",
        price: 10,
        rawMaterials: [],
      },
    };
  };
};

export const editProduct = (_id: string, _data: ProductRequest) => {
  return async (_dispatch: ThunkDispatch): Promise<ActionResult<Product>> => {
    return {
      success: true,
      data: {
        id: 1,
        code: "P-001",
        name: "Mock Product",
        price: 10,
        rawMaterials: [],
      },
    };
  };
};

export const deleteProduct = (_id: string) => {
  return async (_dispatch: ThunkDispatch): Promise<ActionResult> => {
    return { success: true };
  };
};
