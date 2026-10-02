<script setup lang="ts">
import { useI18n } from 'vue-i18n'

const props = defineProps<{ page: number; totalPages: number }>()
const emit = defineEmits<{ change: [page: number] }>()
const { t } = useI18n()
</script>

<template>
  <div v-if="props.totalPages > 1" class="pager">
    <button class="ghost" :disabled="props.page === 0" @click="emit('change', props.page - 1)">{{ t('common.prev') }}</button>
    <span>{{ t('common.page', { n: props.page + 1, total: props.totalPages }) }}</span>
    <button class="ghost" :disabled="props.page + 1 >= props.totalPages" @click="emit('change', props.page + 1)">{{ t('common.next') }}</button>
  </div>
</template>
