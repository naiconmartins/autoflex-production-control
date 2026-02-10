import type { RawMaterial, RawMaterialRequest } from "@/interfaces/raw-material";

type ThunkDispatch = (action: unknown) => unknown;

type ActionResult<T = unknown> =
  | { success: true; data?: T }
  | { success: false; error: string; fieldErrors?: Record<string, string[]> };

export const fetchRawMaterials = () => {
  return async (_dispatch: ThunkDispatch): Promise<ActionResult> => {
    return { success: true, data: undefined };
  };
};

export const searchRawMaterials = (_name: string) => {
  return async (_dispatch: ThunkDispatch): Promise<ActionResult> => {
    return { success: true, data: undefined };
  };
};

export const createRawMaterial = (_data: RawMaterialRequest) => {
  return async (_dispatch: ThunkDispatch): Promise<ActionResult<RawMaterial>> => {
    return {
      success: true,
      data: {
        id: 1,
        code: "RM-001",
        name: "Mock Raw Material",
        stockQuantity: 10,
      },
    };
  };
};

export const editRawMaterial = (_id: string, _data: RawMaterialRequest) => {
  return async (_dispatch: ThunkDispatch): Promise<ActionResult<RawMaterial>> => {
    return {
      success: true,
      data: {
        id: 1,
        code: "RM-001",
        name: "Mock Raw Material",
        stockQuantity: 10,
      },
    };
  };
};

export const deleteRawMaterial = (_id: string) => {
  return async (_dispatch: ThunkDispatch): Promise<ActionResult> => {
    return { success: true };
  };
};
