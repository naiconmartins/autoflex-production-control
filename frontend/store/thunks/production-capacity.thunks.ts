import { getProductionCapacityAction } from "@/actions/production-capacity.actions";
import {
  setCapacity,
  setError,
  setLoading,
} from "../slices/production-capacity.slice";
import { AppDispatch } from "../store";

export const fetchProductionCapacity = () => {
  return async (dispatch: AppDispatch) => {
    try {
      dispatch(setLoading(true));
      const result = await getProductionCapacityAction();

      if (result.success) {
        dispatch(setCapacity({ data: result.data }));
      } else {
        dispatch(setError(result.error));
      }
    } catch (error: any) {
      dispatch(setError(error.message || "Error fetching production capacity"));
    }
  };
};
