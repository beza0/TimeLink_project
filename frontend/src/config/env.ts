/** Backend base URL (Spring Boot default: http://localhost:8080). No trailing slash. */
export function getApiBaseUrl(): string {
  const raw = import.meta.env.VITE_API_BASE_URL as string | undefined;
  if (raw && raw.trim()) {
    return raw.replace(/\/$/, "");
  }
  return "http://localhost:8080";
}
