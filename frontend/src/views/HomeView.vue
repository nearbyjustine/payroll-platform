<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { ArrowRight, FileText, LogIn, Calculator, Users } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'
import TapeStrip from '@/components/TapeStrip.vue'

const auth = useAuthStore()
const { t } = useI18n()

const demoLines = computed(() =>
  (['ana', 'hana', 'paolo'] as const).map((u) => ({ key: u, label: `${u} / ${u}123`, value: t(`home.demoRoles.${u}`) })),
)
const tasks = computed(() =>
  [
    { to: '/employees', icon: Users, label: t('home.tasks.employees'), show: auth.hasRole('HR') },
    { to: '/payroll', icon: Calculator, label: t('home.tasks.payroll'), show: auth.hasRole('PAYROLL_ADMIN') },
    { to: '/my-payslips', icon: FileText, label: t('home.tasks.payslips'), show: true },
  ].filter((x) => x.show),
)
const roles = computed(() => auth.roles.filter((r) => ['EMPLOYEE', 'HR', 'PAYROLL_ADMIN'].includes(r)))
</script>

<template>
  <section v-if="!auth.isAuthenticated" class="grid items-center gap-12 py-4 md:grid-cols-[1.2fr_minmax(280px,360px)] md:py-12">
    <div>
      <h1 class="text-5xl font-bold leading-[1.02] tracking-[-0.035em] md:text-7xl">{{ t('home.signedOutTitle') }}</h1>
      <p class="mt-5 max-w-[50ch] text-lg text-ink-2">{{ t('home.intro') }}</p>
      <button class="key mt-8 px-5 py-2.5 text-base" @click="auth.login('/')"><LogIn class="size-4" aria-hidden="true" />{{ t('app.signIn') }}</button>
    </div>
    <div class="md:rotate-[1.2deg]">
      <TapeStrip :lines="demoLines" :meta="[t('home.signedOut')]" :delay="0.15" />
    </div>
  </section>

  <section v-else class="py-2 md:py-8">
    <h1 class="text-4xl font-bold tracking-[-0.03em] md:text-5xl">{{ t('home.welcome', { name: auth.displayName }) }}</h1>
    <p class="mt-3 flex flex-wrap items-center gap-2 text-sm text-ink-2">
      {{ t('home.roles') }}
      <span v-for="r in roles" :key="r" class="stamp text-ink">{{ r.replace('_', ' ') }}</span>
    </p>
    <ul class="mt-8 max-w-2xl divide-y divide-rule border-y border-rule">
      <li v-for="task in tasks" :key="task.to">
        <RouterLink :to="task.to" class="group flex items-center gap-4 py-4 text-lg transition-colors hover:text-ink">
          <component :is="task.icon" class="size-5 text-ink-2" aria-hidden="true" />
          <span class="flex-1 font-medium">{{ task.label }}</span>
          <ArrowRight class="size-5 text-ink-2 transition-transform group-hover:translate-x-1 motion-reduce:transition-none" aria-hidden="true" />
        </RouterLink>
      </li>
    </ul>
  </section>
</template>
