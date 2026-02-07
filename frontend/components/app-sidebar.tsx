"use client";

import { Box, GalleryVerticalEnd, User2 } from "lucide-react";
import * as React from "react";

import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarHeader,
  SidebarRail,
} from "@/components/ui/sidebar";
import { User } from "@/interfaces/user";
import { NavMain } from "./nav-main";
import { NavUser } from "./nav-user";
import { TeamSwitcher } from "./team-switcher";

export function AppSidebar({
  user,
  ...props
}: React.ComponentProps<typeof Sidebar> & { user: User }) {
  const data = {
    user: {
      name: user.firstName,
      email: user.email,
      avatar: "/7294793.avif",
    },
    teams: [
      {
        name: "Autoflex",
        logo: GalleryVerticalEnd,
        plan: "Production Control",
      },
    ],
    navMain: [
      {
        title: "Inventory",
        url: "/",
        icon: Box,
        isActive: true,
        items: [
          {
            title: "Raw Materials",
            url: "/raw-materials",
          },
          {
            title: "Products",
            url: "/products",
          },
        ],
      },
      {
        title: "Users",
        url: "/user",
        icon: User2,
        items: [
          {
            title: "Explorer",
            url: "#",
          },
        ],
      },
    ],
  };

  return (
    <Sidebar collapsible="icon" {...props}>
      <SidebarHeader>
        <TeamSwitcher teams={data.teams} />
      </SidebarHeader>
      <SidebarContent>
        <NavMain items={data.navMain} />
      </SidebarContent>
      <SidebarFooter>
        <NavUser user={data.user} />
      </SidebarFooter>
      <SidebarRail />
    </Sidebar>
  );
}
