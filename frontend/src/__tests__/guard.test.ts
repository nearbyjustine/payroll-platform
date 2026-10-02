import { describe, expect, it, vi } from 'vitest'

vi.mock('@/stores/auth', () => ({ useAuthStore: () => ({}) }))
vi.mock('@/views/HomeView.vue', () => ({ default: {} }))

const { canAccess } = await import('@/router')

describe('route guard', () => {
  it('lets anyone open public pages', () => {
    expect(canAccess({ public: true }, false, [])).toBe('ok')
  })
  it('sends anonymous users to login', () => {
    expect(canAccess({}, false, [])).toBe('login')
  })
  it('blocks users without the required role', () => {
    expect(canAccess({ roles: ['PAYROLL_ADMIN'] }, true, ['EMPLOYEE'])).toBe('forbidden')
    expect(canAccess({ roles: ['HR', 'PAYROLL_ADMIN'] }, true, ['HR'])).toBe('ok')
  })
})
