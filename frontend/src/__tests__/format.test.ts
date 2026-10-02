import { describe, expect, it } from 'vitest'
import { currentPeriod, money, periodLabel } from '@/utils/format'

describe('format', () => {
  it('formats money with two decimals and thousands separators', () => {
    expect(money(26669.95)).toBe('26,669.95')
    expect(money('1350')).toBe('1,350.00')
  })

  it('labels a period', () => {
    expect(periodLabel('2026-09', 'en-PH')).toBe('September 2026')
  })

  it('uses the Manila month, not the browser time zone', () => {
    // 30 Sep 2026 17:00 UTC is already 1 Oct 01:00 in Manila
    expect(currentPeriod(new Date('2026-09-30T17:00:00Z'))).toBe('2026-10')
  })
})
