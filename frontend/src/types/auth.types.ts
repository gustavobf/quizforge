export type Role = 'USER' | 'ADMIN'

export interface AuthUserResponse {
  id: number
  name: string
  email: string
  role: Role
}

export interface TokenResponse {
  accessToken: string
  refreshToken: string
  expiresIn: number
  tokenType: 'Bearer'
}

export interface AuthResponse {
  user: AuthUserResponse
  token: TokenResponse
}

export interface ApiError {
  timestamp: string
  status: number
  error: string
  message: string
  details?: string
  errors?: string[]
  validationErrors?: Record<string, string>
}

export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  name: string
  email: string
  password: string
}

export interface RefreshTokenRequest {
  refreshToken: string
}

export interface ChangePasswordRequest {
  currentPassword: string
  newPassword: string
}

export interface CreateUserRequest {
  name: string
  email: string
  password: string
  role: Role
}
