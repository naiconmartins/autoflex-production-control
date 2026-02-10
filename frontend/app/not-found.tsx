import type { Metadata } from "next";

import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import Link from "next/link";

export const metadata: Metadata = {
  title: "404 - Page Not Found",
  description: "The page you are looking for does not exist.",
};

export default function NotFound() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-zinc-50 p-6">
      <Card className="w-full max-w-md shadow-none rounded-sm p-8 text-center">
        <h1 className="text-6xl font-bold text-foreground mb-2">404</h1>

        <h2 className="text-lg font-semibold text-foreground mb-2">
          Page not found
        </h2>

        <p className="text-sm text-muted-foreground mb-6">
          The page you are trying to access does not exist or was moved.
        </p>

        <Button asChild className="w-full">
          <Link href="/">Back to dashboard</Link>
        </Button>
      </Card>
    </div>
  );
}
