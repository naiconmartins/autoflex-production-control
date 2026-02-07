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

  if (error.status === 422) {
    return "Some fields are invalid. Please review your input.";
  }

  if (error.status >= 500) {
    return "An unexpected server error occurred. Please try again later.";
  }

  return error.message || "An unexpected error occurred.";
}
