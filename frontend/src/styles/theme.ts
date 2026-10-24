import { createTheme } from '@mui/material/styles'

export const theme = createTheme({
  palette: {
    primary: { main: '#216849', dark: '#154b34', light: '#e6f2ec' },
    secondary: { main: '#52668c' },
    background: { default: '#f7f9f8', paper: '#ffffff' },
    text: { primary: '#202923', secondary: '#65716a' },
    divider: '#e1e7e3',
    success: { main: '#216849' },
    error: { main: '#b94343' },
    warning: { main: '#936a14' },
  },
  shape: { borderRadius: 6 },
  typography: {
    fontFamily: '"DM Sans", sans-serif',
    h1: { fontFamily: 'Manrope, sans-serif', fontSize: '2rem', fontWeight: 700 },
    h2: { fontFamily: 'Manrope, sans-serif', fontSize: '1.5rem', fontWeight: 700 },
    h3: { fontFamily: 'Manrope, sans-serif', fontSize: '1.125rem', fontWeight: 700 },
    h4: { fontFamily: 'Manrope, sans-serif', fontSize: '1rem', fontWeight: 700 },
    button: { textTransform: 'none', fontWeight: 650 },
  },
  components: {
    MuiButton: {
      defaultProps: { disableElevation: true },
      styleOverrides: { root: { minHeight: 40 } },
    },
    MuiTextField: { defaultProps: { size: 'small', fullWidth: true } },
    MuiPaper: { defaultProps: { elevation: 0 } },
    MuiCard: { defaultProps: { variant: 'outlined' } },
    MuiTooltip: { defaultProps: { arrow: true } },
    MuiDialog: { defaultProps: { fullWidth: true, maxWidth: 'sm' } },
    MuiTableCell: {
      styleOverrides: { head: { color: '#65716a', fontWeight: 600, background: '#f7f9f8' } },
    },
  },
})
