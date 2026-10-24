import { render, screen, within } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { describe, expect, it } from 'vitest'
import ExamResult from './ExamResult'
import { result } from '@/test/fixtures'

describe('exam result', () => {
  it('shows unanswered totals and filters correct, wrong, blank and all', async () => {
    const user = userEvent.setup()
    render(<ExamResult result={result} />)
    expect(screen.getByText('Não respondidas').parentElement).toHaveTextContent('1')
    expect(screen.getAllByRole('article')).toHaveLength(3)
    const filters = screen.getByRole('group', { name: 'Filtrar resultados' })
    await user.click(within(filters).getByRole('button', { name: 'Erradas' }))
    expect(screen.getAllByRole('article')).toHaveLength(1)
    expect(screen.getByText('Pergunta errada')).toBeVisible()
    expect(screen.queryByText('Pergunta em branco')).not.toBeInTheDocument()
    await user.click(within(filters).getByRole('button', { name: 'Certas' }))
    expect(screen.getByText('Pergunta certa')).toBeVisible()
    await user.click(within(filters).getByRole('button', { name: 'Em branco' }))
    expect(screen.getByText('Pergunta em branco')).toBeVisible()
    await user.click(within(filters).getByRole('button', { name: 'Todas' }))
    expect(screen.getAllByRole('article')).toHaveLength(3)
  })
  it('displays an empty state when the selected filter has no matches', async () => {
    render(<ExamResult result={{ ...result, questions: [result.questions[0]!] }} />)
    await userEvent.click(screen.getByRole('button', { name: 'Erradas' }))
    expect(screen.getByText('Nenhuma pergunta neste filtro.')).toBeVisible()
  })
})
