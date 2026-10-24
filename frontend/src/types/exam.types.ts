export interface Alternative {
  alternativeId: number
  description: string
}

export interface Question {
  questionId: number
  statement: string
  alternatives: Alternative[]
  orderNumber: number
  questionType: 'SINGLE_CHOICE' | 'MULTIPLE_CHOICE'
}

export interface CreateExamResponse {
  examId: number
  title: string
  totalQuestions: number
  subjectName: string | null
  questions: Question[]
}

export interface FinishExamRequest {
  answers: Record<number, number[]>
}

export interface ExamResultResponse {
  examId: number
  title: string
  subjectName: string | null
  totalQuestions: number
  correctAnswers: number
  wrongAnswers: number
  unansweredQuestions: number
  score: number
  startedAt: string
  finishedAt: string
  timeSpentInMinutes: number
  questions: {
    number: number
    statement: string
    yourAnswer: string[]
    correctAnswer: string[]
    isCorrect: boolean
    answered: boolean
    questionType: string
  }[]
}
