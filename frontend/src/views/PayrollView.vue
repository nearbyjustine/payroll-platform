<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { Api } from '@/api/endpoints'
import type { Page, PayrollRun, Payslip } from '@/api/types'
import { currentPeriod, money, periodLabel } from '@/utils/format'
import ErrorBanner from '@/components/ErrorBanner.vue'
import StatusBadge from '@/components/StatusBadge.vue'
import Pager from '@/components/Pager.vue'

const { t, locale } = useI18n()
const runs = ref<PayrollRun[]>([])
const period = ref(currentPeriod())
const error = ref<Error | null>(null)
const notice = ref('')
const selected = ref<PayrollRun | null>(null)
const payslips = ref<Page<Payslip> | null>(null)
let poller: ReturnType<typeof setInterval> | undefined

const hasActiveRun = computed(() => runs.value.some((r) => r.status === 'PENDING' || r.status === 'PROCESSING'))

async function loadRuns() {
  runs.value = await Api.runs()
  // Poll only while something is processing: the API returned 202, so we check back for progress.
  if (hasActiveRun.value && !poller) poller = setInterval(loadRuns, 1500)
  if (!hasActiveRun.value && poller) {
    clearInterval(poller)
    poller = undefined
  }
}

async function start() {
  error.value = null
  notice.value = ''
  try {
    await Api.startRun(period.value)
    notice.value = t('payroll.started', { period: period.value })
    await loadRuns()
  } catch (e) {
    error.value = e as Error
  }
}

async function open(run: PayrollRun, page = 0) {
  selected.value = run
  payslips.value = await Api.runPayslips(run.id, page)
}

async function download(p: Payslip) {
  const { url } = await Api.downloadUrl(p.id)
  window.location.href = url
}

onMounted(loadRuns)
onUnmounted(() => poller && clearInterval(poller))
</script>

<template>
  <section class="card">
    <h1>{{ t('payroll.title') }}</h1>
    <form class="row" @submit.prevent="start">
      <label>{{ t('payroll.period') }} <input v-model="period" type="month" required /></label>
      <button type="submit">{{ t('payroll.start') }}</button>
    </form>
    <p v-if="notice" class="banner ok">{{ notice }}</p>
    <ErrorBanner :error="error" />
    <div class="table-wrap">
      <table>
        <thead>
          <tr>
            <th>{{ t('payroll.period') }}</th>
            <th>{{ t('payroll.status') }}</th>
            <th>{{ t('payroll.progress') }}</th>
            <th class="num">{{ t('payroll.gross') }}</th>
            <th class="num">{{ t('payroll.deductions') }}</th>
            <th class="num">{{ t('payroll.net') }}</th>
            <th>{{ t('payroll.requestedBy') }}</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="r in runs" :key="r.id">
            <td>{{ periodLabel(r.period, locale) }}</td>
            <td><StatusBadge :status="r.status" /></td>
            <td>
              <progress :value="r.processedCount" :max="r.employeeCount || 1"></progress>
              {{ r.processedCount }}/{{ r.employeeCount }}
            </td>
            <td class="num">{{ money(r.totalGross) }}</td>
            <td class="num">{{ money(r.totalDeductions) }}</td>
            <td class="num">{{ money(r.totalNet) }}</td>
            <td>{{ r.requestedBy }}</td>
            <td><button class="ghost" :disabled="r.status !== 'COMPLETED'" @click="open(r)">{{ t('payroll.payslips') }}</button></td>
          </tr>
          <tr v-if="runs.length === 0"><td colspan="8" class="muted">{{ t('common.none') }}</td></tr>
        </tbody>
      </table>
    </div>
    <p v-for="r in runs.filter((x) => x.failureReason)" :key="'f' + r.id" class="banner error">{{ r.period }}: {{ r.failureReason }}</p>
  </section>

  <section v-if="selected && payslips" class="card">
    <h2>{{ t('payroll.payslips') }} · {{ periodLabel(selected.period, locale) }}</h2>
    <div class="table-wrap">
      <table>
        <thead>
          <tr>
            <th>{{ t('employees.no') }}</th>
            <th>{{ t('employees.name') }}</th>
            <th class="num">{{ t('payroll.gross') }}</th>
            <th class="num">{{ t('payroll.deductions') }}</th>
            <th class="num">{{ t('payroll.net') }}</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="p in payslips.content" :key="p.id">
            <td>{{ p.employeeNo }}</td>
            <td>{{ p.employeeName }}</td>
            <td class="num">{{ money(p.gross) }}</td>
            <td class="num">{{ money(p.totalDeductions) }}</td>
            <td class="num">{{ money(p.net) }}</td>
            <td><button class="ghost" :disabled="!p.pdfAvailable" @click="download(p)">PDF</button></td>
          </tr>
        </tbody>
      </table>
    </div>
    <Pager :page="payslips.page.number" :total-pages="payslips.page.totalPages" @change="(n) => open(selected!, n)" />
  </section>
</template>
