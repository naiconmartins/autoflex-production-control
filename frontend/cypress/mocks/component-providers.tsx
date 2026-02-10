import type { EnhancedStore } from "@reduxjs/toolkit";
import type { AppRouterInstance } from "next/dist/shared/lib/app-router-context.shared-runtime";
import { AppRouterContext } from "next/dist/shared/lib/app-router-context.shared-runtime";
import type { PropsWithChildren } from "react";
import { Provider } from "react-redux";
import { createAppRouterMock } from "./app-router";

type ComponentProvidersProps = PropsWithChildren<{
  store: EnhancedStore;
  router?: AppRouterInstance;
}>;

export function ComponentProviders({
  children,
  store,
  router,
}: ComponentProvidersProps) {
  return (
    <Provider store={store}>
      <AppRouterContext.Provider value={router ?? createAppRouterMock()}>
        {children}
      </AppRouterContext.Provider>
    </Provider>
  );
}
