import { AuthResponse, LoginCredentials } from "@/interfaces/auth";
import { User } from "@/interfaces/user";
import { ApiService } from "./api.service";

class AuthService extends ApiService {
  constructor() {
    super();
  }

  async login(credentials: LoginCredentials): Promise<AuthResponse> {
    return this.request<AuthResponse>("/auth/login", {
      method: "POST",
      body: JSON.stringify(credentials),
    });
  }

  async getAuthenticatedUser(token: string): Promise<User> {
    return this.request<User>("/user/me", {
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
  }
}

export const authService = new AuthService();
