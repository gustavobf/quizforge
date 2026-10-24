import { useState } from 'react'
import type { FormEvent } from 'react'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import {
  Alert,
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Divider,
  Stack,
  TextField,
  Typography,
} from '@mui/material'
import SaveOutlined from '@mui/icons-material/SaveOutlined'
import LockResetOutlined from '@mui/icons-material/LockResetOutlined'
import DeleteOutline from '@mui/icons-material/DeleteOutline'
import { Page } from '@/components/ui/Page'
import { ErrorState } from '@/components/ui/ErrorState'
import { useAuth } from '@/hooks/use-auth'
import { authService } from '@/services/auth.service'
import { clearAuthStorage } from '@/services/http-client'
import { clearExamSession } from '@/utils/exam-session'

export default function ProfilePage() {
  const { user } = useAuth()
  const client = useQueryClient()
  const [deleting, setDeleting] = useState(false)
  const profile = useMutation({
    mutationFn: authService.updateProfile,
    onSuccess: (response) => {
      client.setQueryData(['profile'], response.data)
      localStorage.setItem('user', JSON.stringify(response.data))
    },
  })
  const password = useMutation({ mutationFn: authService.changePassword })
  const remove = useMutation({
    mutationFn: authService.deleteProfile,
    onSuccess: () => {
      clearExamSession(user!.id)
      clearAuthStorage()
    },
  })
  const [passwordError, setPasswordError] = useState('')
  function saveProfile(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (profile.isPending) return
    const data = new FormData(event.currentTarget)
    profile.mutate({
      name: String(data.get('name')).trim(),
      email: String(data.get('email')).trim(),
    })
  }
  function savePassword(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (password.isPending) return
    const form = event.currentTarget
    const data = new FormData(form)
    if (data.get('newPassword') !== data.get('confirmPassword')) {
      setPasswordError('As senhas não coincidem.')
      return
    }
    setPasswordError('')
    password.mutate(
      {
        currentPassword: String(data.get('currentPassword')),
        newPassword: String(data.get('newPassword')),
      },
      {
        onSuccess: () => {
          form.reset()
          clearAuthStorage('Senha alterada. Entre novamente com sua nova senha.')
        },
      },
    )
  }
  return (
    <Page title="Minha conta" eyebrow={user?.role === 'ADMIN' ? 'Administrador' : 'Perfil'}>
      <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', lg: '1fr 1fr' }, gap: 6 }}>
        <Stack component="form" onSubmit={saveProfile} gap={3} key={`${user?.name}-${user?.email}`}>
          <Typography component="h2" variant="h2">
            Dados pessoais
          </Typography>
          {profile.isError && <ErrorState error={profile.error} />}
          {profile.isSuccess && <Alert severity="success">Perfil atualizado.</Alert>}
          <TextField
            label="Nome"
            name="name"
            defaultValue={user?.name}
            autoComplete="name"
            required
            slotProps={{ htmlInput: { maxLength: 120 } }}
          />
          <TextField
            label="E-mail"
            name="email"
            type="email"
            defaultValue={user?.email}
            autoComplete="email"
            required
          />
          <Button
            type="submit"
            variant="contained"
            startIcon={<SaveOutlined />}
            disabled={profile.isPending}
          >
            {profile.isPending ? 'Salvando...' : 'Salvar alterações'}
          </Button>
        </Stack>
        <Stack component="form" onSubmit={savePassword} gap={3}>
          <Typography component="h2" variant="h2">
            Alterar senha
          </Typography>
          {password.isError && <ErrorState error={password.error} />}
          {passwordError && <Alert severity="error">{passwordError}</Alert>}
          {password.isSuccess && <Alert severity="success">Senha alterada.</Alert>}
          <TextField
            label="Senha atual"
            name="currentPassword"
            type="password"
            autoComplete="current-password"
            required
          />
          <TextField
            label="Nova senha"
            name="newPassword"
            type="password"
            autoComplete="new-password"
            required
            slotProps={{ htmlInput: { minLength: 8, maxLength: 128 } }}
          />
          <TextField
            label="Confirmar nova senha"
            name="confirmPassword"
            type="password"
            autoComplete="new-password"
            required
            slotProps={{ htmlInput: { minLength: 8, maxLength: 128 } }}
          />
          <Button
            type="submit"
            variant="outlined"
            startIcon={<LockResetOutlined />}
            disabled={password.isPending}
          >
            {password.isPending ? 'Alterando...' : 'Alterar senha'}
          </Button>
        </Stack>
      </Box>
      <Divider sx={{ my: 5 }} />
      <Button
        color="error"
        startIcon={<DeleteOutline />}
        onClick={() => {
          remove.reset()
          setDeleting(true)
        }}
      >
        Excluir minha conta
      </Button>
      <Dialog
        open={deleting}
        onClose={() => {
          if (!remove.isPending) setDeleting(false)
        }}
        aria-labelledby="delete-account-title"
      >
        <DialogTitle id="delete-account-title">Excluir sua conta?</DialogTitle>
        <DialogContent>
          <DialogContentText>Sua conta será desativada e você perderá o acesso.</DialogContentText>
          {remove.isError && (
            <Box mt={2}>
              <ErrorState error={remove.error} />
            </Box>
          )}
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 3 }}>
          <Button onClick={() => setDeleting(false)} disabled={remove.isPending}>
            Manter conta
          </Button>
          <Button
            color="error"
            variant="contained"
            onClick={() => remove.mutate()}
            disabled={remove.isPending}
          >
            Excluir conta
          </Button>
        </DialogActions>
      </Dialog>
    </Page>
  )
}
