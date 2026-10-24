import http from './http-client'
import type { CreateExamResponse, FinishExamRequest, ExamResultResponse } from '@/types/exam.types'

export const examService = {
  cancelExam: (examId: number) => http.delete(`/exams/${examId}`),
  createExam: (data: { title: string; subjectId?: number; quantity: number }) =>
    http.post<CreateExamResponse>('/exams', data),

  finishExam: (examId: number, data: FinishExamRequest) =>
    http.post<ExamResultResponse>(`/exams/${examId}/finish`, data),
}
