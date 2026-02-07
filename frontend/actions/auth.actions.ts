"use server";

import { AuthResponse, LoginCredentials } from "@/interfaces/auth";
import { User } from "@/interfaces/user";
import { mapApiErrorToClientMessage } from "@/services/api-error.mapper";
import { ApiError } from "@/services/api.service";
import { authService } from "@/services/auth.service";
import { cookies } from "next/headers";
import { redirect } from "next/navigation";

type LoginActionSuccess = {
  success: true;
  data: {
    auth: AuthResponse;
    user: User;
  };
};

type LoginActionError = {
  success: false;
  status: number;
  error: string;
  fieldErrors?: Record<string, string[]>;
};

export async function loginAction(
  credentials: LoginCredentials,
): Promise<LoginActionSuccess | LoginActionError> {
  try {
    const auth = await authService.login(credentials);
    const user = await authService.getAuthenticatedUser(auth.accessToken);

    const cookieStore = await cookies();
    cookieStore.set("token", auth.accessToken, {
      httpOnly: false,
      secure: process.env.NODE_ENV === "production",
      sameSite: "lax",
      maxAge: auth.expires,
      path: "/",
    });

    return { success: true, data: { auth, user } };
  } catch (err: any) {
    if (err instanceof ApiError) {
      return {
        success: false,
        status: err.status,
        error: mapApiErrorToClientMessage(err),
        fieldErrors: err.status === 422 ? err.data?.errors : undefined,
      };
    }

    return {
      success: false,
      status: 500,
      error: "An unexpected error occurred. Please try again later.",
    };
  }
}

export async function logoutAction() {
  const cookieStore = await cookies();
  cookieStore.delete("token");
  redirect("/login");
}
