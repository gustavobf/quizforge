import http from './http-client'
import type {
  HistorySummary,
  HistoryExam,
  HistoryExamDetail,
  SubjectStats,
  PageResponse,
} from '@/types/history.types'

export const historyService = {
  getSummary: () => http.get<HistorySummary>('/history/summary'),

  getExams: (page = 0, size = 10, sortBy = 'finishedAt', sortDirection = 'desc') =>
    http.get<PageResponse<HistoryExam>>('/history/exams', {
      params: { page, size, sortBy, sortDirection },
    }),

  getExamDetail: (examId: number) => http.get<HistoryExamDetail>(`/history/exams/${examId}`),

  getSubjectStats: () => http.get<SubjectStats[]>('/history/subjects'),
}
