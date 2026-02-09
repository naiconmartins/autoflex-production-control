import { z } from "zod";

export const productRawMaterialSchema = z.object({
  id: z
    .number({
      required_error: "Raw material is required",
    })
    .int()
    .positive("Raw material id must be valid"),

  requiredQuantity: z.coerce
    .number({
      required_error: "Required quantity is required",
    })
    .min(0.01, "Required quantity must be greater than zero"),
});
