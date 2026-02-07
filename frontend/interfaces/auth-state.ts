import { User } from "./user";

export interface AuthState {
  user: User | null;
  token: string | null;
  loading: boolean;
  error: string | null;
  hydrated: boolean;
}
