import type { ExamResultResponse } from '@/types/exam.types'
import type { HistoryExamDetail } from '@/types/history.types'

export type ResultData = ExamResultResponse | HistoryExamDetail
export type ResultFilter = 'all' | 'correct' | 'wrong' | 'unanswered'
export function filterResults(questions: ResultData['questions'], filter: ResultFilter) {
  return questions.filter(
    (question) =>
      filter === 'all' ||
      (filter === 'correct' && question.isCorrect) ||
      (filter === 'wrong' && question.answered && !question.isCorrect) ||
      (filter === 'unanswered' && !question.answered),
  )
}
