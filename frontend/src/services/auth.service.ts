import http from './http-client'
import type {
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  RefreshTokenRequest,
  AuthUserResponse,
  ChangePasswordRequest,
  CreateUserRequest,
} from '@/types/auth.types'

export interface UpdateProfileRequest {
  name?: string
  email?: string
}

export const authService = {
  login: (data: LoginRequest) => http.post<AuthResponse>('/auth/login', data),

  register: (data: RegisterRequest) => http.post<AuthResponse>('/auth/register', data),

  refreshToken: (data: RefreshTokenRequest) => http.post<TokenResponse>('/auth/refresh', data),

  logout: () => http.post('/auth/logout'),

  getProfile: () => http.get<AuthUserResponse>('/users/profile'),

  updateProfile: (data: UpdateProfileRequest) => http.put<AuthUserResponse>('/users/profile', data),

  changePassword: (data: ChangePasswordRequest) => http.put('/users/password', data),

  deleteProfile: () => http.delete('/users/profile'),

  adminGetUsers: () => http.get<AuthUserResponse[]>('/admin/users'),

  adminCreateUser: (data: CreateUserRequest) => http.post<AuthUserResponse>('/admin/users', data),
}

interface TokenResponse {
  accessToken: string
  refreshToken: string
  expiresIn: number
  tokenType: string
}
