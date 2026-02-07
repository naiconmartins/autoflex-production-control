export interface RawMaterial {
  id: number;
  code: string;
  name: string;
  stockQuantity: number;
}

export interface RawMaterialRequest {
  code: string;
  name: string;
  stockQuantity: number;
}

export interface RawMaterialPagination {
  content: RawMaterial[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}
