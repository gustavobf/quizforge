import { useState } from 'react'
import { Link } from 'react-router-dom'
import { keepPreviousData, useQuery } from '@tanstack/react-query'
import {
  Alert,
  Box,
  Button,
  IconButton,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TablePagination,
  TableRow,
  Tooltip,
  Typography,
} from '@mui/material'
import ArrowForwardOutlined from '@mui/icons-material/ArrowForwardOutlined'
import AddOutlined from '@mui/icons-material/AddOutlined'
import { Page } from '@/components/ui/Page'
import { ErrorState } from '@/components/ui/ErrorState'
import { LoadingState } from '@/components/ui/LoadingState'
import { historyService } from '@/services/history.service'

export default function HistoryPage() {
  const [page, setPage] = useState(0)
  const [size, setSize] = useState(10)
  const exams = useQuery({
    queryKey: ['history', 'exams', page, size],
    queryFn: async () => (await historyService.getExams(page, size)).data,
    placeholderData: keepPreviousData,
  })
  return (
    <Page
      title="Histórico"
      eyebrow="Suas tentativas"
      action={
        <Button component={Link} to="/" variant="contained" startIcon={<AddOutlined />}>
          Novo simulado
        </Button>
      }
    >
      {exams.isPending ? (
        <LoadingState />
      ) : exams.isError ? (
        <ErrorState
          error={exams.error}
          retry={() => {
            void exams.refetch()
          }}
        />
      ) : exams.data.content.length === 0 ? (
        <Alert severity="info">Nenhum simulado concluído.</Alert>
      ) : (
        <Box>
          <TableContainer>
            <Table aria-label="Simulados concluídos">
              <TableHead>
                <TableRow>
                  <TableCell>Simulado</TableCell>
                  <TableCell>Data</TableCell>
                  <TableCell align="right">Perguntas</TableCell>
                  <TableCell align="right">Nota</TableCell>
                  <TableCell align="right">Revisar</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {exams.data.content.map((exam) => (
                  <TableRow hover key={exam.examId}>
                    <TableCell sx={{ minWidth: 180, maxWidth: 360, overflowWrap: 'anywhere' }}>
                      <Typography variant="body2" fontWeight={600}>
                        {exam.title}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        {exam.subjectName ?? 'Todas as matérias'}
                      </Typography>
                    </TableCell>
                    <TableCell sx={{ whiteSpace: 'nowrap' }}>
                      {new Date(exam.finishedAt).toLocaleDateString('pt-BR')}
                    </TableCell>
                    <TableCell align="right">{exam.totalQuestions}</TableCell>
                    <TableCell align="right" sx={{ fontWeight: 700 }}>
                      {exam.score.toFixed(1)}%
                    </TableCell>
                    <TableCell align="right">
                      <Tooltip title="Revisar resultado">
                        <IconButton
                          component={Link}
                          to={`/history/${exam.examId}`}
                          aria-label={`Revisar ${exam.title}`}
                        >
                          <ArrowForwardOutlined />
                        </IconButton>
                      </Tooltip>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
          <TablePagination
            component="div"
            count={exams.data.totalElements}
            page={page}
            rowsPerPage={size}
            rowsPerPageOptions={[10, 25, 50]}
            onPageChange={(_, next) => setPage(next)}
            onRowsPerPageChange={(event) => {
              setSize(Number(event.target.value))
              setPage(0)
            }}
            labelRowsPerPage="Por página"
            labelDisplayedRows={({ from, to, count }) => `${from}-${to} de ${count}`}
            getItemAriaLabel={(type) => (type === 'next' ? 'Próxima página' : 'Página anterior')}
            sx={{
              '& .MuiTablePagination-toolbar': { flexWrap: 'wrap', px: 0 },
              opacity: exams.isPlaceholderData ? 0.5 : 1,
            }}
          />
        </Box>
      )}
    </Page>
  )
}
