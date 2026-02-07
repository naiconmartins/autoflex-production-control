import { z } from "zod";

export const rawMaterialSchema = z.object({
  id: z.number(),
  name: z.string(),
  stockQuantity: z.number(),
});

export type Task = z.infer<typeof rawMaterialSchema>;
