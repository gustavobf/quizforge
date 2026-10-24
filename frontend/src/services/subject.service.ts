import http from './http-client'
import type { Subject, CreateSubjectRequest } from '@/types/subject.types'

export const subjectService = {
  getAll: () => http.get<Subject[]>('/subjects'),

  getById: (id: number) => http.get<Subject>(`/subjects/${id}`),

  create: (data: CreateSubjectRequest) => http.post<Subject>('/subjects', data),

  update: (id: number, data: CreateSubjectRequest) => http.put<Subject>(`/subjects/${id}`, data),

  delete: (id: number) => http.delete(`/subjects/${id}`),
}
