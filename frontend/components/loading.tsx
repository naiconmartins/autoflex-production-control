import { Spinner } from "./ui/spinner";

export default function Loading() {
  return (
    <div className="text-muted-foreground flex flex-col items-center justify-center gap-2">
      <Spinner />
      Loading
    </div>
  );
}
