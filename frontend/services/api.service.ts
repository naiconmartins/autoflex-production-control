import { env } from "@/lib/env";

export class ApiError extends Error {
  status: number;
  data: any;

  constructor(message: string, status: number, data?: any) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.data = data;
  }
}

export abstract class ApiService {
  protected readonly baseUrl: string;

  constructor() {
    const base = env.API_URL;

    if (!base) {
      throw new Error("env.API_URL is not set");
    }

    this.baseUrl = base.replace(/\/+$/, "");
  }

  protected async request<T>(
    endpoint: string,
    options: RequestInit,
  ): Promise<T> {
    const path = endpoint.startsWith("/") ? endpoint : `/${endpoint}`;
    const url = `${this.baseUrl}${path}`;

    try {
      const response = await fetch(url, {
        ...options,
        headers: {
          "Content-Type": "application/json",
          ...options.headers,
        },
        cache: "no-store",
      });

      const contentType = response.headers.get("content-type") || "";
      const isJson = contentType.includes("application/json");

      const data = isJson
        ? await response.json().catch(() => null)
        : await response.text().catch(() => null);

      if (!response.ok) {
        const message =
          (data && typeof data === "object" && (data.message || data.error)) ||
          `Request failed (${response.status})`;

        throw new ApiError(message, response.status, data);
      }

      return data as T;
    } catch (err: any) {
      if (err instanceof ApiError) throw err;

      const message =
        err?.cause?.message ||
        err?.message ||
        "Network error while calling API";

      throw new ApiError(message, 0, { cause: err?.cause ?? null });
    }
  }
}
