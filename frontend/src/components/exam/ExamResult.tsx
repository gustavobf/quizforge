import { useState } from 'react'
import {
  Alert,
  Box,
  Chip,
  Divider,
  Stack,
  ToggleButton,
  ToggleButtonGroup,
  Typography,
} from '@mui/material'
import CheckCircleOutline from '@mui/icons-material/CheckCircleOutline'
import HighlightOffOutlined from '@mui/icons-material/HighlightOffOutlined'
import RadioButtonUncheckedOutlined from '@mui/icons-material/RadioButtonUncheckedOutlined'
import { Stat } from '@/components/ui/Stat'
import { filterResults } from '@/utils/results'
import type { ResultData, ResultFilter } from '@/utils/results'

export default function ExamResult({ result }: { result: ResultData }) {
  const [filter, setFilter] = useState<ResultFilter>('all')
  const correct = result.questions.filter((question) => question.isCorrect).length
  const unanswered = result.questions.filter((question) => !question.answered).length
  const wrong = result.questions.length - correct - unanswered
  const questions = filterResults(result.questions, filter)
  return (
    <>
      <Box className="stat-grid" mb={4}>
        <Stat label="Aproveitamento" value={`${result.score.toFixed(1)}%`} />
        <Stat label="Certas" value={correct} color="success.main" />
        <Stat label="Erradas" value={wrong} color="error.main" />
        <Stat label="Não respondidas" value={unanswered} color="warning.main" />
      </Box>
      <Stack direction="row" flexWrap="wrap" gap={1} alignItems="center" mb={4}>
        <Typography variant="body2" color="text.secondary">
          {result.totalQuestions} perguntas
        </Typography>
        <Divider orientation="vertical" flexItem />
        <Typography variant="body2" color="text.secondary">
          {result.timeSpentInMinutes} min
        </Typography>
        <Divider orientation="vertical" flexItem />
        <Typography variant="body2" color="text.secondary">
          {new Date(result.finishedAt).toLocaleString('pt-BR')}
        </Typography>
      </Stack>
      <Divider sx={{ mb: 4 }} />
      <Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" gap={2} mb={3}>
        <Typography variant="h2" component="h2">
          Revisão das respostas
        </Typography>
        <ToggleButtonGroup
          value={filter}
          exclusive
          onChange={(_, next: ResultFilter | null) => {
            if (next) setFilter(next)
          }}
          size="small"
          aria-label="Filtrar resultados"
          sx={{ flexWrap: 'wrap', '& .MuiToggleButton-root': { px: 1.5 } }}
        >
          <ToggleButton value="all">Todas</ToggleButton>
          <ToggleButton value="correct">Certas</ToggleButton>
          <ToggleButton value="wrong">Erradas</ToggleButton>
          <ToggleButton value="unanswered">Em branco</ToggleButton>
        </ToggleButtonGroup>
      </Stack>
      {questions.length === 0 && <Alert severity="info">Nenhuma pergunta neste filtro.</Alert>}
      {questions.map((question) => {
        const color = !question.answered ? 'warning' : question.isCorrect ? 'success' : 'error'
        return (
          <Box
            key={question.number}
            component="article"
            sx={{ borderBottom: '1px solid', borderColor: 'divider', pb: 4, mb: 4 }}
          >
            <Stack direction="row" gap={2} alignItems="center" mb={2}>
              <Typography variant="overline" color="text.secondary">
                Pergunta {question.number}
              </Typography>
              <Chip
                size="small"
                color={color}
                variant="outlined"
                icon={
                  !question.answered ? (
                    <RadioButtonUncheckedOutlined />
                  ) : question.isCorrect ? (
                    <CheckCircleOutline />
                  ) : (
                    <HighlightOffOutlined />
                  )
                }
                label={
                  !question.answered ? 'Não respondida' : question.isCorrect ? 'Certa' : 'Errada'
                }
              />
            </Stack>
            <Typography
              variant="h3"
              component="h3"
              className="question-copy"
              sx={{ lineHeight: 1.7, mb: 3 }}
            >
              {question.statement}
            </Typography>
            <Box
              sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr' }, gap: 3 }}
            >
              <Box>
                <Typography variant="body2" color="text.secondary" mb={1}>
                  Sua resposta
                </Typography>
                {!question.answered ? (
                  <Typography color="warning.main">Em branco</Typography>
                ) : (
                  question.yourAnswer.map((answer, index) => (
                    <Typography key={index} className="question-copy" mb={0.5}>
                      {answer}
                    </Typography>
                  ))
                )}
              </Box>
              <Box>
                <Typography variant="body2" color="text.secondary" mb={1}>
                  Resposta correta
                </Typography>
                {question.correctAnswer.map((answer, index) => (
                  <Typography key={index} color="success.main" className="question-copy" mb={0.5}>
                    {answer}
                  </Typography>
                ))}
              </Box>
            </Box>
          </Box>
        )
      })}
    </>
  )
}
