"use client";

import { useRouter } from "next/navigation";
import React from "react";

import { loginAction } from "@/actions/auth.actions";
import { useAppDispatch, useAppSelector } from "@/store/hooks";
import { setAuthError, setSession } from "@/store/slices/auth.slice";

import { Button } from "@/components/ui/button";
import { Field, FieldGroup, FieldLabel } from "@/components/ui/field";
import { Input } from "@/components/ui/input";
import { cn } from "@/lib/utils";

export function LoginForm({
  className,
  ...props
}: React.ComponentProps<"form">) {
  const router = useRouter();
  const dispatch = useAppDispatch();
  const { error } = useAppSelector((state) => state.auth);

  const [loading, setLoading] = React.useState(false);

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();

    dispatch(setAuthError(null));
    setLoading(true);

    const formData = new FormData(event.currentTarget);
    const email = String(formData.get("email") || "");
    const password = String(formData.get("password") || "");

    const result = await loginAction({ email, password });

    if (!result.success) {
      dispatch(setAuthError(result.error));
      setLoading(false);
      return;
    }

    dispatch(
      setSession({
        user: result.data.user,
        token: result.data.auth.accessToken,
      }),
    );

    setLoading(false);
    router.push("/");
  }

  return (
    <form
      data-cy="login-form"
      onSubmit={handleSubmit}
      className={cn("flex flex-col gap-6", className)}
      {...props}
    >
      <FieldGroup>
        <div className="flex flex-col items-center gap-1 text-center">
          <h1 className="text-2xl font-bold">Welcome back</h1>
          <p className="text-muted-foreground text-sm text-balance">
            Enter your email and password to access your account
          </p>
        </div>

        {error && (
          <p
            data-cy="login-error"
            className="text-destructive text-sm font-medium text-center"
          >
            {error}
          </p>
        )}

        <Field>
          <FieldLabel htmlFor="email">Email</FieldLabel>
          <Input
            data-cy="login-email"
            id="email"
            name="email"
            type="email"
            placeholder="you@autoflex.com"
            required
            autoComplete="email"
          />
        </Field>

        <Field>
          <FieldLabel htmlFor="password">Password</FieldLabel>
          <Input
            data-cy="login-password"
            id="password"
            name="password"
            type="password"
            required
            autoComplete="current-password"
          />
        </Field>

        <Field>
          <Button
            data-cy="login-submit"
            type="submit"
            className="w-full"
            disabled={loading}
          >
            {loading ? "Signing in..." : "Sign in"}
          </Button>
        </Field>
      </FieldGroup>
    </form>
  );
}
