"use client";

import { store } from "@/lib/store/store";
import { Provider } from "react-redux";
import { ClientAuthBootstrap } from "./client-auth-bootstrap";

export function StoreProvider({ children }: { children: React.ReactNode }) {
  return (
    <Provider store={store}>
      <ClientAuthBootstrap />
      {children}
    </Provider>
  );
}
