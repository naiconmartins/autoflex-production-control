import type { LoginCredentials } from "@/interfaces/auth";
import type { User } from "@/interfaces/user";

type LoginSuccess = {
  success: true;
  data: {
    auth: {
      accessToken: string;
      tokenType: string;
      expires: number;
    };
    user: User;
  };
};

type LoginError = {
  success: false;
  status: number;
  error: string;
  fieldErrors?: Record<string, string[]>;
};

export async function loginAction(
  credentials: LoginCredentials,
): Promise<LoginSuccess | LoginError> {
  if (!credentials.email || !credentials.password) {
    return {
      success: false,
      status: 401,
      error: "Invalid email or password.",
    };
  }

  return {
    success: true,
    data: {
      auth: {
        accessToken: "mock-token",
        tokenType: "Bearer",
        expires: 3600,
      },
      user: {
        id: "1",
        email: credentials.email,
        firstName: "Mock",
        roles: ["ADMIN"],
        active: true,
        createdAt: new Date().toISOString(),
      },
    },
  };
}

export async function logoutAction() {
  return;
}
