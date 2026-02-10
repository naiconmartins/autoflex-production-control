"use server";

import { ProductionCapacity } from "@/interfaces/production-capacity";
import {
  extractFieldErrors,
  mapApiErrorToClientMessage,
} from "@/services/api-error.mapper";
import { ApiError } from "@/services/api.service";
import { productionCapacityService } from "@/services/production-capacity";
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

export async function getProductionCapacityAction(): Promise<
  ActionResult<ProductionCapacity>
> {
  return executeAction((token) => productionCapacityService.getCapacity(token));
}
