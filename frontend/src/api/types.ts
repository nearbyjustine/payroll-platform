// Mirrors the backend DTOs (records). Keep in sync with the API: in a bigger project, generate these from OpenAPI.
export type Role = 'EMPLOYEE' | 'HR' | 'PAYROLL_ADMIN'
export type EmploymentType = 'REGULAR' | 'CONTRACTUAL'
export type RunStatus = 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED'

export interface Me { username: string; name: string; email: string; roles: Role[] }
export interface Department { id: number; name: string }
export interface Employee {
  id: number; employeeNo: string; fullName: string; email: string; username: string | null
  employmentType: EmploymentType; baseSalary: number; departmentId: number; departmentName: string
  active: boolean; version: number
}
export interface Page<T> { content: T[]; page: { size: number; number: number; totalElements: number; totalPages: number } }
export interface PayrollRun {
  id: number; period: string; status: RunStatus; employeeCount: number; processedCount: number
  totalGross: number; totalDeductions: number; totalNet: number; failureReason: string | null
  requestedBy: string; requestedAt: string; completedAt: string | null
}
export interface PayslipLine { code: string; label: string; amount: number }
export interface Payslip {
  id: number; period: string; employeeNo: string; employeeName: string
  gross: number; totalDeductions: number; net: number; pdfAvailable: boolean; lines: PayslipLine[]
}
export interface DownloadUrl { url: string; expiresAt: string }
