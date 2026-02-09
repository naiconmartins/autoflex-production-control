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
import { RowDataProps } from "@/interfaces/row-data";
import { useAppDispatch } from "@/store/hooks";
import { deleteProduct, fetchProducts } from "@/store/thunks/product.thunks";
import { Trash2 } from "lucide-react";
import { useState } from "react";

export default function DeleteProduct<TData>({ row }: RowDataProps<TData>) {
  const dispatch = useAppDispatch();
  const [open, setOpen] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string>();

  const onSubmit = async () => {
    setIsSubmitting(true);

    try {
      const id = row.getValue("id");
      const result = await dispatch(deleteProduct(id as string));

      if (result.success) {
        await dispatch(fetchProducts());
        setOpen(false);
      } else {
        setError(true);
        setErrorMessage(result.error);
      }
    } catch (err: any) {
      setError(true);
      setErrorMessage(err?.message || "Erro inesperado");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Trash2 className="text-rose-500 w-4 h-4 cursor-pointer" />
      </DialogTrigger>

      <DialogContent className="sm:max-w-md p-0">
        <DialogHeader className="pt-6 px-6 flex flex-row items-center">
          <DialogTitle>Are you absolutely sure?</DialogTitle>
        </DialogHeader>
        <DialogDescription className="px-6 text-base">
          This action cannot be undone. It will permanently delete this product.
        </DialogDescription>

        {error && (
          <div className="mx-6 mb-4 text-sm text-red-600">{errorMessage}</div>
        )}

        <DialogFooter className="px-6 py-6 bg-zinc-100">
          <Button
            type="button"
            variant="outline"
            onClick={() => setOpen(false)}
            disabled={isSubmitting}
          >
            Cancel
          </Button>
          <Button
            variant="destructive"
            disabled={isSubmitting}
            onClick={onSubmit}
          >
            {isSubmitting ? "Deleting..." : "Continue"}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
