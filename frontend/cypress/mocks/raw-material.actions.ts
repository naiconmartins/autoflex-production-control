import type {
  RawMaterial,
  RawMaterialPagination,
  RawMaterialRequest,
} from "@/interfaces/raw-material";

type ActionResult<T = unknown> =
  | { success: true; data: T }
  | { success: false; status: number; error: string; fieldErrors?: Record<string, string[]> };

const emptyPagination: RawMaterialPagination = {
  content: [],
  page: 0,
  size: 10,
  totalElements: 0,
  totalPages: 0,
};

export async function fetchRawMaterialsAction(): Promise<ActionResult<RawMaterialPagination>> {
  return { success: true, data: emptyPagination };
}

export async function createRawMaterialAction(
  _data: RawMaterialRequest,
): Promise<ActionResult<RawMaterial>> {
  return {
    success: true,
    data: { id: 1, code: "RM-001", name: "Mock Raw Material", stockQuantity: 10 },
  };
}

export async function updateRawMaterialAction(
  _id: string,
  _data: RawMaterialRequest,
): Promise<ActionResult<RawMaterial>> {
  return {
    success: true,
    data: { id: 1, code: "RM-001", name: "Mock Raw Material", stockQuantity: 10 },
  };
}

export async function deleteRawMaterialAction(): Promise<ActionResult<void>> {
  return { success: true, data: undefined };
}

export async function findRawMaterialByIdAction(): Promise<ActionResult<RawMaterial>> {
  return {
    success: true,
    data: { id: 1, code: "RM-001", name: "Mock Raw Material", stockQuantity: 10 },
  };
}
