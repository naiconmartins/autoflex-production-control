import type { AppRouterInstance } from "next/dist/shared/lib/app-router-context.shared-runtime";

export function createAppRouterMock(
  overrides: Partial<AppRouterInstance> = {},
): AppRouterInstance {
  return {
    push: () => {},
    replace: () => {},
    refresh: () => {},
    back: () => {},
    forward: () => {},
    prefetch: async () => {},
    ...overrides,
  };
}
