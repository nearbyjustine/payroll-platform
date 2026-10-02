/** Formats money with 2 decimals for the active locale, e.g. 26669.95 -> "26,669.95". */
export function money(value: number | string | null | undefined, locale = 'en-PH'): string {
  const n = typeof value === 'string' ? Number(value) : value ?? 0
  return new Intl.NumberFormat(locale, { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(n)
}

/** "2026-09" -> "September 2026" in the given locale. */
export function periodLabel(period: string, locale = 'en-PH'): string {
  const [y, m] = period.split('-').map(Number)
  return new Intl.DateTimeFormat(locale, { month: 'long', year: 'numeric', timeZone: 'UTC' }).format(new Date(Date.UTC(y, m - 1, 1)))
}

/** The current month in Manila as "YYYY-MM" (payroll periods are Philippine months). */
export function currentPeriod(now = new Date()): string {
  const parts = new Intl.DateTimeFormat('en-CA', { timeZone: 'Asia/Manila', year: 'numeric', month: '2-digit' }).formatToParts(now)
  const y = parts.find((p) => p.type === 'year')!.value
  const m = parts.find((p) => p.type === 'month')!.value
  return `${y}-${m}`
}
