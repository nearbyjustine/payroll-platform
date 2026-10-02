<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import { ChevronLeft, ChevronRight } from 'lucide-vue-next'

const props = defineProps<{ page: number; totalPages: number }>()
const emit = defineEmits<{ change: [page: number] }>()
const { t } = useI18n()
</script>

<template>
  <nav v-if="props.totalPages > 1" class="flex items-center justify-end gap-3 px-4 py-3" :aria-label="t('common.pagination')">
    <button class="key key-quiet px-2.5 py-1.5" :disabled="props.page === 0" :aria-label="t('common.prev')" @click="emit('change', props.page - 1)">
      <ChevronLeft class="size-4" aria-hidden="true" />
    </button>
    <span class="font-mono text-[0.7rem] text-ink-2">{{ t('common.page', { n: props.page + 1, total: props.totalPages }) }}</span>
    <button class="key key-quiet px-2.5 py-1.5" :disabled="props.page + 1 >= props.totalPages" :aria-label="t('common.next')" @click="emit('change', props.page + 1)">
      <ChevronRight class="size-4" aria-hidden="true" />
    </button>
  </nav>
</template>
