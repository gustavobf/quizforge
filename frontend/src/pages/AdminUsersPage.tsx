import type { FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  Alert,
  Box,
  Button,
  Chip,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  MenuItem,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
} from '@mui/material'
import PersonAddOutlined from '@mui/icons-material/PersonAddOutlined'
import { Page } from '@/components/ui/Page'
import { ErrorState } from '@/components/ui/ErrorState'
import { LoadingState } from '@/components/ui/LoadingState'
import { authService } from '@/services/auth.service'
import type { CreateUserRequest, Role } from '@/types/auth.types'

export default function AdminUsersPage({ creating = false }: { creating?: boolean }) {
  const navigate = useNavigate()
  const client = useQueryClient()
  const users = useQuery({
    queryKey: ['admin', 'users'],
    queryFn: async () => (await authService.adminGetUsers()).data,
  })
  const create = useMutation({
    mutationFn: (data: CreateUserRequest) => authService.adminCreateUser(data),
    onSuccess: async () => {
      await client.invalidateQueries({ queryKey: ['admin', 'users'] })
      await navigate('/admin/users')
    },
  })
  function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (create.isPending) return
    const data = new FormData(event.currentTarget)
    create.mutate({
      name: String(data.get('name')).trim(),
      email: String(data.get('email')).trim(),
      password: String(data.get('password')),
      role: String(data.get('role')) as Role,
    })
  }
  return (
    <Page
      title="Usuários"
      eyebrow="Administração"
      action={
        <Button
          component={Link}
          to="/admin/users/create"
          variant="contained"
          startIcon={<PersonAddOutlined />}
        >
          Novo usuário
        </Button>
      }
    >
      {users.isPending ? (
        <LoadingState />
      ) : users.isError ? (
        <ErrorState
          error={users.error}
          retry={() => {
            void users.refetch()
          }}
        />
      ) : users.data.length === 0 ? (
        <Alert severity="info">Nenhum usuário cadastrado.</Alert>
      ) : (
        <TableContainer>
          <Table aria-label="Usuários cadastrados">
            <TableHead>
              <TableRow>
                <TableCell>Nome</TableCell>
                <TableCell>E-mail</TableCell>
                <TableCell>Perfil</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {users.data.map((user) => (
                <TableRow key={user.id}>
                  <TableCell sx={{ overflowWrap: 'anywhere' }}>{user.name}</TableCell>
                  <TableCell sx={{ overflowWrap: 'anywhere' }}>{user.email}</TableCell>
                  <TableCell>
                    <Chip
                      label={user.role === 'ADMIN' ? 'Administrador' : 'Usuário'}
                      size="small"
                      variant="outlined"
                    />
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}
      <Dialog
        open={creating}
        onClose={() => {
          if (!create.isPending) void navigate('/admin/users')
        }}
        aria-labelledby="create-user-title"
      >
        <Box component="form" onSubmit={submit}>
          <DialogTitle id="create-user-title">Novo usuário</DialogTitle>
          <DialogContent>
            <Stack pt={1} gap={3}>
              {create.isError && <ErrorState error={create.error} />}
              <TextField name="name" label="Nome" required autoComplete="name" autoFocus />
              <TextField name="email" label="E-mail" type="email" autoComplete="email" required />
              <TextField
                name="password"
                label="Senha"
                type="password"
                autoComplete="new-password"
                required
                slotProps={{ htmlInput: { minLength: 8, maxLength: 128 } }}
              />
              <TextField name="role" label="Perfil" select defaultValue="USER">
                <MenuItem value="USER">Usuário</MenuItem>
                <MenuItem value="ADMIN">Administrador</MenuItem>
              </TextField>
            </Stack>
          </DialogContent>
          <DialogActions sx={{ px: 3, pb: 3 }}>
            <Button
              onClick={() => {
                void navigate('/admin/users')
              }}
              disabled={create.isPending}
            >
              Cancelar
            </Button>
            <Button
              type="submit"
              variant="contained"
              disabled={create.isPending}
              startIcon={<PersonAddOutlined />}
            >
              {create.isPending ? 'Criando...' : 'Criar usuário'}
            </Button>
          </DialogActions>
        </Box>
      </Dialog>
    </Page>
  )
}
