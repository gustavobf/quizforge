import http from './http-client'
import type { ImportQuestionsResponse } from '@/types/question.types'

export const questionService = {
  importQuestions: (file: File, subjectId: number) => {
    const formData = new FormData()
    formData.append('file', file)
    return http.post<ImportQuestionsResponse>('/questions/import', formData, {
      params: { subjectId },
    })
  },
}
