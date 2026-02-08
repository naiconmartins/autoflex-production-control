import {
  createRawMaterialAction,
  deleteRawMaterialAction,
  fetchRawMaterialsAction,
  updateRawMaterialAction,
} from "@/actions/raw-material.actions";
import { RawMaterialRequest } from "@/interfaces/raw-material";
import {
  addRawMaterial,
  removeRawMaterial,
  setError,
  setLoading,
  setRawMaterials,
  updateRawMaterial,
} from "../slices/raw-material.slice";
import { AppDispatch } from "../store";

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
        dispatch(setRawMaterials(result.data));
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
