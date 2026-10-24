import type { CreateExamResponse, ExamResultResponse } from '@/types/exam.types'

export const exam: CreateExamResponse = {
  examId: 42,
  title: 'Revisão de Java',
  subjectName: 'Java',
  totalQuestions: 3,
  questions: [1, 2, 3].map((number) => ({
    questionId: number,
    orderNumber: number,
    statement: `Enunciado da pergunta ${number}`,
    questionType: number === 2 ? 'MULTIPLE_CHOICE' : 'SINGLE_CHOICE',
    alternatives: [
      { alternativeId: number * 10, description: 'Resposta A' },
      { alternativeId: number * 10 + 1, description: 'Resposta B' },
    ],
  })),
}
export const result: ExamResultResponse = {
  examId: 42,
  title: exam.title,
  subjectName: 'Java',
  totalQuestions: 3,
  correctAnswers: 1,
  wrongAnswers: 1,
  unansweredQuestions: 1,
  score: 100 / 3,
  startedAt: '2026-10-23T10:00:00',
  finishedAt: '2026-10-23T10:05:00',
  timeSpentInMinutes: 5,
  questions: [
    {
      number: 1,
      statement: 'Pergunta certa',
      yourAnswer: ['Resposta A'],
      correctAnswer: ['Resposta A'],
      isCorrect: true,
      answered: true,
      questionType: 'SINGLE_CHOICE',
    },
    {
      number: 2,
      statement: 'Pergunta errada',
      yourAnswer: ['Resposta B'],
      correctAnswer: ['Resposta A'],
      isCorrect: false,
      answered: true,
      questionType: 'MULTIPLE_CHOICE',
    },
    {
      number: 3,
      statement: 'Pergunta em branco',
      yourAnswer: ['Not answered'],
      correctAnswer: ['Resposta A'],
      isCorrect: false,
      answered: false,
      questionType: 'SINGLE_CHOICE',
    },
  ],
}
