import axios from 'axios'
import type { InternalAxiosRequestConfig } from 'axios'
import type { TokenResponse } from '@/types/auth.types'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
  timeout: 30000,
})

let refreshPromise: Promise<string> | null = null

export function clearAuthStorage(notice = '') {
  for (const key of ['accessToken', 'refreshToken', 'user', 'expiresIn', 'examData'])
    localStorage.removeItem(key)
  window.dispatchEvent(new CustomEvent('auth:expired', { detail: notice }))
}

async function refreshAccessToken(): Promise<string> {
  const refreshToken = localStorage.getItem('refreshToken')
  if (!refreshToken) throw new Error('Sua sessão expirou. Entre novamente.')
  const { data } = await axios.post<TokenResponse>(
    `${http.defaults.baseURL}/auth/refresh`,
    { refreshToken },
    { timeout: 30_000 },
  )
  if (localStorage.getItem('refreshToken') !== refreshToken)
    throw new Error('A sessão foi alterada.')
  localStorage.setItem('accessToken', data.accessToken)
  localStorage.setItem('refreshToken', data.refreshToken)
  return data.accessToken
}

http.interceptors.request.use(
  (config) => {
    const accessToken = localStorage.getItem('accessToken')
    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`
    }
    return config
  },
  (error) => Promise.reject(error),
)

http.interceptors.response.use(
  (response) => response,
  async (error) => {
    const request = error.config as (InternalAxiosRequestConfig & { _retry?: boolean }) | undefined
    const publicRequest = /\/auth\/(login|register|refresh)$/.test(request?.url ?? '')
    if (error.response?.status !== 401 || !request || publicRequest) return Promise.reject(error)
    if (request._retry) {
      clearAuthStorage()
      return Promise.reject(error)
    }
    request._retry = true
    try {
      refreshPromise ??= refreshAccessToken().finally(() => {
        refreshPromise = null
      })
      const token = await refreshPromise
      request.headers.Authorization = `Bearer ${token}`
      return await http(request)
    } catch (refreshError) {
      clearAuthStorage()
      return Promise.reject(refreshError)
    }
  },
)

export default http
