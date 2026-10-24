import axios from 'axios'
import type { ApiError } from '@/types/auth.types'

export function errorMessage(error: unknown): string {
  if (axios.isAxiosError<ApiError>(error)) {
    if (!error.response) return 'Não foi possível conectar ao servidor. Tente novamente.'
    if (error.response.status === 403) return 'Você não tem permissão para realizar esta ação.'
    const data = error.response.data
    const fields = data.validationErrors
      ? Object.values(data.validationErrors).join(' ')
      : undefined
    return fields || data.message || 'Não foi possível concluir a solicitação.'
  }
  return error instanceof Error ? error.message : 'Ocorreu um erro inesperado.'
}
