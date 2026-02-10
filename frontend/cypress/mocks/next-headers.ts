type CookieValue = {
  name: string;
  value: string;
};

type CookieStore = {
  get: (name: string) => CookieValue | undefined;
  set: (name: string, value: string) => void;
  delete: (name: string) => void;
};

const memoryCookies = new Map<string, string>();

export async function cookies(): Promise<CookieStore> {
  return {
    get: (name: string) => {
      const value = memoryCookies.get(name);
      return value === undefined ? undefined : { name, value };
    },
    set: (name: string, value: string) => {
      memoryCookies.set(name, value);
    },
    delete: (name: string) => {
      memoryCookies.delete(name);
    },
  };
}
