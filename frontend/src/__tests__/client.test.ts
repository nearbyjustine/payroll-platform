import { describe, expect, it } from 'vitest'
import { toApiError } from '@/api/client'

describe('toApiError', () => {
  it('parses a ProblemDetail body including field errors', async () => {
    const res = new Response(
      JSON.stringify({ detail: 'Some fields are invalid.', code: 'error.validation', errors: { email: 'bad' }, correlationId: 'abc' }),
      { status: 400, headers: { 'Content-Type': 'application/problem+json' } },
    )
    const err = await toApiError(res)
    expect(err.status).toBe(400)
    expect(err.fieldErrors.email).toBe('bad')
    expect(err.correlationId).toBe('abc')
  })

  it('survives a non-JSON error body', async () => {
    const err = await toApiError(new Response('<html>Bad gateway</html>', { status: 502, statusText: 'Bad Gateway' }))
    expect(err.status).toBe(502)
  })
})
