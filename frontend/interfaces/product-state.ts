import { Product } from "./product";

export interface ProductState {
  items: Product[];
  selectedItem: Product | null;
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
