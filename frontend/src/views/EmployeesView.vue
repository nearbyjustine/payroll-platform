<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
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
  <section class="card">
    <div class="row between">
      <h1>{{ t('employees.title') }}</h1>
      <RouterLink v-if="auth.hasRole('HR')" class="button" to="/employees/new">{{ t('employees.new') }}</RouterLink>
    </div>
    <div class="filters">
      <input v-model="q" :placeholder="t('employees.searchHint')" :aria-label="t('common.search')" />
      <select v-model="department" :aria-label="t('employees.department')">
        <option :value="null">{{ t('common.all') }}</option>
        <option v-for="d in departments" :key="d.id" :value="d.id">{{ d.name }}</option>
      </select>
    </div>
    <ErrorBanner :error="error" />
    <p v-if="loading && !data" class="muted">{{ t('common.loading') }}</p>
    <div v-if="data" class="table-wrap">
      <table>
        <thead>
          <tr>
            <th>{{ t('employees.no') }}</th>
            <th>{{ t('employees.name') }}</th>
            <th>{{ t('employees.department') }}</th>
            <th>{{ t('employees.type') }}</th>
            <th class="num">{{ t('employees.salary') }}</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="e in data.content" :key="e.id" :class="{ inactive: !e.active }">
            <td>{{ e.employeeNo }}</td>
            <td>{{ e.fullName }}</td>
            <td>{{ e.departmentName }}</td>
            <td>{{ t(`employees.${e.employmentType}`) }}</td>
            <td class="num">{{ money(e.baseSalary) }}</td>
            <td><RouterLink v-if="auth.hasRole('HR')" :to="`/employees/${e.id}`">{{ t('employees.edit') }}</RouterLink></td>
          </tr>
          <tr v-if="data.content.length === 0">
            <td colspan="6" class="muted">{{ t('common.none') }}</td>
          </tr>
        </tbody>
      </table>
    </div>
    <Pager v-if="data" :page="data.page.number" :total-pages="data.page.totalPages" @change="(p) => { page = p; load() }" />
  </section>
</template>
