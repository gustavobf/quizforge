import { useState } from 'react'
import { NavLink, Outlet, useMatch, useNavigate } from 'react-router-dom'
import {
  Avatar,
  Box,
  Button,
  Divider,
  Drawer,
  IconButton,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Stack,
  Tooltip,
  Typography,
} from '@mui/material'
import DashboardOutlined from '@mui/icons-material/DashboardOutlined'
import AddCircleOutline from '@mui/icons-material/AddCircleOutline'
import MenuBookOutlined from '@mui/icons-material/MenuBookOutlined'
import FileUploadOutlined from '@mui/icons-material/FileUploadOutlined'
import HistoryOutlined from '@mui/icons-material/HistoryOutlined'
import PeopleOutline from '@mui/icons-material/PeopleOutline'
import LogoutOutlined from '@mui/icons-material/LogoutOutlined'
import MenuOutlined from '@mui/icons-material/MenuOutlined'
import CloseOutlined from '@mui/icons-material/CloseOutlined'
import { useAuth } from '@/hooks/use-auth'

const links = [
  { to: '/', label: 'Novo simulado', icon: <AddCircleOutline /> },
  { to: '/dashboard', label: 'Visão geral', icon: <DashboardOutlined /> },
  { to: '/history', label: 'Histórico', icon: <HistoryOutlined /> },
  { to: '/subjects', label: 'Matérias', icon: <MenuBookOutlined /> },
  { to: '/questions', label: 'Importar perguntas', icon: <FileUploadOutlined /> },
]
export default function AppLayout() {
  const { user, signOut } = useAuth()
  const navigate = useNavigate()
  const isExamRoute = useMatch('/exam/:id') !== null
  const [navigationOpen, setNavigationOpen] = useState(false)
  const [leaving, setLeaving] = useState(false)
  const navigation = (
    <Stack height="100%" sx={{ p: 2.5 }}>
      <Stack direction="row" alignItems="center" gap={1.2} sx={{ px: 1, py: 2, mb: 4 }}>
        <Box component="img" src="/favicon.svg" alt="" width={30} height={30} />
        <Typography fontFamily="Manrope" fontWeight={800} fontSize="1.25rem">
          QuizForge
        </Typography>
        {navigationOpen && (
          <Tooltip title="Fechar navegação">
            <IconButton
              aria-label="Fechar navegação"
              size="small"
              onClick={() => setNavigationOpen(false)}
              sx={{ ml: 'auto' }}
            >
              <CloseOutlined />
            </IconButton>
          </Tooltip>
        )}
      </Stack>
      <Typography variant="overline" color="text.secondary" sx={{ px: 1.5, mb: 1 }}>
        Estudos
      </Typography>
      <List sx={{ p: 0 }}>
        {[
          ...links,
          ...(user?.role === 'ADMIN'
            ? [{ to: '/admin/users', label: 'Usuários', icon: <PeopleOutline /> }]
            : []),
        ].map((link) => (
          <ListItemButton
            key={link.to}
            component={NavLink}
            to={link.to}
            end={link.to === '/'}
            onClick={() => setNavigationOpen(false)}
            sx={{
              borderRadius: 1,
              mb: 0.5,
              '&.active': {
                bgcolor: 'primary.light',
                color: 'primary.main',
                '& .MuiListItemIcon-root': { color: 'primary.main' },
              },
            }}
          >
            <ListItemIcon sx={{ minWidth: 36 }}>{link.icon}</ListItemIcon>
            <ListItemText
              primary={link.label}
              primaryTypographyProps={{ fontSize: 14, fontWeight: 500 }}
            />
          </ListItemButton>
        ))}
      </List>
      <Box flex={1} />
      <Divider sx={{ my: 2 }} />
      <Button
        onClick={() => {
          setNavigationOpen(false)
          void navigate('/profile')
        }}
        color="inherit"
        sx={{ justifyContent: 'flex-start', textAlign: 'left', gap: 1.5, minWidth: 0 }}
      >
        <Avatar
          sx={{
            width: 34,
            height: 34,
            bgcolor: 'primary.light',
            color: 'primary.main',
            fontSize: 14,
          }}
        >
          {user?.name.slice(0, 1).toUpperCase()}
        </Avatar>
        <Box minWidth={0}>
          <Typography variant="body2" fontWeight={600} noWrap maxWidth={145}>
            {user?.name}
          </Typography>
          <Typography variant="caption" color="text.secondary">
            Minha conta
          </Typography>
        </Box>
      </Button>
      <Button
        color="inherit"
        disabled={leaving}
        startIcon={<LogoutOutlined />}
        sx={{ justifyContent: 'flex-start', mt: 1 }}
        onClick={async () => {
          setLeaving(true)
          await signOut().catch(() => undefined)
          setLeaving(false)
        }}
      >
        Sair
      </Button>
    </Stack>
  )
  return (
    <Box sx={{ display: 'flex', minHeight: '100dvh' }}>
      <Box
        component="nav"
        aria-label="Navegação principal"
        sx={{ width: isExamRoute ? 0 : { md: 248 }, flexShrink: 0 }}
      >
        {!isExamRoute && (
          <Drawer
            variant="permanent"
            sx={{
              display: { xs: 'none', md: 'block' },
              '& .MuiDrawer-paper': {
                width: 248,
                bgcolor: 'background.paper',
                boxSizing: 'border-box',
              },
            }}
          >
            {navigation}
          </Drawer>
        )}
        <Drawer
          open={navigationOpen}
          onClose={() => setNavigationOpen(false)}
          slotProps={{ paper: { id: 'app-navigation-menu' } }}
          sx={{ '& .MuiDrawer-paper': { width: 270 } }}
        >
          {navigation}
        </Drawer>
      </Box>
      <Box sx={{ flex: 1, minWidth: 0 }}>
        <Stack
          direction="row"
          alignItems="center"
          gap={1}
          sx={{
            display: isExamRoute ? 'flex' : { md: 'none' },
            px: 2,
            height: isExamRoute ? 48 : 64,
            bgcolor: 'background.paper',
            borderBottom: '1px solid',
            borderColor: 'divider',
          }}
        >
          <Tooltip title="Abrir navegação">
            <IconButton
              aria-label="Abrir navegação"
              aria-expanded={navigationOpen}
              aria-controls={navigationOpen ? 'app-navigation-menu' : undefined}
              onClick={() => setNavigationOpen(true)}
            >
              <MenuOutlined />
            </IconButton>
          </Tooltip>
          <Typography fontFamily="Manrope" fontWeight={700}>
            QuizForge
          </Typography>
        </Stack>
        <Box
          component="main"
          sx={{
            maxWidth: isExamRoute ? 1120 : 1240,
            mx: 'auto',
            px: isExamRoute ? { xs: 2, sm: 3, lg: 4 } : { xs: 2, sm: 4, lg: 6 },
            py: isExamRoute ? 2 : { xs: 3, md: 6 },
          }}
        >
          <Outlet />
        </Box>
      </Box>
    </Box>
  )
}
