<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { AnimatePresence, MotionConfig, motion } from 'motion-v'
import { LogIn, LogOut, Monitor, Moon, ReceiptText, Sun } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'
import { cycleTheme, themePref } from '@/stores/theme'

const auth = useAuthStore()
const { t, locale } = useI18n()

const links = computed(() =>
  [
    { to: '/employees', label: t('nav.employees'), show: auth.hasRole('HR', 'PAYROLL_ADMIN') },
    { to: '/payroll', label: t('nav.payroll'), show: auth.hasRole('PAYROLL_ADMIN') },
    { to: '/my-payslips', label: t('nav.myPayslips'), show: auth.isAuthenticated },
  ].filter((l) => l.show),
)
const themeIcon = computed(() => ({ system: Monitor, dark: Moon, light: Sun })[themePref.value])

function setLocale(value: string) {
  locale.value = value
  document.documentElement.lang = value
  try {
    localStorage.setItem('locale', value)
  } catch {
    /* private mode */
  }
}
</script>

<template>
  <MotionConfig reduced-motion="user">
    <header class="border-b border-rule">
      <div class="mx-auto flex max-w-[1180px] flex-wrap items-center gap-x-8 gap-y-2 px-4 py-3.5 sm:px-6">
        <RouterLink to="/" class="flex items-center gap-2">
          <ReceiptText class="size-5" aria-hidden="true" />
          <span class="text-[1.05rem] font-bold tracking-[-0.01em]">{{ t('app.title') }}</span>
        </RouterLink>
        <nav class="order-last flex w-full gap-1 overflow-x-auto sm:order-none sm:w-auto" :aria-label="t('app.title')">
          <RouterLink
            v-for="l in links"
            :key="l.to"
            :to="l.to"
            class="whitespace-nowrap rounded-md px-3 py-1.5 text-sm font-medium text-ink-2 transition-colors hover:bg-desk-2 hover:text-ink"
            active-class="!bg-ink !text-paper"
          >
            {{ l.label }}
          </RouterLink>
        </nav>
        <div class="ml-auto flex items-center gap-1">
          <label class="sr-only" for="lang">{{ t('app.language') }}</label>
          <select
            id="lang"
            class="cursor-pointer rounded-md bg-transparent px-1.5 py-1 text-xs font-semibold uppercase hover:bg-desk-2"
            :value="locale"
            @change="setLocale(($event.target as HTMLSelectElement).value)"
          >
            <option value="en">EN</option>
            <option value="fil">FIL</option>
          </select>
          <button class="rounded-md p-2 hover:bg-desk-2" :title="t(`app.theme.${themePref}`)" :aria-label="t(`app.theme.${themePref}`)" @click="cycleTheme">
            <component :is="themeIcon" class="size-4" aria-hidden="true" />
          </button>
          <template v-if="auth.isAuthenticated">
            <span class="ml-1 hidden text-sm text-ink-2 sm:inline">{{ auth.displayName }}</span>
            <button class="rounded-md p-2 hover:bg-desk-2" :title="t('app.signOut')" :aria-label="t('app.signOut')" @click="auth.logout()">
              <LogOut class="size-4" aria-hidden="true" />
            </button>
          </template>
          <button v-else class="key ml-1 py-1.5" @click="auth.login('/')"><LogIn class="size-4" aria-hidden="true" />{{ t('app.signIn') }}</button>
        </div>
      </div>
    </header>

    <main class="mx-auto max-w-[1180px] px-4 py-7 sm:px-6 sm:py-10">
      <RouterView v-slot="{ Component, route }">
        <AnimatePresence mode="wait">
          <motion.div
            :key="route.path"
            :initial="{ opacity: 0, y: 6 }"
            :animate="{ opacity: 1, y: 0 }"
            :exit="{ opacity: 0, transition: { duration: 0.1 } }"
            :transition="{ duration: 0.18, ease: [0.16, 1, 0.3, 1] }"
          >
            <component :is="Component" />
          </motion.div>
        </AnimatePresence>
      </RouterView>
    </main>
  </MotionConfig>
</template>
