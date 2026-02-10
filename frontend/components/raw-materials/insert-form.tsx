import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { useAppDispatch } from "@/lib/store/hooks";
import {
  createRawMaterial,
  fetchRawMaterials,
} from "@/lib/store/thunks/raw-material.thunks";
import { rawMaterialSchema } from "@/schemas/raw-material-schema";
import { zodResolver } from "@hookform/resolvers/zod";
import { Plus } from "lucide-react";
import { useState } from "react";
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

const inputs = [
  {
    name: "code",
    label: "Code",
    type: "text" as const,
  },
  {
    name: "name",
    label: "Name",
    type: "text" as const,
  },
  {
    name: "stockQuantity",
    label: "Stock Quantity",
    type: "number" as const,
  },
];

export default function InsertRawMaterilForm({
  setLoading,
}: {
  setLoading: (value: boolean) => void;
}) {
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

  const onSubmit = async (values: z.infer<typeof rawMaterialSchema>) => {
    setIsSubmitting(true);

    try {
      const result = await dispatch(createRawMaterial(values));

      if (result.success) {
        setLoading(true);
        await dispatch(fetchRawMaterials());
        form.reset();
        setOpen(false);
      } else {
        if (result.fieldErrors) {
          Object.entries(result.fieldErrors).forEach(([field, messages]) => {
            form.setError(field as any, {
              type: "manual",
              message: messages[0],
            });
          });
        }

        form.setError("root", {
          type: "manual",
          message: result.error,
        });
      }
    } catch (error: any) {
      form.setError("root", {
        type: "manual",
        message: error.message || "An unexpected error occurred",
      });
    } finally {
      setIsSubmitting(false);
      setLoading(false);
    }
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button variant="default" className="cursor-pointer">
          <Plus />
          New Raw Material
        </Button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-md p-0">
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)}>
            <DialogHeader className="p-6">
              <DialogTitle>Create Raw Materials</DialogTitle>
              <DialogDescription />
            </DialogHeader>

            {form.formState.errors.root && (
              <div className="mx-6 mb-4 p-3 text-sm text-red-600">
                {form.formState.errors.root.message}
              </div>
            )}

            {inputs.map((item) => (
              <FormField
                key={item.name}
                control={form.control}
                name={item.name as any}
                render={({ field }) => (
                  <FormItem className="px-6 pb-4">
                    <FormLabel htmlFor={item.name}>{item.label}</FormLabel>
                    <FormControl>
                      <Input
                        id={item.name}
                        type={item.type}
                        {...field}
                        onChange={(e) => {
                          const value =
                            item.type === "number"
                              ? Number(e.target.value)
                              : e.target.value;
                          field.onChange(value);
                        }}
                        disabled={isSubmitting}
                      />
                    </FormControl>
                    <FormMessage className="text-xs" />
                  </FormItem>
                )}
              />
            ))}

            <DialogFooter className="mt-4 px-6 py-6 bg-zinc-100">
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
