<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { AnimatePresence, motion } from 'motion-v'
import { Download, Play } from 'lucide-vue-next'
import { Api } from '@/api/endpoints'
import type { Page, PayrollRun, Payslip } from '@/api/types'
import { currentPeriod, money, periodLabel } from '@/utils/format'
import AnimatedNumber from '@/components/AnimatedNumber.vue'
import ErrorBanner from '@/components/ErrorBanner.vue'
import Pager from '@/components/Pager.vue'
import StatusBadge from '@/components/StatusBadge.vue'

const { t, locale } = useI18n()
const runs = ref<PayrollRun[] | null>(null)
const period = ref(currentPeriod())
const error = ref<Error | null>(null)
const notice = ref('')
const selectedId = ref<number | null>(null)
const payslips = ref<Page<Payslip> | null>(null)
let poller: ReturnType<typeof setInterval> | undefined

const selected = computed(() => runs.value?.find((r) => r.id === selectedId.value) ?? null)
const active = computed(() => (runs.value ?? []).some((r) => r.status === 'PENDING' || r.status === 'PROCESSING'))
const printing = computed(() => selected.value?.status === 'PENDING' || selected.value?.status === 'PROCESSING')

async function loadRuns() {
  runs.value = await Api.runs()
  if (selectedId.value === null && runs.value.length) selectedId.value = runs.value[0].id
  // The API answered 202: poll only while a run is still processing, and stop when it posts.
  if (active.value && !poller) poller = setInterval(loadRuns, 1200)
  if (!active.value && poller) {
    clearInterval(poller)
    poller = undefined
  }
  if (selected.value?.status === 'COMPLETED' && (!payslips.value || payslips.value.content[0]?.period !== selected.value.period)) await loadPayslips(0)
}

async function loadPayslips(page: number) {
  if (!selected.value || selected.value.status !== 'COMPLETED') {
    payslips.value = null
    return
  }
  payslips.value = await Api.runPayslips(selected.value.id, page)
}

async function select(run: PayrollRun) {
  selectedId.value = run.id
  payslips.value = null
  await loadPayslips(0)
}

async function start() {
  error.value = null
  notice.value = ''
  try {
    const run = await Api.startRun(period.value)
    notice.value = t('payroll.started', { period: periodLabel(period.value, locale.value) })
    selectedId.value = run.id
    payslips.value = null
    await loadRuns()
  } catch (e) {
    error.value = e as Error
  }
}

async function download(p: Payslip) {
  const { url } = await Api.downloadUrl(p.id)
  window.location.href = url
}

onMounted(loadRuns)
onUnmounted(() => poller && clearInterval(poller))
</script>

<template>
  <header class="mb-7">
    <h1 class="text-4xl font-bold tracking-[-0.03em]">{{ t('payroll.title') }}</h1>
    <p class="mt-1.5 max-w-[62ch] text-ink-2">{{ t('payroll.lead') }}</p>
  </header>

  <div class="grid items-start gap-8 lg:grid-cols-[340px_1fr]">
    <!-- Left: the calculator. Enter a period, press the key, the tape prints. -->
    <div class="lg:sticky lg:top-6">
      <form class="flex items-end gap-2" @submit.prevent="start">
        <label class="field flex-1">{{ t('payroll.period') }}
          <input v-model="period" type="month" class="font-mono" required />
        </label>
        <button class="key" type="submit"><Play class="size-4" aria-hidden="true" />{{ t('payroll.start') }}</button>
      </form>
      <ErrorBanner :error="error" />
      <p v-if="notice && printing" class="mt-3 font-mono text-[0.72rem] text-ink-2" role="status">{{ notice }}</p>

      <AnimatePresence mode="wait">
        <motion.div
          v-if="selected"
          :key="selected.id"
          class="mt-6"
          :initial="{ clipPath: 'inset(0 0 100% 0)' }"
          :animate="{ clipPath: 'inset(-12px -12px -12px -12px)' }"
          :exit="{ opacity: 0, transition: { duration: 0.12 } }"
          :transition="{ duration: 0.45, ease: [0.16, 1, 0.3, 1] }"
        >
          <div class="tape">
            <div class="mb-2.5 space-y-0.5 border-b border-dashed border-rule pb-2.5">
              <div class="flex items-center justify-between gap-2">
                <span class="font-mono text-[0.95rem] font-bold">{{ periodLabel(selected.period, locale) }}</span>
                <StatusBadge :status="selected.status" />
              </div>
              <div class="tape-meta">#{{ String(selected.id).padStart(4, '0') }} · {{ t('payroll.requestedBy', { user: selected.requestedBy }) }}</div>
            </div>

            <div class="tape-line">
              <span>{{ t('payroll.employees') }}</span>
              <span><AnimatedNumber :value="selected.processedCount" format="int" /> / {{ selected.employeeCount || '—' }}</span>
              <span class="op" aria-hidden="true">#</span>
            </div>
            <div class="tape-line">
              <span>{{ t('payroll.gross') }}</span>
              <span><AnimatedNumber :value="selected.totalGross" /></span>
              <span class="op" aria-hidden="true">+</span>
            </div>
            <div class="tape-line neg">
              <span>{{ t('payroll.deductions') }}</span>
              <span><AnimatedNumber :value="selected.totalDeductions" /></span>
              <span class="op" aria-hidden="true">−</span>
            </div>

            <!-- While processing: the print head. When posted: the total line prints. -->
            <div v-if="printing" class="mt-3 flex items-center gap-2 font-mono text-[0.72rem] text-ink-2" role="status">
              <span class="h-3 w-1.5 bg-ink motion-safe:animate-pulse" aria-hidden="true" />{{ t('payroll.printing') }}…
            </div>
            <motion.div
              v-else-if="selected.status === 'COMPLETED'"
              class="tape-line total"
              :initial="{ clipPath: 'inset(0 0 100% 0)' }"
              :animate="{ clipPath: 'inset(0 0 0% 0)' }"
              :transition="{ duration: 0.3, ease: [0.16, 1, 0.3, 1] }"
            >
              <span>{{ t('payroll.net') }}</span>
              <span><AnimatedNumber :value="selected.totalNet" /></span>
              <span class="op" aria-hidden="true">*</span>
            </motion.div>
            <p v-if="selected.failureReason" class="mt-3 font-mono text-[0.72rem] text-ribbon">{{ selected.failureReason }}</p>
            <div class="mt-3 h-1 overflow-hidden rounded-full bg-desk-2" aria-hidden="true">
              <motion.div
                class="h-full origin-left bg-ink"
                :animate="{ scaleX: selected.employeeCount ? selected.processedCount / selected.employeeCount : 0 }"
                :transition="{ duration: 0.5, ease: [0.16, 1, 0.3, 1] }"
              />
            </div>
          </div>
        </motion.div>
      </AnimatePresence>
    </div>

    <!-- Right: the run register and the selected run's payslips. -->
    <div class="min-w-0 space-y-8">
      <section>
        <h2 class="mb-3 font-mono text-[0.72rem] uppercase tracking-[0.08em] text-ink-2">{{ t('payroll.register') }}</h2>
        <div v-if="!runs" class="skeleton h-40" />
        <p v-else-if="runs.length === 0" class="rounded-md bg-paper px-5 py-8 text-ink-2 shadow-tape">{{ t('payroll.noRuns') }}</p>
        <div v-else class="overflow-x-auto rounded-md bg-paper shadow-tape">
          <table class="ledger">
            <thead>
              <tr>
                <th>{{ t('payroll.period') }}</th>
                <th>{{ t('payroll.status') }}</th>
                <th class="hidden !text-right sm:table-cell">{{ t('payroll.employees') }}</th>
                <th class="!text-right">{{ t('payroll.net') }}</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="r in runs"
                :key="r.id"
                class="cursor-pointer"
                :class="{ '!bg-desk-2': r.id === selectedId }"
                tabindex="0"
                :aria-selected="r.id === selectedId"
                @click="select(r)"
                @keydown.enter="select(r)"
              >
                <td class="font-medium">{{ periodLabel(r.period, locale) }}</td>
                <td><StatusBadge :status="r.status" /></td>
                <td class="num hidden sm:table-cell">{{ r.processedCount }}/{{ r.employeeCount }}</td>
                <td class="num font-semibold">{{ money(r.totalNet) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <section v-if="selected && payslips">
        <h2 class="mb-3 font-mono text-[0.72rem] uppercase tracking-[0.08em] text-ink-2">{{ t('payroll.payslips', { period: periodLabel(selected.period, locale) }) }}</h2>
        <div class="overflow-hidden rounded-md bg-paper shadow-tape">
          <div class="overflow-x-auto">
            <table class="ledger">
              <thead>
                <tr>
                  <th>No.</th>
                  <th>{{ t('payroll.employee') }}</th>
                  <th class="hidden !text-right sm:table-cell">{{ t('payroll.gross') }}</th>
                  <th class="hidden !text-right md:table-cell">{{ t('payroll.deductions') }}</th>
                  <th class="!text-right">{{ t('payroll.net') }}</th>
                  <th><span class="sr-only">PDF</span></th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="p in payslips.content" :key="p.id">
                  <td class="font-mono text-[0.75rem]">{{ p.employeeNo }}</td>
                  <td>{{ p.employeeName }}</td>
                  <td class="num hidden sm:table-cell">{{ money(p.gross) }}</td>
                  <td class="num hidden text-ribbon md:table-cell">−{{ money(p.totalDeductions) }}</td>
                  <td class="num font-semibold">{{ money(p.net) }}</td>
                  <td class="w-12 text-right">
                    <button class="rounded-md p-1.5 text-ink-2 hover:bg-desk-2 hover:text-ink disabled:opacity-40" :disabled="!p.pdfAvailable" :aria-label="`PDF ${p.employeeName}`" @click="download(p)">
                      <Download class="size-4" aria-hidden="true" />
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <Pager :page="payslips.page.number" :total-pages="payslips.page.totalPages" @change="loadPayslips" />
        </div>
      </section>
      <p v-else-if="runs && runs.length && !selected" class="text-ink-2">{{ t('payroll.select') }}</p>
    </div>
  </div>
</template>
