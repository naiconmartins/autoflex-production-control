import { RawMaterial } from "@/interfaces/raw-material";

export interface RawMaterialState {
  items: RawMaterial[];
  selectedItem: RawMaterial | null;
  loading: boolean;
  error: string | null;
  hydrated: boolean;
  pagination: {
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
  };
}
