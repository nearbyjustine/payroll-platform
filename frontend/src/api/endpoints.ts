import { api } from './client'
import type { Department, DownloadUrl, Employee, Me, Page, PayrollRun, Payslip } from './types'

export const Api = {
  me: () => api<Me>('/api/me'),
  myPayslips: () => api<Payslip[]>('/api/me/payslips'),
  departments: () => api<Department[]>('/api/departments'),
  employees: (params: { q?: string; department?: number | null; page?: number; size?: number }) => {
    const qs = new URLSearchParams()
    if (params.q) qs.set('q', params.q)
    if (params.department) qs.set('department', String(params.department))
    qs.set('page', String(params.page ?? 0))
    qs.set('size', String(params.size ?? 15))
    qs.set('sort', 'employeeNo')   // E-0002 before E-0010: number order, not alphabetical names
    return api<Page<Employee>>(`/api/employees?${qs}`)
  },
  employee: (id: number) => api<Employee>(`/api/employees/${id}`),
  createEmployee: (body: unknown) => api<Employee>('/api/employees', { method: 'POST', body: JSON.stringify(body) }),
  updateEmployee: (id: number, body: unknown) => api<Employee>(`/api/employees/${id}`, { method: 'PUT', body: JSON.stringify(body) }),
  runs: () => api<PayrollRun[]>('/api/payroll-runs'),
  run: (id: number) => api<PayrollRun>(`/api/payroll-runs/${id}`),
  startRun: (period: string) => api<PayrollRun>('/api/payroll-runs', { method: 'POST', body: JSON.stringify({ period }) }),
  runPayslips: (id: number, page = 0) => api<Page<Payslip>>(`/api/payroll-runs/${id}/payslips?page=${page}&size=15`),
  downloadUrl: (payslipId: number) => api<DownloadUrl>(`/api/payslips/${payslipId}/download-url`),
}
