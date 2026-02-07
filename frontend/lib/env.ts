import { z } from "zod";

const envSchema = z.object({
  API_URL: z.string().min(1, "API_URL is required"),
});

let parsedEnv;

try {
  parsedEnv = envSchema.parse({
    API_URL: process.env.API_URL,
  });
} catch (error) {
  throw error;
}

export const env = parsedEnv;
