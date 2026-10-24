import { createContext } from 'react'
import type { AuthResponse, AuthUserResponse } from '@/types/auth.types'

export interface AuthContextValue {
  authenticated: boolean
  notice: string
  user: AuthUserResponse | undefined
  loading: boolean
  error: Error | null
  retry: () => void
  signIn: (response: AuthResponse) => void
  signOut: () => Promise<void>
}

export const AuthContext = createContext<AuthContextValue | null>(null)
