import { SidebarProvider } from "@/components/ui/sidebar";
import type { PropsWithChildren } from "react";

export function SidebarProviders({ children }: PropsWithChildren) {
  return <SidebarProvider>{children}</SidebarProvider>;
}
