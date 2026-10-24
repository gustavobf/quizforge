import axios from 'axios'
import MockAdapter from 'axios-mock-adapter'
import { afterEach, describe, expect, it } from 'vitest'
import http from './http-client'

const api = new MockAdapter(http)
const refresh = new MockAdapter(axios, { delayResponse: 15 })
afterEach(() => {
  api.reset()
  refresh.reset()
})

describe('HTTP authentication', () => {
  it('shares a single refresh between concurrent requests and retries both', async () => {
    localStorage.setItem('accessToken', 'old')
    localStorage.setItem('refreshToken', 'refresh-old')
    api.onGet('/first').replyOnce(401).onGet('/first').reply(200, { ok: true })
    api.onGet('/second').replyOnce(401).onGet('/second').reply(200, { ok: true })
    refresh.onPost().reply(200, { accessToken: 'new', refreshToken: 'refresh-new' })
    const responses = await Promise.all([http.get('/first'), http.get('/second')])
    expect(responses.map((response) => response.status)).toEqual([200, 200])
    expect(refresh.history.post).toHaveLength(1)
    expect(localStorage.getItem('accessToken')).toBe('new')
    expect(api.history.get.at(-1)?.headers?.Authorization).toBe('Bearer new')
  })
  it('rejects every waiting request and clears the session when refresh fails', async () => {
    localStorage.setItem('accessToken', 'old')
    localStorage.setItem('refreshToken', 'expired')
    api.onGet().reply(401)
    refresh.onPost().reply(401)
    const responses = await Promise.allSettled([http.get('/first'), http.get('/second')])
    expect(responses.every((response) => response.status === 'rejected')).toBe(true)
    expect(localStorage.getItem('accessToken')).toBeNull()
    expect(refresh.history.post).toHaveLength(1)
  })
  it('does not refresh or clear an existing session for a rejected login', async () => {
    localStorage.setItem('accessToken', 'existing')
    api.onPost('/auth/login').reply(401)
    await expect(http.post('/auth/login', {})).rejects.toBeDefined()
    expect(refresh.history.post).toHaveLength(0)
    expect(localStorage.getItem('accessToken')).toBe('existing')
  })
})
