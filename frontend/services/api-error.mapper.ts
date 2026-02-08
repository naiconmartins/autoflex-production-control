import { ApiError } from "./api.service";

export function mapApiErrorToClientMessage(error: ApiError): string {
  if (error.status === 0) {
    return "The service is temporarily unavailable. Please try again later.";
  }

  if (error.status === 401) {
    return "Invalid email or password.";
  }

  if (error.status === 403) {
    return "You are not allowed to perform this action.";
  }

  if (error.status === 404) {
    return "The requested resource was not found.";
  }

  if (error.status === 409) {
    return "This resource already exists.";
  }

  if (error.status === 422) {
    return "Some fields are invalid. Please review your input.";
  }

  if (error.status >= 500) {
    return "An unexpected server error occurred. Please try again later.";
  }

  return error.message || "An unexpected error occurred.";
}

export function extractFieldErrors(
  error: ApiError,
): Record<string, string[]> | undefined {
  if (
    error.status === 422 &&
    error.data?.errors &&
    Array.isArray(error.data.errors)
  ) {
    const fieldErrors: Record<string, string[]> = {};

    error.data.errors.forEach((err: { field: string; message: string }) => {
      if (!fieldErrors[err.field]) {
        fieldErrors[err.field] = [];
      }
      fieldErrors[err.field].push(err.message);
    });

    return fieldErrors;
  }

  return undefined;
}
