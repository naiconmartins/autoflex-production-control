import { mapApiErrorToClientMessage } from "@/services/api-error.mapper";
import { ApiError } from "@/services/api.service";
import { authService } from "@/services/auth.service";
import { cookies } from "next/headers";
import { NextResponse } from "next/server";

export async function GET() {
  const cookieStore = await cookies();
  const token = cookieStore.get("token")?.value;

  if (!token) {
    return NextResponse.json({ error: "No token" }, { status: 401 });
  }

  try {
    const user = await authService.getAuthenticatedUser(token);
    return NextResponse.json(user);
  } catch (error) {
    console.error("Auth error:", error);

    if (error instanceof ApiError) {
      const message = mapApiErrorToClientMessage(error);
      return NextResponse.json(
        { error: message },
        { status: error.status || 500 },
      );
    }

    return NextResponse.json(
      { error: "Internal server error" },
      { status: 500 },
    );
  }
}
