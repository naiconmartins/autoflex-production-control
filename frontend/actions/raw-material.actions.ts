"use server";

import { RawMaterialRequest } from "@/interfaces/raw-material";
import {
  extractFieldErrors,
  mapApiErrorToClientMessage,
} from "@/services/api-error.mapper";
import { ApiError } from "@/services/api.service";
import { rawMaterialService } from "@/services/raw-material.service";
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

export async function fetchRawMaterialsAction(
  page: number = 0,
  size: number = 10,
  sortBy: string = "name",
  direction: string = "asc",
) {
  return executeAction((token) =>
    rawMaterialService.findAll(token, page, size, sortBy, direction),
  );
}

export async function searchRawMaterialsAction(name: string) {
  return executeAction((token) => rawMaterialService.search(name, token));
}

export async function createRawMaterialAction(data: RawMaterialRequest) {
  return executeAction((token) => rawMaterialService.create(data, token));
}

export async function updateRawMaterialAction(
  id: string,
  data: RawMaterialRequest,
) {
  return executeAction((token) => rawMaterialService.update(id, data, token));
}

export async function deleteRawMaterialAction(id: string) {
  return executeAction(async (token) => {
    await rawMaterialService.delete(id, token);
    return undefined as void;
  });
}

export async function findRawMaterialByIdAction(id: string) {
  return executeAction((token) => rawMaterialService.findById(id, token));
}
