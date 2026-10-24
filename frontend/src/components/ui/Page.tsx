import type { ReactNode } from 'react'
import { Box, Stack, Typography } from '@mui/material'

export function Page({
  title,
  eyebrow,
  action,
  children,
}: {
  title: string
  eyebrow?: string
  action?: ReactNode
  children: ReactNode
}) {
  return (
    <Box className="page-enter">
      <Stack
        direction={{ xs: 'column', sm: 'row' }}
        justifyContent="space-between"
        alignItems={{ xs: 'stretch', sm: 'center' }}
        gap={2}
        mb={4}
      >
        <Box minWidth={0}>
          {eyebrow && (
            <Typography variant="overline" color="text.secondary">
              {eyebrow}
            </Typography>
          )}
          <Typography component="h1" variant="h1" sx={{ overflowWrap: 'anywhere' }}>
            {title}
          </Typography>
        </Box>
        {action}
      </Stack>
      {children}
    </Box>
  )
}
