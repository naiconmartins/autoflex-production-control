import type { FormInput } from "@/interfaces/form-input";
import "@tanstack/react-table";
import type { UseFormReturn } from "react-hook-form";

declare module "@tanstack/react-table" {
  interface TableMeta<TData extends unknown> {
    rowActions?: {
      inputs: FormInput[];
      form: UseFormReturn<any>;
      onUpdate: (values: any, id: string) => Promise<void>;
      isSubmitting: boolean;
      onDelete?: (data: TData) => void;
    };
  }
}
