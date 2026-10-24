import { useState } from 'react'
import type { FormEvent } from 'react'
import { Link } from 'react-router-dom'
import { useMutation, useQuery } from '@tanstack/react-query'
import { Alert, Box, Button, MenuItem, Stack, TextField, Typography } from '@mui/material'
import DownloadOutlined from '@mui/icons-material/DownloadOutlined'
import UploadFileOutlined from '@mui/icons-material/UploadFileOutlined'
import { Page } from '@/components/ui/Page'
import { ErrorState } from '@/components/ui/ErrorState'
import { LoadingState } from '@/components/ui/LoadingState'
import { questionService } from '@/services/question.service'
import { subjectService } from '@/services/subject.service'
import { templateService } from '@/services/template.service'

export default function QuestionsPage() {
  const [file, setFile] = useState<File | null>(null)
  const [subject, setSubject] = useState('')
  const subjects = useQuery({
    queryKey: ['subjects'],
    queryFn: async () => (await subjectService.getAll()).data,
  })
  const upload = useMutation({
    mutationFn: async () => {
      if (!file || !subject) throw new Error('Selecione uma matéria e uma planilha.')
      if (file.size > 10 * 1024 * 1024) throw new Error('A planilha deve ter no máximo 10 MB.')
      return (await questionService.importQuestions(file, Number(subject))).data
    },
  })
  const download = useMutation({
    mutationFn: async () => {
      const { data } = await templateService.downloadTemplate()
      const url = URL.createObjectURL(data)
      const anchor = document.createElement('a')
      anchor.href = url
      anchor.download = 'quizforge-perguntas.xlsx'
      anchor.click()
      URL.revokeObjectURL(url)
    },
  })
  function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (!upload.isPending) upload.mutate()
  }
  return (
    <Page
      title="Importar perguntas"
      eyebrow="Sua biblioteca"
      action={
        <Button
          variant="outlined"
          startIcon={<DownloadOutlined />}
          disabled={download.isPending}
          onClick={() => download.mutate()}
        >
          {download.isPending ? 'Baixando...' : 'Planilha modelo'}
        </Button>
      }
    >
      {download.isError && (
        <Box mb={3}>
          <ErrorState error={download.error} />
        </Box>
      )}
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
        <Alert severity="info">
          Cadastre uma matéria antes de importar perguntas.{' '}
          <Link to="/subjects">Cadastrar matéria</Link>
        </Alert>
      ) : (
        <Stack component="form" onSubmit={submit} gap={3} maxWidth={600}>
          {upload.isError && <ErrorState error={upload.error} />}
          {upload.isSuccess && (
            <Alert severity="success">
              {upload.data.totalQuestionsImported} perguntas importadas.
            </Alert>
          )}
          <TextField
            select
            label="Matéria"
            value={subject}
            onChange={(event) => {
              setSubject(event.target.value)
              upload.reset()
            }}
            required
            disabled={upload.isPending}
          >
            {subjects.data.map((item) => (
              <MenuItem key={item.id} value={item.id}>
                {item.name}
              </MenuItem>
            ))}
          </TextField>
          <Box
            sx={{
              border: '1px dashed',
              borderColor: 'divider',
              p: 4,
              textAlign: 'center',
              borderRadius: 1,
              bgcolor: 'background.paper',
            }}
          >
            <UploadFileOutlined sx={{ fontSize: 36, color: 'text.secondary', mb: 2 }} />
            <Typography variant="body2" color="text.secondary" mb={2}>
              Planilha de perguntas (.xlsx)
            </Typography>
            <Button component="label" variant="outlined" disabled={upload.isPending}>
              Selecionar arquivo
              <input
                aria-label="Planilha de perguntas"
                type="file"
                accept=".xlsx"
                hidden
                onChange={(event) => {
                  setFile(event.target.files?.[0] ?? null)
                  upload.reset()
                }}
              />
            </Button>
            {file && (
              <Typography variant="body2" mt={2} sx={{ overflowWrap: 'anywhere' }}>
                {file.name}
              </Typography>
            )}
          </Box>
          <Button
            type="submit"
            variant="contained"
            startIcon={<UploadFileOutlined />}
            disabled={!file || !subject || upload.isPending}
          >
            {upload.isPending ? 'Importando...' : 'Importar perguntas'}
          </Button>
        </Stack>
      )}
    </Page>
  )
}
