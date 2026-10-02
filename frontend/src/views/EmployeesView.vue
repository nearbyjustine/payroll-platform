<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { motion } from 'motion-v'
import { Pencil, Plus, Search } from 'lucide-vue-next'
import { Api } from '@/api/endpoints'
import type { Department, Employee, Page } from '@/api/types'
import { useAuthStore } from '@/stores/auth'
import { money } from '@/utils/format'
import ErrorBanner from '@/components/ErrorBanner.vue'
import Pager from '@/components/Pager.vue'

const { t } = useI18n()
const auth = useAuthStore()
const q = ref('')
const department = ref<number | null>(null)
const page = ref(0)
const data = ref<Page<Employee> | null>(null)
const departments = ref<Department[]>([])
const loading = ref(false)
const error = ref<Error | null>(null)

async function load() {
  loading.value = true
  error.value = null
  try {
    data.value = await Api.employees({ q: q.value, department: department.value, page: page.value })
  } catch (e) {
    error.value = e as Error
  } finally {
    loading.value = false
  }
}

// Debounce typing so we don't send a request per keystroke.
let timer: ReturnType<typeof setTimeout> | undefined
watch(q, () => {
  clearTimeout(timer)
  timer = setTimeout(() => {
    page.value = 0
    load()
  }, 300)
})
watch(department, () => {
  page.value = 0
  load()
})

onMounted(async () => {
  departments.value = await Api.departments()
  await load()
})
</script>

<template>
  <header class="mb-6 flex flex-wrap items-end justify-between gap-4">
    <div>
      <h1 class="text-4xl font-bold tracking-[-0.03em]">{{ t('employees.title') }}</h1>
      <p class="mt-1.5 text-ink-2">{{ t('employees.lead') }}</p>
    </div>
    <RouterLink v-if="auth.hasRole('HR')" class="key" to="/employees/new"><Plus class="size-4" aria-hidden="true" />{{ t('employees.new') }}</RouterLink>
  </header>

  <div class="mb-4 flex flex-wrap gap-3">
    <label class="relative min-w-60 flex-1">
      <span class="sr-only">{{ t('common.search') }}</span>
      <Search class="pointer-events-none absolute left-3 top-1/2 size-4 -translate-y-1/2 text-ink-2" aria-hidden="true" />
      <input v-model="q" class="input w-full pl-9" :placeholder="t('employees.searchHint')" />
    </label>
    <label>
      <span class="sr-only">{{ t('employees.department') }}</span>
      <select v-model="department" class="input">
        <option :value="null">{{ t('common.all') }}</option>
        <option v-for="d in departments" :key="d.id" :value="d.id">{{ d.name }}</option>
      </select>
    </label>
  </div>

  <ErrorBanner :error="error" />

  <div class="overflow-hidden rounded-md bg-paper shadow-tape">
    <div class="overflow-x-auto">
      <table class="ledger">
        <thead>
          <tr>
            <th>{{ t('employees.no') }}</th>
            <th>{{ t('employees.name') }}</th>
            <th class="hidden md:table-cell">{{ t('employees.department') }}</th>
            <th class="hidden sm:table-cell">{{ t('employees.type') }}</th>
            <th class="!text-right">{{ t('employees.salary') }}</th>
            <th v-if="auth.hasRole('HR')"><span class="sr-only">{{ t('employees.edit') }}</span></th>
          </tr>
        </thead>
        <tbody v-if="!data">
          <tr v-for="n in 8" :key="n">
            <td colspan="6"><div class="skeleton h-4" /></td>
          </tr>
        </tbody>
        <tbody v-else :class="{ 'opacity-60 transition-opacity': loading }">
          <motion.tr
            v-for="(e, i) in data.content"
            :key="e.id"
            :initial="{ opacity: 0 }"
            :animate="{ opacity: 1 }"
            :transition="{ delay: Math.min(i, 12) * 0.015, duration: 0.18 }"
            :class="{ 'text-ink-2 line-through decoration-ink-2/60': !e.active }"
          >
            <td class="font-mono text-[0.75rem]">{{ e.employeeNo }}</td>
            <td class="font-medium">{{ e.fullName }}</td>
            <td class="hidden md:table-cell">{{ e.departmentName }}</td>
            <td class="hidden text-ink-2 sm:table-cell">{{ t(`employees.${e.employmentType}`) }}</td>
            <td class="num">{{ money(e.baseSalary) }}</td>
            <td v-if="auth.hasRole('HR')" class="w-12 text-right">
              <RouterLink :to="`/employees/${e.id}`" class="inline-flex rounded-md p-1.5 text-ink-2 hover:bg-desk-2 hover:text-ink" :aria-label="`${t('employees.edit')} ${e.fullName}`">
                <Pencil class="size-4" aria-hidden="true" />
              </RouterLink>
            </td>
          </motion.tr>
          <tr v-if="data.content.length === 0">
            <td colspan="6" class="py-12 text-center text-ink-2">{{ t('employees.empty') }}</td>
          </tr>
        </tbody>
      </table>
    </div>
    <Pager v-if="data" :page="data.page.number" :total-pages="data.page.totalPages" @change="(p) => { page = p; load() }" />
  </div>
</template>
