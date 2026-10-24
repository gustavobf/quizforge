import { useState } from 'react'
import type { FormEvent } from 'react'
import { Link, Navigate, useLocation } from 'react-router-dom'
import {
  Alert,
  Box,
  Button,
  Container,
  IconButton,
  InputAdornment,
  Stack,
  TextField,
  Tooltip,
  Typography,
} from '@mui/material'
import ArrowForwardOutlined from '@mui/icons-material/ArrowForwardOutlined'
import VisibilityOutlined from '@mui/icons-material/VisibilityOutlined'
import VisibilityOffOutlined from '@mui/icons-material/VisibilityOffOutlined'
import { authService } from '@/services/auth.service'
import { useAuth } from '@/hooks/use-auth'
import { errorMessage } from '@/utils/errors'

export default function AuthPage({ register = false }: { register?: boolean }) {
  const { authenticated, notice, signIn } = useAuth()
  const location = useLocation()
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')
  const [visible, setVisible] = useState(false)
  const destination =
    typeof location.state?.from === 'string' &&
    location.state.from.startsWith('/') &&
    !location.state.from.startsWith('//')
      ? location.state.from
      : '/'
  if (authenticated) return <Navigate to={destination} replace />
  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const values = new FormData(event.currentTarget)
    setBusy(true)
    setError('')
    try {
      const data = {
        name: String(values.get('name') ?? '').trim(),
        email: String(values.get('email')).trim(),
        password: String(values.get('password')),
      }
      const response = register ? await authService.register(data) : await authService.login(data)
      signIn(response.data)
    } catch (cause) {
      setError(errorMessage(cause))
    } finally {
      setBusy(false)
    }
  }
  return (
    <Box
      sx={{
        minHeight: '100dvh',
        bgcolor: 'background.paper',
        backgroundImage: 'radial-gradient(#dce5df 0.7px, transparent 0.7px)',
        backgroundSize: '24px 24px',
      }}
    >
      <Stack direction="row" alignItems="center" gap={1.2} sx={{ p: { xs: 3, sm: 5 } }}>
        <Box component="img" src="/favicon.svg" alt="" width={32} height={32} />
        <Typography fontFamily="Manrope" fontWeight={800} fontSize="1.3rem">
          QuizForge
        </Typography>
      </Stack>
      <Container maxWidth="xs" sx={{ py: { xs: 5, sm: 10 } }}>
        <Typography variant="overline" color="primary">
          Seu espaço de estudo
        </Typography>
        <Typography variant="h1" component="h1" mt={1} mb={4}>
          {register ? 'Crie sua conta' : 'Acesse sua conta'}
        </Typography>
        <Stack component="form" onSubmit={submit} gap={2.5}>
          {notice && <Alert severity="success">{notice}</Alert>}
          {error && <Alert severity="error">{error}</Alert>}
          {register && (
            <TextField
              label="Nome"
              name="name"
              autoComplete="name"
              required
              slotProps={{ htmlInput: { maxLength: 120 } }}
            />
          )}
          <TextField label="E-mail" name="email" type="email" autoComplete="email" required />
          <TextField
            label="Senha"
            name="password"
            type={visible ? 'text' : 'password'}
            autoComplete={register ? 'new-password' : 'current-password'}
            required
            slotProps={{
              htmlInput: { minLength: register ? 8 : 1, maxLength: 128 },
              input: {
                endAdornment: (
                  <InputAdornment position="end">
                    <Tooltip title={visible ? 'Ocultar senha' : 'Mostrar senha'}>
                      <IconButton
                        aria-label={visible ? 'Ocultar senha' : 'Mostrar senha'}
                        onClick={() => setVisible(!visible)}
                        edge="end"
                      >
                        {visible ? <VisibilityOffOutlined /> : <VisibilityOutlined />}
                      </IconButton>
                    </Tooltip>
                  </InputAdornment>
                ),
              },
            }}
          />
          <Button
            type="submit"
            variant="contained"
            size="large"
            disabled={busy}
            endIcon={<ArrowForwardOutlined />}
          >
            {busy ? 'Aguarde...' : register ? 'Criar conta' : 'Entrar'}
          </Button>
          <Typography variant="body2" color="text.secondary" textAlign="center">
            {register ? 'Já tem uma conta? ' : 'Ainda não tem conta? '}
            <Box
              component={Link}
              to={register ? '/login' : '/register'}
              sx={{ color: 'primary.main', fontWeight: 600 }}
            >
              {register ? 'Entrar' : 'Criar conta'}
            </Box>
          </Typography>
        </Stack>
      </Container>
    </Box>
  )
}
