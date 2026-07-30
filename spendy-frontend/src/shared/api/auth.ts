import client from './client'

export interface LoginRequest {
  username: string
  password: string
}

export interface AuthResponse {
  token: string
}

export function login(data: LoginRequest) {
  return client.post<AuthResponse>('/auth/login', data)
}

export function refresh(refreshToken: string) {
  return client.post<AuthResponse>('/auth/refresh', { refreshToken })
}

export function logout(refreshToken: string) {
  return client.post('/auth/logout', { refreshToken })
}
