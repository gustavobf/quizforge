export interface Subject {
  id: number
  name: string
  description: string | null
}

export interface CreateSubjectRequest {
  name: string
  description?: string
}
