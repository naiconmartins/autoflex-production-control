import {
  Product,
  ProductPagination,
  ProductRequest,
} from "@/interfaces/product";
import { ApiService } from "./api.service";

class ProductService extends ApiService {
  constructor() {
    super();
  }

  async create(data: ProductRequest, token: string): Promise<Product> {
    return this.request<Product>("/products", {
      method: "POST",
      body: JSON.stringify(data),
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  async update(
    id: string,
    data: ProductRequest,
    token: string,
  ): Promise<Product> {
    return this.request<Product>(`/products/${id}`, {
      method: "PUT",
      body: JSON.stringify(data),
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  async delete(id: string, token: string): Promise<void> {
    return this.request<void>(`/products/${id}`, {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  async findById(id: string, token: string): Promise<Product> {
    return this.request<Product>(`/products/${id}`, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  async findAll(
    token: string,
    page: number = 0,
    size: number = 10,
    sortBy: string = "name",
    direction: string = "asc",
  ): Promise<ProductPagination> {
    const queryParams = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
      sort: sortBy,
      dir: direction,
    });

    return this.request<ProductPagination>(
      `/products?${queryParams.toString()}`,
      {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      },
    );
  }
}

export const productService = new ProductService();
