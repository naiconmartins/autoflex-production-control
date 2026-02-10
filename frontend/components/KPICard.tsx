"use client";

import { motion } from "framer-motion";
import type { LucideIcon } from "lucide-react";
import { Card } from "./ui/card";

interface KPICardProps {
  title: string;
  value: string;
  subtitle?: string;
  icon?: LucideIcon;
  variant?: "default" | "primary" | "accent";
  delay?: number;
}

export default function KPICard({
  title,
  value,
  subtitle,
  icon: Icon,
  variant = "default",
  delay = 0,
}: KPICardProps) {
  const cardVariant =
    variant === "primary"
      ? "bg-primary text-white"
      : variant === "accent"
        ? "border-accent/30 bg-card shadow-[0_0_0_1px_oklch(var(--accent)/0.2)]"
        : "border-border bg-card";

  const textVariant = variant === "primary" ? "text-white" : null;

  return (
    <motion.div
      initial={{ opacity: 0, y: 16 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.4, delay }}
    >
      <Card className={`shadow-none rounded-sm p-4 ${cardVariant}`}>
        <div className="flex items-start justify-between gap-4">
          <div className="space-y-1.5">
            <p
              className={`text-xs font-medium uppercase tracking-wider text-muted-foreground ${textVariant}`}
            >
              {title}
            </p>
            <p
              className={`text-3xl font-semibold tracking-tight text-foreground ${textVariant}`}
            >
              {value}
            </p>
            {subtitle && (
              <p className={`text-sm text-muted-foreground ${textVariant}`}>
                {subtitle}
              </p>
            )}
          </div>

          {Icon && (
            <div className="rounded-lg p-3">
              <Icon className="h-5 w-5" />
            </div>
          )}
        </div>
      </Card>
    </motion.div>
  );
}
