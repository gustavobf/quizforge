import { useRef, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Alert,
  Box,
  Button,
  Checkbox,
  Chip,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  FormControlLabel,
  IconButton,
  LinearProgress,
  Radio,
  Stack,
  Tooltip,
  Typography,
} from '@mui/material'
import ExpandMoreOutlined from '@mui/icons-material/ExpandMoreOutlined'
import CheckOutlined from '@mui/icons-material/CheckOutlined'
import CloseOutlined from '@mui/icons-material/CloseOutlined'
import ArrowBackOutlined from '@mui/icons-material/ArrowBackOutlined'
import ArrowForwardOutlined from '@mui/icons-material/ArrowForwardOutlined'
import { useAuth } from '@/hooks/use-auth'
import { Page } from '@/components/ui/Page'
import { ErrorState } from '@/components/ui/ErrorState'
import { examService } from '@/services/exam.service'
import {
  clearExamSession,
  readExamSession,
  saveExamSession,
  toggleAnswer,
} from '@/utils/exam-session'
import type { ExamSession } from '@/utils/exam-session'

export default function ExamPage() {
  const { user } = useAuth()
  const { id } = useParams()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [session, setSession] = useState(() => readExamSession(user!.id))
  const [dialog, setDialog] = useState<'finish' | 'cancel' | null>(null)
  const [storageError, setStorageError] = useState(false)
  const submitting = useRef(false)
  const questionHeading = useRef<HTMLHeadingElement>(null)
  const action = useMutation({
    mutationFn: async (type: 'finish' | 'cancel') => {
      if (!session || submitting.current) return
      submitting.current = true
      try {
        if (type === 'cancel') await examService.cancelExam(session.exam.examId)
        else {
          const response = await examService.finishExam(session.exam.examId, {
            answers: session.answers,
          })
          queryClient.setQueryData(['history', 'detail', session.exam.examId], response.data)
        }
        clearExamSession(user!.id)
        await queryClient.invalidateQueries({ queryKey: ['history'] })
        await navigate(type === 'cancel' ? '/' : `/history/${session.exam.examId}`, {
          replace: true,
        })
      } finally {
        submitting.current = false
      }
    },
  })
  if (!session || session.exam.examId !== Number(id))
    return (
      <Page title="Simulado indisponível">
        <Alert severity="info" sx={{ mb: 3 }}>
          Não há uma tentativa em andamento neste navegador para este simulado.
        </Alert>
        <Button component={Link} to="/" variant="contained">
          Voltar ao início
        </Button>
      </Page>
    )
  const { exam, answers, index } = session
  const question = exam.questions[index]!
  const selected = answers[question.questionId] ?? []
  const answered = exam.questions.filter(
    (item) => (answers[item.questionId]?.length ?? 0) > 0,
  ).length
  const unanswered = exam.totalQuestions - answered
  const multiple = question.questionType === 'MULTIPLE_CHOICE'
  function update(next: ExamSession) {
    if (action.isPending) return
    setSession(next)
    try {
      saveExamSession(user!.id, next)
      setStorageError(false)
    } catch {
      setStorageError(true)
    }
  }
  function goTo(index: number) {
    update({ ...session!, index })
    questionHeading.current?.focus()
    window.scrollTo({ top: 0, behavior: 'instant' })
  }
  function select(alternativeId: number) {
    update({
      ...session!,
      answers: {
        ...answers,
        [question.questionId]: toggleAnswer(selected, alternativeId, multiple),
      },
    })
  }
  return (
    <Box className="page-enter" sx={{ pb: 10 }}>
      <Stack
        component="header"
        direction="row"
        justifyContent="space-between"
        alignItems="center"
        flexWrap="wrap"
        gap={1}
        mb={2}
      >
        <Box minWidth={0}>
          <Typography variant="caption" color="text.secondary">
            {exam.subjectName ?? 'Todas as matérias'}
          </Typography>
          <Typography component="h1" variant="h3" sx={{ overflowWrap: 'anywhere' }}>
            {exam.title}
          </Typography>
        </Box>
        <Chip
          size="small"
          variant="outlined"
          label={`${answered} de ${exam.totalQuestions} respondidas`}
        />
      </Stack>
      {storageError && (
        <Alert severity="warning" sx={{ mb: 2 }}>
          O navegador não conseguiu salvar o progresso. Não recarregue esta página.
        </Alert>
      )}
      {action.isError && (
        <Box mb={2}>
          <ErrorState error={action.error} />
        </Box>
      )}
      <LinearProgress
        variant="determinate"
        value={(answered / exam.totalQuestions) * 100}
        aria-label="Progresso do simulado"
        sx={{ height: 3, mb: 1, borderRadius: 1 }}
      />
      <Accordion
        disableGutters
        sx={{
          bgcolor: 'transparent',
          borderBottom: '1px solid',
          borderColor: 'divider',
          mb: 2,
          '&::before': { display: 'none' },
        }}
      >
        <AccordionSummary
          expandIcon={<ExpandMoreOutlined />}
          aria-controls="question-map"
          id="question-map-toggle"
          sx={{ minHeight: 40, px: 0, '& .MuiAccordionSummary-content': { my: 0.5 } }}
        >
          <Typography variant="body2" fontWeight={600}>
            Perguntas{' '}
            <Box component="span" color="text.secondary" ml={1}>
              {index + 1} / {exam.totalQuestions}
            </Box>
          </Typography>
        </AccordionSummary>
        <AccordionDetails id="question-map" sx={{ px: 0, pb: 1.5 }}>
          <Box sx={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, 42px)', gap: 1 }}>
            {exam.questions.map((item, itemIndex) => {
              const done = !!answers[item.questionId]?.length
              return (
                <Tooltip
                  key={item.questionId}
                  title={`Pergunta ${itemIndex + 1}: ${done ? 'respondida' : 'em branco'}`}
                >
                  <Button
                    aria-label={`Ir para pergunta ${itemIndex + 1}${done ? ', respondida' : ', em branco'}`}
                    aria-current={index === itemIndex ? 'step' : undefined}
                    variant={index === itemIndex ? 'contained' : 'outlined'}
                    color={done || index === itemIndex ? 'primary' : 'inherit'}
                    onClick={() => goTo(itemIndex)}
                    disabled={action.isPending}
                    sx={{
                      minWidth: 42,
                      width: 42,
                      height: 42,
                      p: 0,
                      bgcolor: done && index !== itemIndex ? 'primary.light' : undefined,
                      borderColor: 'divider',
                    }}
                  >
                    {itemIndex + 1}
                  </Button>
                </Tooltip>
              )
            })}
          </Box>
        </AccordionDetails>
      </Accordion>
      <Stack direction="row" gap={1.5} alignItems="center" mb={1}>
        <Typography variant="overline" color="text.secondary">
          Pergunta {index + 1}
        </Typography>
        <Chip
          size="small"
          variant="outlined"
          label={multiple ? 'Múltipla escolha' : 'Escolha única'}
        />
      </Stack>
      <Typography
        ref={questionHeading}
        tabIndex={-1}
        component="h2"
        variant="body1"
        className="question-copy"
        sx={{
          mb: 2,
          fontWeight: 500,
          lineHeight: 1.55,
          outline: 'none',
        }}
      >
        {question.statement}
      </Typography>
      <Box role="group" aria-label="Alternativas">
        <Stack gap={1}>
          {question.alternatives.map((alternative, alternativeIndex) => (
            <FormControlLabel
              key={alternative.alternativeId}
              disabled={action.isPending}
              sx={{
                m: 0,
                px: 1.5,
                py: 0.5,
                minHeight: 48,
                border: '1px solid',
                borderColor: selected.includes(alternative.alternativeId)
                  ? 'primary.main'
                  : 'divider',
                bgcolor: selected.includes(alternative.alternativeId)
                  ? 'primary.light'
                  : 'background.paper',
                borderRadius: 1,
                alignItems: 'center',
                '& .MuiFormControlLabel-label': { minWidth: 0, overflowWrap: 'anywhere' },
              }}
              control={
                multiple ? (
                  <Checkbox
                    sx={{ p: 0.5, mr: 1 }}
                    checked={selected.includes(alternative.alternativeId)}
                    onChange={() => select(alternative.alternativeId)}
                  />
                ) : (
                  <Radio
                    sx={{ p: 0.5, mr: 1 }}
                    checked={selected.includes(alternative.alternativeId)}
                    onClick={() => select(alternative.alternativeId)}
                  />
                )
              }
              label={
                <Typography
                  component="span"
                  variant="body2"
                  className="question-copy"
                  sx={{ lineHeight: 1.5 }}
                >
                  <Box component="span" fontWeight={700} mr={1.5} color="text.secondary">
                    {String.fromCharCode(65 + alternativeIndex)}.
                  </Box>
                  {alternative.description}
                </Typography>
              }
            />
          ))}
        </Stack>
      </Box>
      <Box
        component="footer"
        sx={{
          position: 'fixed',
          bottom: 0,
          left: 0,
          right: 0,
          bgcolor: 'background.paper',
          borderTop: '1px solid',
          borderColor: 'divider',
          zIndex: 1100,
          px: { xs: 2, sm: 4 },
          py: 1,
        }}
      >
        <Stack
          direction="row"
          justifyContent="space-between"
          alignItems="center"
          flexWrap="wrap"
          gap={1.5}
          maxWidth={1120}
          mx="auto"
        >
          <Button
            color="error"
            startIcon={<CloseOutlined />}
            onClick={() => {
              action.reset()
              setDialog('cancel')
            }}
            disabled={action.isPending}
          >
            Cancelar
          </Button>
          <Stack direction="row" gap={1} alignItems="center">
            <Tooltip title="Pergunta anterior">
              <span>
                <IconButton
                  aria-label="Pergunta anterior"
                  disabled={index === 0 || action.isPending}
                  onClick={() => goTo(index - 1)}
                >
                  <ArrowBackOutlined />
                </IconButton>
              </span>
            </Tooltip>
            <Tooltip title="Próxima pergunta">
              <span>
                <IconButton
                  aria-label="Próxima pergunta"
                  disabled={index === exam.totalQuestions - 1 || action.isPending}
                  onClick={() => goTo(index + 1)}
                >
                  <ArrowForwardOutlined />
                </IconButton>
              </span>
            </Tooltip>
            <Button
              variant="contained"
              startIcon={<CheckOutlined />}
              disabled={action.isPending}
              onClick={() => {
                action.reset()
                setDialog('finish')
              }}
            >
              {action.isPending ? 'Aguarde...' : 'Finalizar'}
            </Button>
          </Stack>
        </Stack>
      </Box>
      <Dialog
        open={!!dialog}
        onClose={() => {
          if (!action.isPending) setDialog(null)
        }}
        aria-labelledby="exam-dialog-title"
      >
        <DialogTitle id="exam-dialog-title">
          {dialog === 'cancel' ? 'Cancelar este simulado?' : 'Finalizar simulado?'}
        </DialogTitle>
        <DialogContent>
          <DialogContentText>
            {dialog === 'cancel'
              ? 'Esta tentativa será descartada e não entrará nas suas estatísticas. Essa ação não pode ser desfeita.'
              : `${answered} respondidas e ${unanswered} em branco. Depois de finalizar, não será possível alterar as respostas.`}
          </DialogContentText>
          {action.isError && (
            <Box mt={2}>
              <ErrorState error={action.error} />
            </Box>
          )}
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 3 }}>
          <Button onClick={() => setDialog(null)} disabled={action.isPending}>
            Continuar respondendo
          </Button>
          <Button
            variant="contained"
            color={dialog === 'cancel' ? 'error' : 'primary'}
            disabled={action.isPending}
            onClick={() => {
              if (dialog && !action.isPending) action.mutate(dialog)
            }}
          >
            {action.isPending
              ? 'Aguarde...'
              : dialog === 'cancel'
                ? 'Cancelar simulado'
                : 'Confirmar finalização'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  )
}
