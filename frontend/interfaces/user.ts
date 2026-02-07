export type UserRole = "ADMIN" | "USER";

export interface User {
  id: string;
  email: string;
  firstName: string;
  roles: UserRole[];
  active: boolean;
  createdAt: string;
}
