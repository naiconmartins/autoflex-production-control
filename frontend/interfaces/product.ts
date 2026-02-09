import { ProductRawMaterial } from "./product-raw-material";

export interface Product {
  id: number;
  code: string;
  name: string;
  price: number;
  rawMaterials: ProductRawMaterial[];
}

export interface ProductRequest {
  code: string;
  name: string;
  price: number;
}

export interface ProductPagination {
  content: Product[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}
