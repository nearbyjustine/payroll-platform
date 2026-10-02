<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import type { RunStatus } from '@/api/types'

defineProps<{ status: RunStatus }>()
const { t } = useI18n()
</script>

<template>
  <span class="badge" :class="status.toLowerCase()">{{ t(`payroll.${status}`) }}</span>
</template>
