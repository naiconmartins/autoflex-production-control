export type ActionSuccess<T> = {
  success: true;
  data: T;
};

export type ActionError = {
  success: false;
  status: number;
  error: string;
  fieldErrors?: Record<string, string[]>;
};

export type ActionResult<T> = ActionSuccess<T> | ActionError;
