import {
  createRawMaterialAction,
  deleteRawMaterialAction,
  fetchRawMaterialsAction,
  searchRawMaterialsAction,
  updateRawMaterialAction,
} from "@/actions/raw-material.actions";
import {
  RawMaterial,
  RawMaterialPagination,
  RawMaterialRequest,
} from "@/interfaces/raw-material";
import {
  addRawMaterial,
  clearRawMaterialItems,
  removeRawMaterial,
  setError,
  setLoading,
  setRawMaterials,
  updateRawMaterial,
} from "../slices/raw-material.slice";
import { AppDispatch } from "../store";

function normalizeRawMaterialPagination(
  data: RawMaterialPagination | RawMaterial[] | undefined,
): RawMaterialPagination {
  if (!data) {
    return {
      content: [],
      page: 0,
      size: 10,
      totalElements: 0,
      totalPages: 0,
    };
  }

  if (Array.isArray(data)) {
    return {
      content: data,
      page: 0,
      size: data.length || 10,
      totalElements: data.length,
      totalPages: 1,
    };
  }

  return {
    content: Array.isArray(data.content) ? data.content : [],
    page: data.page ?? 0,
    size: data.size ?? 10,
    totalElements: data.totalElements ?? data.content?.length ?? 0,
    totalPages: data.totalPages ?? 1,
  };
}

export const fetchRawMaterials = (params?: {
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

      const result = await fetchRawMaterialsAction(
        page,
        size,
        sortBy,
        direction,
      );

      if (result.success) {
        dispatch(
          setRawMaterials(
            normalizeRawMaterialPagination(
              result.data as RawMaterialPagination | RawMaterial[] | undefined,
            ),
          ),
        );
      } else {
        dispatch(setError(result.error));
      }
    } catch (error: any) {
      dispatch(setError(error.message || "Erro ao buscar matérias-primas"));
    }
  };
};

export const searchRawMaterials = (name: string) => {
  return async (dispatch: AppDispatch) => {
    try {
      dispatch(clearRawMaterialItems());
      dispatch(setLoading(true));

      const result = await searchRawMaterialsAction(name);

      if (result.success) {
        dispatch(
          setRawMaterials(
            normalizeRawMaterialPagination(
              result.data as RawMaterialPagination | RawMaterial[] | undefined,
            ),
          ),
        );
      } else {
        dispatch(setError(result.error));
      }
    } catch (error: any) {
      dispatch(setError(error.message || "Erro ao buscar matérias-primas"));
    }
  };
};

export const createRawMaterial = (data: RawMaterialRequest) => {
  return async (dispatch: AppDispatch) => {
    try {
      dispatch(setLoading(true));
      const result = await createRawMaterialAction(data);

      if (result.success) {
        dispatch(addRawMaterial(result.data));
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

export const editRawMaterial = (id: string, data: RawMaterialRequest) => {
  return async (dispatch: AppDispatch) => {
    try {
      dispatch(setLoading(true));
      const result = await updateRawMaterialAction(id, data);

      if (result.success) {
        dispatch(updateRawMaterial(result.data));
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

export const deleteRawMaterial = (id: string) => {
  return async (dispatch: AppDispatch) => {
    try {
      dispatch(setLoading(true));
      const result = await deleteRawMaterialAction(id);

      if (result.success) {
        dispatch(removeRawMaterial(id));
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
