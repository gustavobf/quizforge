import { expect, test } from '@playwright/test'
import type { Page } from '@playwright/test'
import { exam, result } from '../src/test/fixtures'

async function mockApi(page: Page) {
  await page.addInitScript(() => {
    localStorage.setItem('accessToken', 'test-access-token')
    localStorage.setItem('refreshToken', 'test-refresh-token')
  })
  await page.route('**/api/**', (route) => {
    const request = route.request()
    const path = new URL(request.url()).pathname.replace('/api', '')
    let data: unknown
    if (path === '/users/profile')
      data = { id: 7, name: 'Ana Silva', email: 'ana@example.test', role: 'USER' }
    else if (path === '/subjects')
      data = [{ id: 1, name: 'Java', description: 'Fundamentos da linguagem' }]
    else if (path === '/history/summary')
      data = {
        totalExams: 0,
        totalQuestionsAnswered: 0,
        averageScore: 0,
        bestScore: 0,
        worstScore: 0,
        recentExams: [],
      }
    else if (path === '/history/subjects')
      data = [
        {
          subjectId: 0,
          subjectName: 'Todas as materias',
          totalExams: 1,
          totalQuestions: 3,
          correctAnswers: 1,
          averageScore: 100 / 3,
          bestScore: 100 / 3,
        },
      ]
    else if (path === '/history/exams/42' || path === '/exams/42/finish') data = result
    else if (path === '/history/exams') data = { content: [], totalElements: 0, totalPages: 0 }
    else if (path === '/exams') data = exam
    else if (path === '/exams/42' && request.method() === 'DELETE')
      return route.fulfill({ status: 204 })
    else return route.fulfill({ status: 404, json: { message: 'Unexpected test request' } })
    return route.fulfill({ json: data })
  })
}

async function expectNoOverflow(page: Page) {
  expect(
    await page.evaluate(
      () => document.documentElement.scrollWidth <= document.documentElement.clientWidth,
    ),
  ).toBe(true)
}

test('home redirects to login without a token', async ({ page }, testInfo) => {
  await page.goto('/')
  await expect(page).toHaveURL(/\/login$/)
  await expect(page.getByRole('heading', { name: 'Acesse sua conta' })).toBeVisible()
  await expectNoOverflow(page)
  await page.screenshot({ path: testInfo.outputPath('login.png'), fullPage: true })
})

test('displays accented interface labels without altering API data', async ({ page }) => {
  await mockApi(page)
  await page.goto('/dashboard')
  await expect(page.getByRole('heading', { name: 'Visão geral' })).toBeVisible()
  await expect(page.getByRole('table', { name: 'Desempenho por matéria' })).toBeVisible()
  await expect(page.getByRole('cell', { name: 'Todas as matérias', exact: true })).toBeVisible()
  await expect(page.getByRole('columnheader', { name: 'Média', exact: true })).toBeVisible()
})

test('navigates, collapses the question panel and restores answers after reload', async ({
  page,
}, testInfo) => {
  await mockApi(page)
  await page.addInitScript((data) => {
    if (!sessionStorage.getItem('exam-seeded')) {
      localStorage.setItem(
        'quizforge:exam:7',
        JSON.stringify({ exam: data, answers: {}, index: 0 }),
      )
      sessionStorage.setItem('exam-seeded', 'true')
    }
  }, exam)
  await page.goto('/exam/42')
  await expect(page.getByRole('heading', { name: exam.title })).toBeVisible()
  const navigationToggle = page.getByRole('button', { name: 'Abrir navegação' })
  const importLink = page.getByRole('link', { name: 'Importar perguntas' })
  await expect(importLink).toBeHidden()
  await expect(navigationToggle).toBeVisible()
  await expect(navigationToggle).toHaveAttribute('aria-expanded', 'false')
  await expect(page.locator('footer')).toHaveCSS('left', '0px')
  await navigationToggle.click()
  await expect(importLink).toBeVisible()
  await expectNoOverflow(page)
  await page.screenshot({
    path: testInfo.outputPath('exam-navigation.png'),
    animations: 'disabled',
  })
  await page.getByRole('button', { name: 'Fechar navegação' }).click()
  await expect(importLink).toBeHidden()
  await expect(navigationToggle).toHaveAttribute('aria-expanded', 'false')
  await navigationToggle.click()
  await expect(importLink).toBeVisible()
  await page.keyboard.press('Escape')
  await expect(importLink).toBeHidden()
  const finish = page.getByRole('button', { name: 'Finalizar', exact: true })
  await expect(finish).toBeInViewport()
  await page.getByRole('radio', { name: /Resposta A/ }).click()
  await expect(page.getByRole('button', { name: /Ir para pergunta 2/ })).toBeHidden()
  await page.getByRole('button', { name: /^Perguntas/ }).click()
  await page.getByRole('button', { name: 'Ir para pergunta 2, em branco', exact: true }).click()
  await page.getByRole('checkbox', { name: /Resposta A/ }).check()
  await page.getByRole('checkbox', { name: /Resposta B/ }).check()
  await page.reload()
  await expect(page.getByRole('checkbox', { name: /Resposta A/ })).toBeChecked()
  await expect(page.getByRole('checkbox', { name: /Resposta B/ })).toBeChecked()
  await expect(page.getByRole('button', { name: /Ir para pergunta 1/ })).toBeHidden()
  await page.getByRole('button', { name: /^Perguntas/ }).click()
  await expect(page.getByRole('button', { name: /Ir para pergunta 1/ })).toBeVisible()
  await page.getByRole('button', { name: /^Perguntas/ }).click()
  await expect(page.getByRole('button', { name: /Ir para pergunta 1/ })).toBeHidden()
  await expect(finish).toBeInViewport()
  await page.getByRole('button', { name: /^Perguntas/ }).click()
  await expect(page.getByRole('button', { name: /Ir para pergunta 1/ })).toBeVisible()
  await expect(page.locator('.MuiCollapse-root')).toHaveClass(/MuiCollapse-entered/)
  await expectNoOverflow(page)
  await page.screenshot({
    path: testInfo.outputPath('exam.png'),
    fullPage: true,
    animations: 'disabled',
  })
  await finish.click()
  await expect(page.getByRole('dialog')).toContainText('2 respondidas e 1 em branco')
  await page.getByRole('button', { name: 'Confirmar finalização' }).click()
  await expect(page).toHaveURL(/\/history\/42$/)
  if (testInfo.project.name === 'desktop') {
    await expect(importLink).toBeVisible()
    await expect(navigationToggle).toBeHidden()
  }
  await expect(page.getByText('Não respondidas', { exact: true })).toBeVisible()
  await page.getByRole('button', { name: 'Erradas', exact: true }).click()
  await expect(page.getByRole('article')).toHaveCount(1)
  await expect(page.getByText('Pergunta errada')).toBeVisible()
  await page.getByRole('button', { name: 'Em branco', exact: true }).click()
  await expect(page.getByRole('article')).toHaveCount(1)
  await expect(page.getByText('Pergunta em branco')).toBeVisible()
  await page.getByRole('button', { name: 'Todas', exact: true }).click()
  await expect(page.getByRole('article')).toHaveCount(3)
  await expectNoOverflow(page)
  await page.screenshot({ path: testInfo.outputPath('results.png'), fullPage: true })
})

test('keeps a long five-option question compact and vertically centers its alternatives', async ({
  page,
}, testInfo) => {
  if (testInfo.project.name === 'desktop') await page.setViewportSize({ width: 1366, height: 768 })
  await mockApi(page)
  const descriptions = [
    'Upgrade to Copilot Business.',
    'Add a .gitignore file to the repo.',
    'Have an organization owner configure content exclusions.',
    'Have a repo administrator configure content exclusions.',
    'Use the #file Chat variable to exclude the files.',
  ]
  const compactExam = {
    ...exam,
    title: 'teste',
    subjectName: 'GH300',
    totalQuestions: 19,
    questions: Array.from({ length: 19 }, (_, index) => ({
      questionId: index + 1,
      orderNumber: index + 1,
      questionType: 'SINGLE_CHOICE',
      statement:
        'A team is using GitHub Copilot Individual in their daily development activities. They need to exclude specific files from being used to inform code completion suggestions. How can they achieve this?',
      alternatives: descriptions.map((description, alternativeIndex) => ({
        alternativeId: (index + 1) * 10 + alternativeIndex,
        description,
      })),
    })),
  }
  await page.addInitScript((data) => {
    localStorage.setItem(
      'quizforge:exam:7',
      JSON.stringify({ exam: data, answers: { 1: [10] }, index: 1 }),
    )
  }, compactExam)
  await page.goto('/exam/42')
  await expect(page.getByRole('heading', { name: 'teste', exact: true })).toBeVisible()
  await expect(page.getByText('1 de 19 respondidas', { exact: true })).toBeVisible()
  await expect(page.getByRole('button', { name: /Ir para pergunta 1,/ })).toBeHidden()
  const alternatives = page.getByRole('group', { name: 'Alternativas' }).locator('label')
  await expect(alternatives).toHaveCount(5)
  const offsets = await alternatives.evaluateAll((labels) =>
    labels.map((label) => {
      const bounds = label.getBoundingClientRect()
      const center = bounds.top + bounds.height / 2
      const control = label.querySelector('.MuiRadio-root')!.getBoundingClientRect()
      const text = label.querySelector('.question-copy')!.getBoundingClientRect()
      return {
        control: Math.abs(control.top + control.height / 2 - center),
        text: Math.abs(text.top + text.height / 2 - center),
      }
    }),
  )
  for (const offset of offsets) {
    expect(offset.control).toBeLessThanOrEqual(1)
    expect(offset.text).toBeLessThanOrEqual(1)
  }
  if (testInfo.project.name === 'desktop') {
    await expect(alternatives.last()).toBeInViewport({ ratio: 1 })
    const lastAlternative = await alternatives.last().boundingBox()
    const footer = await page.locator('footer').boundingBox()
    expect(lastAlternative!.y + lastAlternative!.height).toBeLessThanOrEqual(footer!.y - 8)
    expect(
      await page.evaluate(() => document.documentElement.scrollHeight <= window.innerHeight),
    ).toBe(true)
  }
  await expect(page.getByRole('button', { name: 'Finalizar', exact: true })).toBeInViewport()
  await expectNoOverflow(page)
  await page.screenshot({
    path: testInfo.outputPath('compact-question.png'),
    fullPage: true,
    animations: 'disabled',
  })
})

test('cancels through DELETE without sending answers or finishing the attempt', async ({
  page,
}) => {
  await mockApi(page)
  let finishes = 0
  let cancellations = 0
  page.on('request', (request) => {
    if (request.url().endsWith('/exams/42/finish')) finishes++
    if (request.url().endsWith('/exams/42') && request.method() === 'DELETE') cancellations++
  })
  await page.goto('/')
  await page.getByRole('textbox', { name: 'Nome do simulado' }).fill('Revisão')
  await page.getByRole('spinbutton', { name: 'Quantidade de perguntas' }).fill('3')
  await page.getByRole('button', { name: 'Iniciar simulado' }).click()
  await expect(page).toHaveURL(/\/exam\/42$/)
  await page.getByRole('button', { name: 'Cancelar', exact: true }).click()
  await expect(page.getByRole('dialog')).toContainText('não entrará nas suas estatísticas')
  await page.getByRole('button', { name: 'Cancelar simulado', exact: true }).click()
  await expect(page).toHaveURL(/\/$/)
  expect(cancellations).toBe(1)
  expect(finishes).toBe(0)
  expect(await page.evaluate(() => localStorage.getItem('quizforge:exam:7'))).toBeNull()
})
