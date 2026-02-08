import { z } from "zod";

export const rawMaterialSchema = z.object({
  code: z.string().min(1, "Code is required"),
  name: z.string().min(1, "Name is required"),
  stockQuantity: z.coerce.number().min(1, "Stock quantity must be at least 1"),
});

export type RawMaterialSchema = z.infer<typeof rawMaterialSchema>;
