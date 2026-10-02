<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { Download, LoaderCircle } from 'lucide-vue-next'
import { Api } from '@/api/endpoints'
import type { Payslip } from '@/api/types'
import { money, periodLabel } from '@/utils/format'
import ErrorBanner from '@/components/ErrorBanner.vue'
import TapeStrip, { type PrintedLine } from '@/components/TapeStrip.vue'

const { t, locale } = useI18n()
const payslips = ref<Payslip[] | null>(null)
const error = ref<Error | null>(null)
const busy = ref<number | null>(null)

onMounted(async () => {
  try {
    payslips.value = await Api.myPayslips()
  } catch (e) {
    error.value = e as Error
  }
})

/** A payslip printed as calculator tape: gross, each deduction in red ribbon, the net as the starred total. */
function lines(p: Payslip): PrintedLine[] {
  return [
    { key: 'gross', label: t('payslips.gross'), value: money(p.gross), op: '+' },
    ...p.lines.map((l) => ({ key: l.code, label: l.label, value: money(l.amount), op: '−', tone: 'neg' as const })),
    { key: 'net', label: t('payslips.net'), value: money(p.net), op: '*', tone: 'total' as const },
  ]
}

async function download(p: Payslip) {
  busy.value = p.id
  try {
    const { url } = await Api.downloadUrl(p.id)
    window.location.href = url // short-lived pre-signed S3 link
  } catch (e) {
    error.value = e as Error
  } finally {
    busy.value = null
  }
}
</script>

<template>
  <header class="mb-8">
    <h1 class="text-4xl font-bold tracking-[-0.03em]">{{ t('payslips.title') }}</h1>
    <p class="mt-1.5 max-w-[62ch] text-ink-2">{{ t('payslips.lead') }}</p>
  </header>
  <ErrorBanner :error="error" />

  <div v-if="!payslips && !error" class="grid gap-8 sm:grid-cols-2 lg:grid-cols-3">
    <div v-for="n in 3" :key="n" class="skeleton h-64" />
  </div>
  <p v-else-if="payslips && payslips.length === 0" class="max-w-md rounded-md bg-paper px-5 py-8 text-ink-2 shadow-tape">{{ t('payslips.empty') }}</p>

  <div v-else class="grid gap-x-8 gap-y-12 sm:grid-cols-2 lg:grid-cols-3">
    <article v-for="(p, i) in payslips ?? []" :key="p.id" class="flex flex-col">
      <TapeStrip
        :lines="lines(p)"
        :meta="[periodLabel(p.period, locale), `${p.employeeNo} · ${p.employeeName}`]"
        :delay="i * 0.12"
      />
      <button class="key key-quiet mt-5 self-start" :disabled="!p.pdfAvailable || busy === p.id" @click="download(p)">
        <LoaderCircle v-if="busy === p.id" class="size-4 animate-spin motion-reduce:animate-none" aria-hidden="true" />
        <Download v-else class="size-4" aria-hidden="true" />{{ t('payslips.download') }}
      </button>
    </article>
  </div>
  <p v-if="payslips?.length" class="mt-10 font-mono text-[0.66rem] uppercase tracking-[0.08em] text-ink-2">{{ t('payslips.disclaimer') }}</p>
</template>
