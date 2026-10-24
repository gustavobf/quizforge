import { useState } from 'react'
import type { FormEvent } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  Alert,
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  IconButton,
  Stack,
  TextField,
  Tooltip,
  Typography,
} from '@mui/material'
import AddOutlined from '@mui/icons-material/AddOutlined'
import EditOutlined from '@mui/icons-material/EditOutlined'
import DeleteOutline from '@mui/icons-material/DeleteOutline'
import SaveOutlined from '@mui/icons-material/SaveOutlined'
import { Page } from '@/components/ui/Page'
import { ErrorState } from '@/components/ui/ErrorState'
import { LoadingState } from '@/components/ui/LoadingState'
import { subjectService } from '@/services/subject.service'
import type { Subject, CreateSubjectRequest } from '@/types/subject.types'

export default function SubjectsPage() {
  const client = useQueryClient()
  const subjects = useQuery({
    queryKey: ['subjects'],
    queryFn: async () => (await subjectService.getAll()).data,
  })
  const [editing, setEditing] = useState<Subject | 'new' | null>(null)
  const [deleting, setDeleting] = useState<Subject | null>(null)
  const save = useMutation({
    mutationFn: async (data: CreateSubjectRequest) =>
      editing === 'new'
        ? subjectService.create(data)
        : subjectService.update((editing as Subject).id, data),
    onSuccess: async () => {
      await client.invalidateQueries({ queryKey: ['subjects'] })
      setEditing(null)
    },
  })
  const remove = useMutation({
    mutationFn: (id: number) => subjectService.delete(id),
    onSuccess: async () => {
      await client.invalidateQueries({ queryKey: ['subjects'] })
      setDeleting(null)
    },
  })
  function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (save.isPending) return
    const data = new FormData(event.currentTarget)
    save.mutate({
      name: String(data.get('name')).trim(),
      description: String(data.get('description')).trim(),
    })
  }
  return (
    <Page
      title="Matérias"
      eyebrow="Sua biblioteca"
      action={
        <Button
          variant="contained"
          startIcon={<AddOutlined />}
          onClick={() => {
            save.reset()
            setEditing('new')
          }}
        >
          Nova matéria
        </Button>
      }
    >
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
        <Alert severity="info">Nenhuma matéria cadastrada.</Alert>
      ) : (
        subjects.data.map((subject) => (
          <Stack
            key={subject.id}
            direction="row"
            justifyContent="space-between"
            alignItems="center"
            gap={2}
            sx={{ borderBottom: '1px solid', borderColor: 'divider', py: 3 }}
          >
            <Box minWidth={0}>
              <Typography variant="h3" component="h2" sx={{ overflowWrap: 'anywhere' }}>
                {subject.name}
              </Typography>
              {subject.description && (
                <Typography variant="body2" color="text.secondary" mt={1} className="question-copy">
                  {subject.description}
                </Typography>
              )}
            </Box>
            <Stack direction="row" flexShrink={0}>
              <Tooltip title="Editar matéria">
                <IconButton
                  aria-label={`Editar ${subject.name}`}
                  onClick={() => {
                    save.reset()
                    setEditing(subject)
                  }}
                >
                  <EditOutlined />
                </IconButton>
              </Tooltip>
              <Tooltip title="Excluir matéria">
                <IconButton
                  color="error"
                  aria-label={`Excluir ${subject.name}`}
                  onClick={() => {
                    remove.reset()
                    setDeleting(subject)
                  }}
                >
                  <DeleteOutline />
                </IconButton>
              </Tooltip>
            </Stack>
          </Stack>
        ))
      )}
      <Dialog
        open={editing !== null}
        onClose={() => {
          if (!save.isPending) setEditing(null)
        }}
        aria-labelledby="subject-title"
      >
        <Box component="form" onSubmit={submit} key={editing === 'new' ? 'new' : editing?.id}>
          <DialogTitle id="subject-title">
            {editing === 'new' ? 'Nova matéria' : 'Editar matéria'}
          </DialogTitle>
          <DialogContent>
            <Stack gap={3} pt={1}>
              {save.isError && <ErrorState error={save.error} />}
              <TextField
                name="name"
                label="Nome"
                defaultValue={editing && editing !== 'new' ? editing.name : ''}
                required
                autoFocus
                slotProps={{ htmlInput: { maxLength: 100 } }}
              />
              <TextField
                name="description"
                label="Descrição"
                defaultValue={editing && editing !== 'new' ? editing.description : ''}
                multiline
                minRows={3}
                slotProps={{ htmlInput: { maxLength: 1000 } }}
              />
            </Stack>
          </DialogContent>
          <DialogActions sx={{ px: 3, pb: 3 }}>
            <Button onClick={() => setEditing(null)} disabled={save.isPending}>
              Cancelar
            </Button>
            <Button
              type="submit"
              variant="contained"
              startIcon={<SaveOutlined />}
              disabled={save.isPending}
            >
              {save.isPending ? 'Salvando...' : 'Salvar'}
            </Button>
          </DialogActions>
        </Box>
      </Dialog>
      <Dialog
        open={!!deleting}
        onClose={() => {
          if (!remove.isPending) setDeleting(null)
        }}
        aria-labelledby="delete-subject-title"
      >
        <DialogTitle id="delete-subject-title">Excluir matéria?</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Excluir "{deleting?.name}"? Esta ação não pode ser desfeita.
          </DialogContentText>
          {remove.isError && (
            <Box mt={2}>
              <ErrorState error={remove.error} />
            </Box>
          )}
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 3 }}>
          <Button onClick={() => setDeleting(null)} disabled={remove.isPending}>
            Manter matéria
          </Button>
          <Button
            color="error"
            variant="contained"
            disabled={remove.isPending}
            onClick={() => {
              if (deleting) remove.mutate(deleting.id)
            }}
          >
            Excluir
          </Button>
        </DialogActions>
      </Dialog>
    </Page>
  )
}
