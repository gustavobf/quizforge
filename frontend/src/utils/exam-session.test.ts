import { describe, expect, it } from 'vitest'
import { clearExamSession, readExamSession, saveExamSession, toggleAnswer } from './exam-session'
import { exam } from '@/test/fixtures'

describe('exam session', () => {
  it('restores answers and current question for the same user only', () => {
    saveExamSession(7, { exam, answers: { 1: [10], 2: [20, 21] }, index: 1 })
    expect(readExamSession(7)).toMatchObject({ answers: { 1: [10], 2: [20, 21] }, index: 1 })
    expect(readExamSession(8)).toBeNull()
    clearExamSession(7)
    expect(readExamSession(7)).toBeNull()
  })
  it('rejects malformed storage and clamps an out-of-range position', () => {
    localStorage.setItem('quizforge:exam:7', '{broken')
    expect(readExamSession(7)).toBeNull()
    localStorage.setItem(
      'quizforge:exam:7',
      JSON.stringify({ exam: { questions: [null] }, answers: {} }),
    )
    expect(readExamSession(7)).toBeNull()
    saveExamSession(7, { exam, answers: { 1: [10, 11, 999], 2: [20, 20, 21, 999] }, index: 99 })
    expect(readExamSession(7)).toMatchObject({ index: 2, answers: { 1: [10], 2: [20, 21] } })
  })
  it('replaces single answers, toggles multiple answers and allows clearing', () => {
    expect(toggleAnswer([10], 11, false)).toEqual([11])
    expect(toggleAnswer([10], 10, false)).toEqual([])
    expect(toggleAnswer([20], 21, true)).toEqual([20, 21])
    expect(toggleAnswer([20, 21], 20, true)).toEqual([21])
  })
})
