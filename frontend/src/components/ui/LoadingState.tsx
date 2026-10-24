import { Box, CircularProgress } from '@mui/material'

export function LoadingState() {
  return (
    <Box
      role="status"
      aria-label="Carregando"
      sx={{ display: 'flex', justifyContent: 'center', py: 8 }}
    >
      <CircularProgress size={30} />
    </Box>
  )
}
