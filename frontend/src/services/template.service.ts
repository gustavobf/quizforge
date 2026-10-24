import http from './http-client'

export const templateService = {
  downloadTemplate: () => http.get('/template', { responseType: 'blob' }),
}
