import { Link, useParams } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { Button } from '@mui/material'
import AddOutlined from '@mui/icons-material/AddOutlined'
import { historyService } from '@/services/history.service'
import { Page } from '@/components/ui/Page'
import { ErrorState } from '@/components/ui/ErrorState'
import { LoadingState } from '@/components/ui/LoadingState'
import ExamResult from '@/components/exam/ExamResult'

export default function ResultPage() {
  const { id } = useParams()
  const result = useQuery({
    queryKey: ['history', 'detail', Number(id)],
    queryFn: async () => (await historyService.getExamDetail(Number(id))).data,
  })
  if (result.isPending) return <LoadingState />
  if (result.isError)
    return (
      <ErrorState
        error={result.error}
        retry={() => {
          void result.refetch()
        }}
      />
    )
  return (
    <Page
      title={result.data.title}
      eyebrow={result.data.subjectName ?? 'Resultado do simulado'}
      action={
        <Button component={Link} to="/" variant="contained" startIcon={<AddOutlined />}>
          Novo simulado
        </Button>
      }
    >
      <ExamResult result={result.data} />
    </Page>
  )
}
