"use client";

import { useAppDispatch } from "@/lib/store/hooks";
import { clearSession, setSession } from "@/lib/store/slices/auth.slice";
import { useEffect } from "react";

export function ClientAuthBootstrap() {
  const dispatch = useAppDispatch();

  useEffect(() => {
    const redirectToLogin = () => {
      const currentPath = window.location.pathname;
      if (currentPath === "/login") {
        return;
      }

      const nextParam = `${currentPath}${window.location.search}`;
      const loginUrl = `/login?next=${encodeURIComponent(nextParam)}&authError=1`;
      window.location.replace(loginUrl);
    };

    const bootstrap = async () => {
      const currentPath = window.location.pathname;
      const isLoginPath = currentPath === "/login";

      try {
        const response = await fetch("/api/auth/me", {
          method: "GET",
          credentials: "include",
          cache: "no-store",
        });

        if (!response.ok) {
          dispatch(clearSession());
          if (!isLoginPath) {
            redirectToLogin();
          }
          return;
        }

        const user = await response.json();

        const token = document.cookie
          .split("; ")
          .find((row) => row.startsWith("token="))
          ?.split("=")[1];

        dispatch(setSession({ user, token: token || "" }));
        if (isLoginPath) {
          window.location.replace("/");
        }
      } catch (error) {
        dispatch(clearSession());
        if (!isLoginPath) {
          redirectToLogin();
        }
      }
    };

    bootstrap();
  }, [dispatch]);

  return null;
}
