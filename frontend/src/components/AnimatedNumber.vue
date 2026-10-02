<script setup lang="ts">
import { onBeforeUnmount, ref, watch } from 'vue'
import { animate } from 'motion-v'
import { money } from '@/utils/format'

/** Rolls a figure to its new value, like a calculator display settling. Reduced motion: jumps instantly. */
const props = withDefaults(defineProps<{ value: number; format?: 'money' | 'int' }>(), { format: 'money' })
const shown = ref(Number(props.value))
const reduce = typeof window !== 'undefined' && window.matchMedia('(prefers-reduced-motion: reduce)').matches
let controls: { stop: () => void } | undefined

watch(
  () => Number(props.value),
  (to, from) => {
    controls?.stop()
    if (reduce) {
      shown.value = to
      return
    }
    controls = animate(from, to, { duration: 0.6, ease: [0.16, 1, 0.3, 1], onUpdate: (v: number) => (shown.value = v) })
  },
)
onBeforeUnmount(() => controls?.stop())
</script>

<template>
  <span class="tabular-nums">{{ format === 'money' ? money(shown) : Math.round(shown).toLocaleString('en-PH') }}</span>
</template>
