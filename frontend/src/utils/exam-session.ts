import type { CreateExamResponse } from '@/types/exam.types'

export interface ExamSession {
  exam: CreateExamResponse
  answers: Record<number, number[]>
  index: number
}
const key = (userId: number) => `quizforge:exam:${userId}`
const record = (value: unknown): value is Record<string, unknown> =>
  typeof value === 'object' && value !== null

export function saveExamSession(userId: number, session: ExamSession) {
  localStorage.setItem(key(userId), JSON.stringify(session))
}
export function clearExamSession(userId: number) {
  localStorage.removeItem(key(userId))
}

export function readExamSession(userId: number): ExamSession | null {
  try {
    const value: unknown = JSON.parse(localStorage.getItem(key(userId)) ?? 'null')
    if (!record(value) || !record(value.exam) || !record(value.answers)) return null
    const exam = value.exam
    if (
      !Number.isInteger(exam.examId) ||
      typeof exam.title !== 'string' ||
      !Array.isArray(exam.questions) ||
      exam.questions.length === 0 ||
      exam.questions.length !== exam.totalQuestions
    )
      return null
    for (const question of exam.questions) {
      if (
        !record(question) ||
        !Number.isInteger(question.questionId) ||
        typeof question.statement !== 'string' ||
        !['SINGLE_CHOICE', 'MULTIPLE_CHOICE'].includes(String(question.questionType)) ||
        !Array.isArray(question.alternatives) ||
        !question.alternatives.every(
          (alternative: unknown) =>
            record(alternative) &&
            Number.isInteger(alternative.alternativeId) &&
            typeof alternative.description === 'string',
        )
      )
        return null
    }
    const data = exam as unknown as CreateExamResponse
    const answers: Record<number, number[]> = {}
    for (const question of data.questions) {
      const selected = value.answers[question.questionId]
      if (!Array.isArray(selected)) continue
      const valid = [
        ...new Set(
          selected.filter(
            (id): id is number =>
              typeof id === 'number' &&
              question.alternatives.some((alternative) => alternative.alternativeId === id),
          ),
        ),
      ]
      answers[question.questionId] =
        question.questionType === 'SINGLE_CHOICE' ? valid.slice(0, 1) : valid
    }
    const index =
      typeof value.index === 'number' && Number.isInteger(value.index)
        ? Math.max(0, Math.min(value.index, data.questions.length - 1))
        : 0
    return { exam: data, answers, index }
  } catch {
    return null
  }
}

export function toggleAnswer(
  selected: number[],
  alternativeId: number,
  multiple: boolean,
): number[] {
  if (selected.includes(alternativeId)) return selected.filter((id) => id !== alternativeId)
  return multiple ? [...selected, alternativeId] : [alternativeId]
}
