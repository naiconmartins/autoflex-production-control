import type { Product, ProductPagination, ProductRequest } from "@/interfaces/product";

type ActionResult<T = unknown> =
  | { success: true; data: T }
  | { success: false; status: number; error: string; fieldErrors?: Record<string, string[]> };

const emptyPagination: ProductPagination = {
  content: [],
  page: 0,
  size: 10,
  totalElements: 0,
  totalPages: 0,
};

export async function fetchProductsAction(): Promise<ActionResult<ProductPagination>> {
  return { success: true, data: emptyPagination };
}

export async function createProductAction(
  _data: ProductRequest,
): Promise<ActionResult<Product>> {
  return {
    success: true,
    data: { id: 1, code: "P-001", name: "Mock Product", price: 10, rawMaterials: [] },
  };
}

export async function updateProductAction(
  _id: string,
  _data: ProductRequest,
): Promise<ActionResult<Product>> {
  return {
    success: true,
    data: { id: 1, code: "P-001", name: "Mock Product", price: 10, rawMaterials: [] },
  };
}

export async function deleteProductAction(): Promise<ActionResult<void>> {
  return { success: true, data: undefined };
}

export async function findProductByIdAction(): Promise<ActionResult<Product>> {
  return {
    success: true,
    data: { id: 1, code: "P-001", name: "Mock Product", price: 10, rawMaterials: [] },
  };
}
