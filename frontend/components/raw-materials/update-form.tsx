import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { RowDataProps } from "@/interfaces/row-data";
import { rawMaterialSchema } from "@/schemas/raw-material-schema";
import { useAppDispatch } from "@/store/hooks";
import {
  editRawMaterial,
  fetchRawMaterials,
} from "@/store/thunks/raw-material.thunks";
import { zodResolver } from "@hookform/resolvers/zod";
import { PencilIcon } from "lucide-react";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import z from "zod";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "../ui/form";
import { Input } from "../ui/input";

export default function UpdateRawMaterialForm<TData>({
  row,
}: RowDataProps<TData>) {
  const dispatch = useAppDispatch();
  const [open, setOpen] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const form = useForm<z.infer<typeof rawMaterialSchema>>({
    resolver: zodResolver(rawMaterialSchema),
    defaultValues: {
      code: "",
      name: "",
      stockQuantity: 0,
    },
  });

  useEffect(() => {
    if (!open) return;

    form.reset({
      code: row.getValue("code") as string,
      name: row.getValue("name") as string,
      stockQuantity: Number(row.getValue("stockQuantity")),
    });
  }, [open, row, form]);

  const onSubmit = async (values: z.infer<typeof rawMaterialSchema>) => {
    setIsSubmitting(true);

    try {
      const id = row.getValue("id");
      const result = await dispatch(editRawMaterial(id as any, values));

      if (result.success) {
        await dispatch(fetchRawMaterials());
        setOpen(false);
      } else {
        if (result.fieldErrors) {
          for (const [field, messages] of Object.entries(result.fieldErrors)) {
            form.setError(field as any, {
              type: "manual",
              message: messages[0],
            });
          }
        }

        form.setError("root", {
          type: "manual",
          message: result.error,
        });
      }
    } catch (err: any) {
      form.setError("root", {
        type: "manual",
        message: err?.message || "Erro inesperado",
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <PencilIcon className="w-4 h-4 cursor-pointer" />
      </DialogTrigger>

      <DialogContent className="sm:max-w-md p-0">
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)}>
            <DialogHeader className="p-6">
              <DialogTitle>Edit Raw Material</DialogTitle>
            </DialogHeader>

            {form.formState.errors.root && (
              <div className="mx-6 mb-4 text-sm text-red-600">
                {form.formState.errors.root.message}
              </div>
            )}

            <FormField
              control={form.control}
              name="code"
              render={({ field }) => (
                <FormItem className="px-6 pb-4">
                  <FormLabel>Code</FormLabel>
                  <FormControl>
                    <Input {...field} disabled={isSubmitting} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="name"
              render={({ field }) => (
                <FormItem className="px-6 pb-4">
                  <FormLabel>Name</FormLabel>
                  <FormControl>
                    <Input {...field} disabled={isSubmitting} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="stockQuantity"
              render={({ field }) => (
                <FormItem className="px-6 pb-4">
                  <FormLabel>Stock Quantity</FormLabel>
                  <FormControl>
                    <Input
                      type="number"
                      value={field.value ?? 0}
                      onChange={(e) => field.onChange(Number(e.target.value))}
                      disabled={isSubmitting}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <DialogFooter className="px-6 py-6 bg-zinc-100">
              <Button
                type="button"
                variant="outline"
                onClick={() => setOpen(false)}
                disabled={isSubmitting}
              >
                Cancel
              </Button>
              <Button type="submit" disabled={isSubmitting}>
                {isSubmitting ? "Saving..." : "Save"}
              </Button>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}
