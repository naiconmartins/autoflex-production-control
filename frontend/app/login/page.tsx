import { LoginForm } from "@/components/login-form";
import { GalleryVerticalEnd } from "lucide-react";

export default function LoginPage() {
  return (
    <div data-cy="login-page" className="grid min-h-svh lg:grid-cols-2">
      <div data-cy="login-content" className="flex flex-col gap-4 p-6 md:p-10">
        <div className="flex justify-center gap-2 md:justify-start">
          <a
            data-cy="login-logo"
            href="#"
            className="flex items-center gap-2 font-medium"
          >
            <div
              data-cy="login-logo-icon"
              className="bg-primary text-primary-foreground flex size-6 items-center justify-center rounded-md"
            >
              <GalleryVerticalEnd className="size-4" />
            </div>
            Autoflex
          </a>
        </div>

        <div className="flex flex-1 items-center justify-center">
          <div data-cy="login-form-container" className="w-full max-w-xs space-y-2">
            <LoginForm />
            <p
              data-cy="login-credentials-hint"
              className="text-xs leading-4 text-muted-foreground"
            >
              Demo access: <strong>adm@autoflex.com</strong> /{" "}
              <strong>adm</strong>
            </p>
          </div>
        </div>
      </div>

      <div
        data-cy="login-image-wrapper"
        className="bg-muted relative hidden lg:block"
      >
        <img
          data-cy="login-image"
          src="/130861.avif"
          alt="Image"
          width={667}
          height={1000}
          className="absolute inset-0 h-full w-full object-cover dark:brightness-[0.2] dark:grayscale"
        />
      </div>
    </div>
  );
}
