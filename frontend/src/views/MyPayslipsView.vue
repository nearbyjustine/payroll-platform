<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { Api } from '@/api/endpoints'
import type { Payslip } from '@/api/types'
import { money, periodLabel } from '@/utils/format'
import ErrorBanner from '@/components/ErrorBanner.vue'

const { t, locale } = useI18n()
const payslips = ref<Payslip[] | null>(null)
const error = ref<Error | null>(null)

onMounted(async () => {
  try {
    payslips.value = await Api.myPayslips()
  } catch (e) {
    error.value = e as Error
  }
})

async function download(p: Payslip) {
  try {
    const { url } = await Api.downloadUrl(p.id)
    window.location.href = url   // short-lived pre-signed S3 URL
  } catch (e) {
    error.value = e as Error
  }
}
</script>

<template>
  <section class="card">
    <h1>{{ t('payslips.title') }}</h1>
    <ErrorBanner :error="error" />
    <p v-if="payslips && payslips.length === 0" class="muted">{{ t('payslips.empty') }}</p>
    <div class="slips">
      <article v-for="p in payslips ?? []" :key="p.id" class="slip">
        <header class="row between">
          <h2>{{ periodLabel(p.period, locale) }}</h2>
          <button :disabled="!p.pdfAvailable" @click="download(p)">{{ t('payslips.download') }}</button>
        </header>
        <dl>
          <div class="line strong"><dt>{{ t('payroll.gross') }}</dt><dd>{{ money(p.gross) }}</dd></div>
          <div v-for="l in p.lines" :key="l.code" class="line"><dt>{{ l.label }}</dt><dd>({{ money(l.amount) }})</dd></div>
          <div class="line strong total"><dt>{{ t('payroll.net') }}</dt><dd>{{ money(p.net) }}</dd></div>
        </dl>
      </article>
    </div>
  </section>
</template>
