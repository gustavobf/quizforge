import { useQuery } from '@tanstack/react-query'
import {
  Alert,
  Box,
  LinearProgress,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from '@mui/material'
import { Page } from '@/components/ui/Page'
import { Stat } from '@/components/ui/Stat'
import { ErrorState } from '@/components/ui/ErrorState'
import { LoadingState } from '@/components/ui/LoadingState'
import { historyService } from '@/services/history.service'

export default function DashboardPage() {
  const summary = useQuery({
    queryKey: ['history', 'summary'],
    queryFn: async () => (await historyService.getSummary()).data,
  })
  const subjects = useQuery({
    queryKey: ['history', 'subjects'],
    queryFn: async () => (await historyService.getSubjectStats()).data,
  })
  return (
    <Page title="Visão geral" eyebrow="Seu desempenho">
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
        <Box className="stat-grid" mb={6}>
          <Stat label="Simulados concluídos" value={summary.data.totalExams} />
          <Stat label="Perguntas respondidas" value={summary.data.totalQuestionsAnswered} />
          <Stat label="Média geral" value={`${summary.data.averageScore.toFixed(1)}%`} />
          <Stat
            label="Melhor nota"
            value={`${summary.data.bestScore.toFixed(1)}%`}
            color="success.main"
          />
        </Box>
      )}
      <Typography component="h2" variant="h2" mb={3}>
        Por matéria
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
      ) : subjects.data.length === 0 ? (
        <Alert severity="info">Ainda não há resultados por matéria.</Alert>
      ) : (
        <TableContainer>
          <Table aria-label="Desempenho por matéria">
            <TableHead>
              <TableRow>
                <TableCell>Matéria</TableCell>
                <TableCell align="right">Simulados</TableCell>
                <TableCell align="right">Acertos</TableCell>
                <TableCell>Média</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {subjects.data.map((subject) => {
                const subjectName =
                  subject.subjectId === 0 ? 'Todas as matérias' : subject.subjectName
                return (
                  <TableRow key={subject.subjectId}>
                    <TableCell sx={{ overflowWrap: 'anywhere', minWidth: 160 }}>
                      {subjectName}
                    </TableCell>
                    <TableCell align="right">{subject.totalExams}</TableCell>
                    <TableCell align="right">
                      {subject.correctAnswers} / {subject.totalQuestions}
                    </TableCell>
                    <TableCell sx={{ minWidth: 160 }}>
                      <Typography variant="body2" mb={1}>
                        {subject.averageScore.toFixed(1)}%
                      </Typography>
                      <LinearProgress
                        variant="determinate"
                        value={Math.min(100, Math.max(0, subject.averageScore))}
                        aria-label={`Média em ${subjectName}`}
                      />
                    </TableCell>
                  </TableRow>
                )
              })}
            </TableBody>
          </Table>
        </TableContainer>
      )}
    </Page>
  )
}
