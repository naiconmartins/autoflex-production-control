"use client";

import { useAppDispatch } from "@/store/hooks";
import { clearSession, setSession } from "@/store/slices/auth.slice";
import { useEffect } from "react";

export function ClientAuthBootstrap() {
  const dispatch = useAppDispatch();

  useEffect(() => {
    const bootstrap = async () => {
      try {
        const response = await fetch("/api/auth/me");

        if (!response.ok) {
          dispatch(clearSession());
          return;
        }

        const user = await response.json();

        const token = document.cookie
          .split("; ")
          .find((row) => row.startsWith("token="))
          ?.split("=")[1];

        dispatch(setSession({ user, token: token || "" }));
      } catch (error) {
        dispatch(clearSession());
      }
    };

    bootstrap();
  }, [dispatch]);

  return null;
}
