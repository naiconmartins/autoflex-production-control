import {
  RawMaterial,
  RawMaterialPagination,
  RawMaterialRequest,
} from "@/interfaces/raw-material";
import { ApiService } from "./api.service";

class RawMaterialService extends ApiService {
  constructor() {
    super();
  }

  async create(data: RawMaterialRequest, token: string): Promise<RawMaterial> {
    return this.request<RawMaterial>("/raw-material", {
      method: "POST",
      body: JSON.stringify(data),
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  async update(
    id: string,
    data: RawMaterialRequest,
    token: string,
  ): Promise<RawMaterial> {
    return this.request<RawMaterial>(`/raw-material/${id}`, {
      method: "PUT",
      body: JSON.stringify(data),
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  async delete(id: string, token: string): Promise<void> {
    return this.request<void>(`/raw-material/${id}`, {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  async findById(id: string, token: string): Promise<RawMaterial> {
    return this.request<RawMaterial>(`/raw-material/${id}`, {
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
  ): Promise<RawMaterialPagination> {
    const queryParams = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
      sort: sortBy,
      dir: direction,
    });

    return this.request<RawMaterialPagination>(
      `/raw-material?${queryParams.toString()}`,
      {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      },
    );
  }
}

export const rawMaterialService = new RawMaterialService();
