import z from "zod";
import { productRawMaterialSchema } from "./product-raw-material";

export const productSchema = z.object({
  code: z.string().min(1, "Product code is required"),

  name: z.string().min(1, "Product name is required"),

  price: z.coerce
    .number({
      required_error: "Product price is required",
    })
    .min(0.01, "Price must be greater than zero"),

  rawMaterials: z
    .array(productRawMaterialSchema)
    .min(1, "The product must contain at least one raw material"),
});
