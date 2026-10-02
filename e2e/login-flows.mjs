// Run with the full stack up (docker compose --profile full up -d):  cd e2e && npm install && npm test
import puppeteer from 'puppeteer-core'
const shots = process.argv[2]
const browser = await puppeteer.launch({ executablePath: process.env.CHROME_PATH ?? '/Applications/Google Chrome.app/Contents/MacOS/Google Chrome', headless: 'new', args: ['--no-first-run'] })
let page
async function fresh() { const ctx = await browser.createBrowserContext(); page = await ctx.newPage(); await page.setViewport({ width: 1280, height: 860 }) }
async function login(user) {
  await fresh()
  await page.goto('http://localhost:8081/', { waitUntil: 'networkidle0' })
  await Promise.all([page.waitForNavigation({ waitUntil: 'networkidle0' }), page.click('main button')])
  console.log('redirected to', new URL(page.url()).host + new URL(page.url()).pathname)
  await page.type('#username', user); await page.type('#password', user + '123')
  await Promise.all([page.waitForNavigation({ waitUntil: 'networkidle0' }), page.click('#kc-login')])
  await page.waitForFunction(() => location.pathname === '/' && document.querySelector('h1')?.textContent?.includes('Welcome'))
  console.log(user, 'logged in:', await page.$eval('h1', e => e.textContent))
}
async function logout() { await page.browserContext().close() }

await login('paolo')
await page.goto('http://localhost:8081/payroll', { waitUntil: 'networkidle0' })
await page.waitForSelector('tbody tr')
const btn = await page.$$('tbody tr button'); await btn[0].click(); await page.waitForSelector('section:nth-of-type(2) tbody tr')
console.log('payroll rows:', await page.$$eval('section:first-of-type tbody tr', r => r.map(x => x.innerText.replace(/\s+/g, ' ').trim())))
await page.screenshot({ path: shots + '/payroll.png', fullPage: true })
await logout()

await login('hana')
await page.goto('http://localhost:8081/employees', { waitUntil: 'networkidle0' })
await page.waitForSelector('tbody tr')
await page.type('.filters input', 'demo employee 2')
await new Promise(r => setTimeout(r, 900))
console.log('search results:', (await page.$$('tbody tr')).length)
await page.screenshot({ path: shots + '/employees.png', fullPage: true })
// payroll link must not be visible to HR
console.log('HR nav:', await page.$$eval('.topbar nav a', a => a.map(x => x.textContent)))
await logout()

await login('ana')
await page.goto('http://localhost:8081/my-payslips', { waitUntil: 'networkidle0' })
await page.waitForSelector('.slip')
console.log('ana payslips:', await page.$$eval('.slip h2', h => h.map(x => x.textContent)))
await page.select('.lang select', 'fil'); await new Promise(r => setTimeout(r, 300))
console.log('fil title:', await page.$eval('h1', e => e.textContent))
await page.screenshot({ path: shots + '/my-payslips-fil.png', fullPage: true })
await page.goto('http://localhost:8081/payroll', { waitUntil: 'networkidle0' })
console.log('ana visiting /payroll ends at', new URL(page.url()).pathname)
await browser.close()
