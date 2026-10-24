import type { FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useMutation, useQuery } from '@tanstack/react-query'
import { Alert, Box, Button, Divider, MenuItem, Stack, TextField, Typography } from '@mui/material'
import ArrowForwardOutlined from '@mui/icons-material/ArrowForwardOutlined'
import PlayArrowOutlined from '@mui/icons-material/PlayArrowOutlined'
import { Page } from '@/components/ui/Page'
import { Stat } from '@/components/ui/Stat'
import { ErrorState } from '@/components/ui/ErrorState'
import { LoadingState } from '@/components/ui/LoadingState'
import { subjectService } from '@/services/subject.service'
import { examService } from '@/services/exam.service'
import { historyService } from '@/services/history.service'
import { useAuth } from '@/hooks/use-auth'
import { readExamSession, saveExamSession } from '@/utils/exam-session'

export default function HomePage() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const subjects = useQuery({
    queryKey: ['subjects'],
    queryFn: async () => (await subjectService.getAll()).data,
  })
  const summary = useQuery({
    queryKey: ['history', 'summary'],
    queryFn: async () => (await historyService.getSummary()).data,
  })
  const active = readExamSession(user!.id)
  const create = useMutation({
    mutationFn: async (data: { title: string; quantity: number; subjectId?: number }) => {
      const { data: exam } = await examService.createExam(data)
      saveExamSession(user!.id, { exam, answers: {}, index: 0 })
      await navigate(`/exam/${exam.examId}`)
    },
  })
  function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (create.isPending || active) return
    const data = new FormData(event.currentTarget)
    const subject = String(data.get('subject'))
    create.mutate({
      title: String(data.get('title')).trim(),
      quantity: Number(data.get('quantity')),
      subjectId: subject ? Number(subject) : undefined,
    })
  }
  return (
    <Page title="Novo simulado" eyebrow={`Olá, ${user?.name.split(' ')[0] ?? ''}`}>
      {active && (
        <Alert
          severity="info"
          sx={{ mb: 4 }}
          action={
            <Button
              component={Link}
              to={`/exam/${active.exam.examId}`}
              color="inherit"
              startIcon={<PlayArrowOutlined />}
            >
              Retomar
            </Button>
          }
        >
          Simulado em andamento: {active.exam.title}
        </Alert>
      )}
      <Box
        sx={{
          display: 'grid',
          gridTemplateColumns: { xs: '1fr', lg: 'minmax(0, 1.35fr) minmax(0, 1fr)' },
          gap: { xs: 5, lg: 7 },
        }}
      >
        <Box component="section">
          <Typography variant="h3" mb={3}>
            Configurar tentativa
          </Typography>
          {subjects.isPending ? (
            <LoadingState />
          ) : subjects.isError ? (
            <ErrorState
              error={subjects.error}
              retry={() => {
                void subjects.refetch()
              }}
            />
          ) : (
            <Stack component="form" onSubmit={submit} gap={3}>
              {create.isError && <ErrorState error={create.error} />}
              {subjects.data.length === 0 && (
                <Alert severity="info">
                  Nenhuma matéria cadastrada. <Link to="/subjects">Cadastrar matéria</Link>
                </Alert>
              )}
              <TextField
                name="title"
                label="Nome do simulado"
                placeholder="Revisão da semana"
                required
                disabled={!!active}
                slotProps={{ htmlInput: { maxLength: 200 } }}
              />
              <TextField select name="subject" label="Matéria" defaultValue="" disabled={!!active}>
                <MenuItem value="">Todas as matérias</MenuItem>
                {subjects.data.map((subject) => (
                  <MenuItem key={subject.id} value={subject.id}>
                    {subject.name}
                  </MenuItem>
                ))}
              </TextField>
              <TextField
                name="quantity"
                type="number"
                label="Quantidade de perguntas"
                defaultValue={10}
                required
                disabled={!!active}
                slotProps={{ htmlInput: { min: 1, max: 100, step: 1 } }}
              />
              <Button
                variant="contained"
                type="submit"
                size="large"
                disabled={create.isPending || !!active}
                endIcon={<ArrowForwardOutlined />}
              >
                {create.isPending ? 'Preparando...' : 'Iniciar simulado'}
              </Button>
            </Stack>
          )}
        </Box>
        <Box
          component="section"
          sx={{ borderLeft: { lg: '1px solid' }, borderColor: { lg: 'divider' }, pl: { lg: 5 } }}
        >
          <Typography variant="h3" mb={3}>
            Seu percurso
          </Typography>
          {summary.isPending ? (
            <LoadingState />
          ) : summary.isError ? (
            <ErrorState
              error={summary.error}
              retry={() => {
                void summary.refetch()
              }}
            />
          ) : (
            <>
              <Box
                className="stat-grid"
                sx={{ gridTemplateColumns: 'repeat(2, minmax(0, 1fr))', mb: 4 }}
              >
                <Stat label="Concluídos" value={summary.data.totalExams} />
                <Stat label="Média" value={`${summary.data.averageScore.toFixed(0)}%`} />
              </Box>
              <Divider />
              <Typography variant="h4" mt={3} mb={1}>
                Últimas tentativas
              </Typography>
              {summary.data.recentExams.length === 0 ? (
                <Typography color="text.secondary" variant="body2" py={2}>
                  Nenhum simulado concluído.
                </Typography>
              ) : (
                summary.data.recentExams.slice(0, 4).map((exam) => (
                  <Stack
                    key={exam.id}
                    component={Link}
                    to={`/history/${exam.id}`}
                    direction="row"
                    gap={2}
                    justifyContent="space-between"
                    sx={{
                      py: 2,
                      textDecoration: 'none',
                      borderBottom: '1px solid',
                      borderColor: 'divider',
                      '&:hover': { color: 'primary.main' },
                    }}
                  >
                    <Box minWidth={0}>
                      <Typography
                        variant="body2"
                        fontWeight={600}
                        sx={{ overflowWrap: 'anywhere' }}
                      >
                        {exam.title}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        {exam.subjectName ?? 'Todas as matérias'}
                      </Typography>
                    </Box>
                    <Typography fontWeight={700}>{exam.score.toFixed(0)}%</Typography>
                  </Stack>
                ))
              )}
              <Button
                component={Link}
                to="/history"
                endIcon={<ArrowForwardOutlined />}
                sx={{ mt: 2, px: 0 }}
              >
                Ver histórico
              </Button>
            </>
          )}
        </Box>
      </Box>
    </Page>
  )
}
