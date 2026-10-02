import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import type { Role } from '@/api/types'

declare module 'vue-router' {
  interface RouteMeta {
    public?: boolean
    roles?: Role[]
  }
}

const routes: RouteRecordRaw[] = [
  { path: '/', name: 'home', component: () => import('@/views/HomeView.vue'), meta: { public: true } },
  { path: '/callback', name: 'callback', component: () => import('@/views/CallbackView.vue'), meta: { public: true } },
  { path: '/employees', name: 'employees', component: () => import('@/views/EmployeesView.vue'), meta: { roles: ['HR', 'PAYROLL_ADMIN'] } },
  { path: '/employees/new', name: 'employee-new', component: () => import('@/views/EmployeeFormView.vue'), meta: { roles: ['HR'] } },
  { path: '/employees/:id', name: 'employee-edit', component: () => import('@/views/EmployeeFormView.vue'), props: true, meta: { roles: ['HR'] } },
  { path: '/payroll', name: 'payroll', component: () => import('@/views/PayrollView.vue'), meta: { roles: ['PAYROLL_ADMIN'] } },
  { path: '/my-payslips', name: 'my-payslips', component: () => import('@/views/MyPayslipsView.vue') },
  { path: '/:pathMatch(.*)*', redirect: '/' },
]

export const router = createRouter({ history: createWebHistory(), routes })

/** Route guard: the UI hides what you can't use. The API still enforces every rule (UI checks are UX, not security). */
export function canAccess(meta: { public?: boolean; roles?: Role[] }, authenticated: boolean, roles: Role[]): 'ok' | 'login' | 'forbidden' {
  if (meta.public) return 'ok'
  if (!authenticated) return 'login'
  if (meta.roles && !meta.roles.some((r) => roles.includes(r))) return 'forbidden'
  return 'ok'
}

router.beforeEach(async (to) => {
  const auth = useAuthStore()
  if (!auth.ready) await auth.init()
  const decision = canAccess(to.meta, auth.isAuthenticated, auth.roles)
  if (decision === 'login') {
    await auth.login(to.fullPath)
    return false
  }
  if (decision === 'forbidden') return { name: 'home' }
  return true
})
