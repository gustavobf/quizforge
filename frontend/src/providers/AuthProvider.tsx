import { useEffect, useState } from 'react'
import type { ReactNode } from 'react'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import { AuthContext } from '@/contexts/auth-context'
import { authService } from '@/services/auth.service'
import { clearAuthStorage } from '@/services/http-client'
import type { AuthResponse } from '@/types/auth.types'

export function AuthProvider({ children }: { children: ReactNode }) {
  const queryClient = useQueryClient()
  const [authenticated, setAuthenticated] = useState(() => !!localStorage.getItem('accessToken'))
  const [notice, setNotice] = useState('')
  const profile = useQuery({
    queryKey: ['profile'],
    queryFn: async () => (await authService.getProfile()).data,
    enabled: authenticated,
    retry: false,
  })
  useEffect(() => {
    function expired(event: Event) {
      const message: unknown = (event as CustomEvent).detail
      setNotice(typeof message === 'string' ? message : '')
      setAuthenticated(false)
      queryClient.clear()
    }
    function storageChanged(event: StorageEvent) {
      if (event.key === 'accessToken' || event.key === null) {
        setAuthenticated(!!localStorage.getItem('accessToken'))
        queryClient.clear()
      }
    }
    window.addEventListener('auth:expired', expired)
    window.addEventListener('storage', storageChanged)
    return () => {
      window.removeEventListener('auth:expired', expired)
      window.removeEventListener('storage', storageChanged)
    }
  }, [queryClient])
  function signIn(response: AuthResponse) {
    setNotice('')
    queryClient.clear()
    localStorage.setItem('accessToken', response.token.accessToken)
    localStorage.setItem('refreshToken', response.token.refreshToken)
    localStorage.setItem('user', JSON.stringify(response.user))
    queryClient.setQueryData(['profile'], response.user)
    setAuthenticated(true)
  }
  async function signOut() {
    try {
      await authService.logout()
    } finally {
      clearAuthStorage()
    }
  }
  return (
    <AuthContext.Provider
      value={{
        authenticated,
        notice,
        user: profile.data,
        loading: authenticated && profile.isPending,
        error: profile.error,
        retry: () => {
          void profile.refetch()
        },
        signIn,
        signOut,
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}
