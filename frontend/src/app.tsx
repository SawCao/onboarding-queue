import type { RequestConfig } from 'umi';

const ADMIN_TOKEN_KEY = 'onboarding_admin_token';

export const request: RequestConfig = {
  timeout: 10000,
  headers: () => {
    const token = localStorage.getItem(ADMIN_TOKEN_KEY);
    return token ? { 'X-Admin-Token': token } : {};
  },
};

export function setAdminToken(token: string) {
  localStorage.setItem(ADMIN_TOKEN_KEY, token);
}

export function clearAdminToken() {
  localStorage.removeItem(ADMIN_TOKEN_KEY);
}

export function getAdminToken(): string | null {
  return localStorage.getItem(ADMIN_TOKEN_KEY);
}
