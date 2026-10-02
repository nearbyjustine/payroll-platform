<script setup lang="ts">
import { motion } from 'motion-v'

export interface PrintedLine {
  key: string
  label: string
  value: string
  op?: string
  tone?: 'neg' | 'total'
}

/**
 * A strip of printed calculator tape. Lines "print": each unrolls downward from the print head
 * (clip-path), one after another, the way paper feeds out of a printing calculator.
 */
withDefaults(defineProps<{ lines: PrintedLine[]; meta?: string[]; delay?: number }>(), { delay: 0 })
</script>

<template>
  <div class="tape">
    <div v-if="meta?.length" class="mb-2.5 space-y-0.5 border-b border-dashed border-rule pb-2.5">
      <div v-for="m in meta" :key="m" class="tape-meta">{{ m }}</div>
    </div>
    <motion.div
      v-for="(line, i) in lines"
      :key="line.key"
      class="tape-line"
      :class="line.tone"
      :initial="{ clipPath: 'inset(0 0 100% 0)' }"
      :animate="{ clipPath: 'inset(0 0 0% 0)' }"
      :transition="{ duration: 0.22, ease: [0.16, 1, 0.3, 1], delay: delay + Math.min(i, 10) * 0.06 }"
    >
      <span class="truncate">{{ line.label }}</span>
      <span><slot :name="`value-${line.key}`">{{ line.value }}</slot></span>
      <span class="op" aria-hidden="true">{{ line.op ?? '' }}</span>
    </motion.div>
    <slot />
  </div>
</template>
