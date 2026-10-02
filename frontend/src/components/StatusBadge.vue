<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import { CircleCheck, CircleX, Clock, LoaderCircle } from 'lucide-vue-next'
import type { RunStatus } from '@/api/types'

defineProps<{ status: RunStatus }>()
const { t } = useI18n()
const icons = { PENDING: Clock, PROCESSING: LoaderCircle, COMPLETED: CircleCheck, FAILED: CircleX }
</script>

<template>
  <span
    class="stamp"
    :class="{ 'text-posted': status === 'COMPLETED', 'text-ribbon': status === 'FAILED', 'text-ink-2': status === 'PENDING' || status === 'PROCESSING' }"
  >
    <component
      :is="icons[status]"
      class="size-3"
      :class="{ 'animate-spin motion-reduce:animate-none': status === 'PROCESSING' }"
      aria-hidden="true"
    />
    {{ t(`payroll.${status}`) }}
  </span>
</template>
