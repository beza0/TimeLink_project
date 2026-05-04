type GoogleTokenResponse = {
  access_token?: string;
  error?: string;
  error_description?: string;
};

declare global {
  interface Window {
    google?: {
      accounts?: {
        oauth2?: {
          initTokenClient: (config: {
            client_id: string;
            scope: string;
            prompt?: string;
            callback: (response: GoogleTokenResponse) => void;
          }) => { requestAccessToken: () => void };
        };
      };
    };
    FB?: {
      init: (config: {
        appId: string;
        cookie: boolean;
        xfbml: boolean;
        version: string;
      }) => void;
      login: (
        callback: (response: {
          status: string;
          authResponse?: { accessToken?: string };
        }) => void,
        opts: { scope: string },
      ) => void;
    };
    fbAsyncInit?: () => void;
  }
}

function loadScriptOnce(id: string, src: string): Promise<void> {
  return new Promise((resolve, reject) => {
    const existing = document.getElementById(id) as HTMLScriptElement | null;
    if (existing) {
      if (existing.dataset.loaded === "true") {
        resolve();
        return;
      }
      existing.addEventListener("load", () => resolve(), { once: true });
      existing.addEventListener("error", () => reject(new Error("Script load failed")), {
        once: true,
      });
      return;
    }
    const s = document.createElement("script");
    s.id = id;
    s.src = src;
    s.async = true;
    s.defer = true;
    s.onload = () => {
      s.dataset.loaded = "true";
      resolve();
    };
    s.onerror = () => reject(new Error("Script load failed"));
    document.head.appendChild(s);
  });
}

export async function beginGoogleLogin(): Promise<string> {
  const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID as string | undefined;
  if (!clientId || !clientId.trim()) {
    throw new Error("Google sign-in is not configured");
  }
  await loadScriptOnce("google-gsi-sdk", "https://accounts.google.com/gsi/client");
  const initTokenClient = window.google?.accounts?.oauth2?.initTokenClient;
  if (!initTokenClient) {
    throw new Error("Google sign-in is unavailable");
  }
  return new Promise((resolve, reject) => {
    const client = initTokenClient({
      client_id: clientId,
      scope: "openid email profile",
      prompt: "select_account",
      callback: (response) => {
        if (response.error || !response.access_token) {
          reject(new Error(response.error_description || "Google login failed"));
          return;
        }
        resolve(response.access_token);
      },
    });
    client.requestAccessToken();
  });
}

export async function beginFacebookLogin(): Promise<string> {
  const appId = import.meta.env.VITE_FACEBOOK_APP_ID as string | undefined;
  if (!appId || !appId.trim()) {
    throw new Error("Facebook sign-in is not configured");
  }
  await loadScriptOnce("facebook-sdk", "https://connect.facebook.net/en_US/sdk.js");
  if (!window.FB) {
    throw new Error("Facebook sign-in is unavailable");
  }
  window.FB.init({
    appId,
    cookie: true,
    xfbml: false,
    version: "v19.0",
  });
  return new Promise((resolve, reject) => {
    window.FB?.login(
      (response) => {
        const token = response.authResponse?.accessToken;
        if (response.status !== "connected" || !token) {
          reject(new Error("Facebook login failed"));
          return;
        }
        resolve(token);
      },
      { scope: "public_profile,email" },
    );
  });
}
