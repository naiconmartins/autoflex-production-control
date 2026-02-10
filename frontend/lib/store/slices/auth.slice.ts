import { AuthState } from "@/interfaces/auth-state";
import { User } from "@/interfaces/user";
import { createSlice, PayloadAction } from "@reduxjs/toolkit";

const initialState: AuthState = {
  user: null,
  token: null,
  loading: false,
  error: null,
  hydrated: false,
};

type SetSessionPayload = {
  user: User;
  token: string;
};

const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    setSession: (state, action: PayloadAction<SetSessionPayload>) => {
      state.user = action.payload.user;
      state.token = action.payload.token;
      state.error = null;
      state.hydrated = true;
    },
    clearSession: (state) => {
      state.user = null;
      state.token = null;
      state.error = null;
      state.loading = false;
      state.hydrated = true;
    },
    setAuthError: (state, action: PayloadAction<string | null>) => {
      state.error = action.payload;
    },
    setHydrated: (state, action: PayloadAction<boolean>) => {
      state.hydrated = action.payload;
    },
  },
});

export const { setSession, clearSession, setAuthError, setHydrated } =
  authSlice.actions;
export default authSlice.reducer;
