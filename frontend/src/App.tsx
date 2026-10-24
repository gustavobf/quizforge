import { lazy, Suspense } from 'react'
import { Navigate, Outlet, Route, Routes, useLocation } from 'react-router-dom'
import { Container } from '@mui/material'
import { AuthProvider } from '@/providers/AuthProvider'
import AppLayout from '@/layouts/AppLayout'
import { ErrorState } from '@/components/ui/ErrorState'
import { LoadingState } from '@/components/ui/LoadingState'
import { useAuth } from '@/hooks/use-auth'
import AuthPage from '@/pages/AuthPage'
const HomePage = lazy(() => import('@/pages/HomePage'))
const ExamPage = lazy(() => import('@/pages/ExamPage'))
const ResultPage = lazy(() => import('@/pages/ResultPage'))
const HistoryPage = lazy(() => import('@/pages/HistoryPage'))
const DashboardPage = lazy(() => import('@/pages/DashboardPage'))
const SubjectsPage = lazy(() => import('@/pages/SubjectsPage'))
const QuestionsPage = lazy(() => import('@/pages/QuestionsPage'))
const ProfilePage = lazy(() => import('@/pages/ProfilePage'))
const AdminUsersPage = lazy(() => import('@/pages/AdminUsersPage'))

function AdminRoute() {
  const { user } = useAuth()
  return user?.role === 'ADMIN' ? <Outlet /> : <Navigate to="/" replace />
}

function ProtectedRoute() {
  const { authenticated, notice, loading, error, retry } = useAuth()
  const location = useLocation()
  if (!authenticated)
    return <Navigate to="/login" replace state={notice ? undefined : { from: location.pathname }} />
  if (loading) return <LoadingState />
  if (error)
    return (
      <Container sx={{ py: 6 }}>
        <ErrorState error={error} retry={retry} />
      </Container>
    )
  return <Outlet />
}

export default function App() {
  return (
    <AuthProvider>
      <Suspense fallback={<LoadingState />}>
        <Routes>
          <Route path="/login" element={<AuthPage />} />
          <Route path="/register" element={<AuthPage register />} />
          <Route element={<ProtectedRoute />}>
            <Route element={<AppLayout />}>
              <Route index element={<HomePage />} />
              <Route path="exam/:id" element={<ExamPage />} />
              <Route path="history/:id" element={<ResultPage />} />
              <Route path="history" element={<HistoryPage />} />
              <Route path="dashboard" element={<DashboardPage />} />
              <Route path="subjects" element={<SubjectsPage />} />
              <Route path="questions" element={<QuestionsPage />} />
              <Route path="profile" element={<ProfilePage />} />
              <Route element={<AdminRoute />}>
                <Route path="admin/users" element={<AdminUsersPage />} />
                <Route path="admin/users/create" element={<AdminUsersPage creating />} />
              </Route>
            </Route>
          </Route>
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </Suspense>
    </AuthProvider>
  )
}
