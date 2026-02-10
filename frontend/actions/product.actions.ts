"use server";

import { ProductRequest } from "@/interfaces/product";
import {
  extractFieldErrors,
  mapApiErrorToClientMessage,
} from "@/services/api-error.mapper";
import { ApiError } from "@/services/api.service";
import { productService } from "@/services/product.service";
import { ActionResult } from "@/types/actions.types";
import { getToken } from "./findCookie";

async function executeAction<T>(
  action: (token: string) => Promise<T>,
): Promise<ActionResult<T>> {
  try {
    const token = await getToken();
    if (!token) {
      return {
        success: false,
        status: 401,
        error: "Token not found. Please login again.",
      };
    }

    const data = await action(token);
    return { success: true, data };
  } catch (err: any) {
    if (err instanceof ApiError) {
      const fieldErrors = extractFieldErrors(err);

      return {
        success: false,
        status: err.status,
        error: mapApiErrorToClientMessage(err),
        fieldErrors,
      };
    }

    return {
      success: false,
      status: 500,
      error: "Unexpected error. Please try again.",
    };
  }
}

export async function fetchProductsAction(
  page: number = 0,
  size: number = 10,
  sortBy: string = "name",
  direction: string = "asc",
) {
  return executeAction((token) =>
    productService.findAll(token, page, size, sortBy, direction),
  );
}

export async function searchProductsAction(
  name: string,
  page: number = 0,
  size: number = 10,
  sortBy: string = "name",
  direction: string = "asc",
) {
  return executeAction((token) =>
    productService.search(token, name, page, size, sortBy, direction),
  );
}

export async function createProductAction(data: ProductRequest) {
  return executeAction((token) => productService.create(data, token));
}

export async function updateProductAction(id: string, data: ProductRequest) {
  return executeAction((token) => productService.update(id, data, token));
}

export async function deleteProductAction(id: string) {
  return executeAction(async (token) => {
    await productService.delete(id, token);
    return undefined as void;
  });
}

export async function findProductByIdAction(id: string) {
  return executeAction((token) => productService.findById(id, token));
}
