import type { ReactNode } from 'react'
import { Box, Typography } from '@mui/material'

export function Stat({ label, value, color }: { label: string; value: ReactNode; color?: string }) {
  return (
    <Box sx={{ borderLeft: '2px solid', borderColor: color ?? 'divider', pl: 2 }}>
      <Typography variant="body2" color="text.secondary">
        {label}
      </Typography>
      <Typography
        sx={{
          fontFamily: 'Manrope',
          fontSize: '1.75rem',
          fontWeight: 700,
          mt: 0.5,
          overflowWrap: 'anywhere',
        }}
        color={color}
      >
        {value}
      </Typography>
    </Box>
  )
}
