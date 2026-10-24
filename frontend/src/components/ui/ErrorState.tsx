import { Alert, Button } from '@mui/material'
import RefreshOutlined from '@mui/icons-material/RefreshOutlined'
import { errorMessage } from '@/utils/errors'

export function ErrorState({ error, retry }: { error: unknown; retry?: () => void }) {
  return (
    <Alert
      severity="error"
      action={
        retry && (
          <Button color="inherit" size="small" startIcon={<RefreshOutlined />} onClick={retry}>
            Tentar novamente
          </Button>
        )
      }
    >
      {errorMessage(error)}
    </Alert>
  )
}
