<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import { ArrowLeft, LoaderCircle } from 'lucide-vue-next'
import { Api } from '@/api/endpoints'
import { ApiError } from '@/api/client'
import type { Department, EmploymentType } from '@/api/types'
import ErrorBanner from '@/components/ErrorBanner.vue'

const props = defineProps<{ id?: string }>()
const { t } = useI18n()
const router = useRouter()
const departments = ref<Department[]>([])
const error = ref<Error | null>(null)
const fieldErrors = ref<Record<string, string>>({})
const saving = ref(false)

const form = reactive({
  employeeNo: '',
  fullName: '',
  email: '',
  username: '',
  employmentType: 'REGULAR' as EmploymentType,
  baseSalary: 25000,
  departmentId: null as number | null,
  active: true,
  version: 0,
})

onMounted(async () => {
  departments.value = await Api.departments()
  if (props.id) {
    const e = await Api.employee(Number(props.id))
    Object.assign(form, { ...e, username: e.username ?? '' })
  }
})

async function save() {
  saving.value = true
  error.value = null
  fieldErrors.value = {}
  try {
    if (props.id) await Api.updateEmployee(Number(props.id), form)
    else await Api.createEmployee(form)
    await router.push('/employees')
  } catch (e) {
    error.value = e as Error
    if (e instanceof ApiError) fieldErrors.value = e.fieldErrors
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <RouterLink to="/employees" class="mb-4 inline-flex items-center gap-1.5 text-sm text-ink-2 hover:text-ink">
    <ArrowLeft class="size-4" aria-hidden="true" />{{ t('employees.title') }}
  </RouterLink>
  <section class="max-w-2xl rounded-md bg-paper p-6 shadow-tape md:p-8">
    <h1 class="text-3xl font-bold tracking-[-0.03em]">{{ props.id ? t('employees.editTitle') : t('employees.new') }}</h1>
    <p v-if="props.id" class="mt-1.5 text-sm text-ink-2">{{ t('employees.versionNote') }}</p>
    <ErrorBanner :error="error" />
    <form class="mt-6 grid gap-5 sm:grid-cols-2" novalidate @submit.prevent="save">
      <label v-if="!props.id" class="field">{{ t('employees.no') }}
        <input v-model="form.employeeNo" class="font-mono" placeholder="E-0123" required :aria-invalid="!!fieldErrors.employeeNo" />
        <span class="err">{{ fieldErrors.employeeNo }}</span>
      </label>
      <label class="field" :class="{ 'sm:col-span-2': props.id }">{{ t('employees.name') }}
        <input v-model="form.fullName" required :aria-invalid="!!fieldErrors.fullName" />
        <span class="err">{{ fieldErrors.fullName }}</span>
      </label>
      <label class="field">{{ t('employees.email') }}
        <input v-model="form.email" type="email" required :aria-invalid="!!fieldErrors.email" />
        <span class="err">{{ fieldErrors.email }}</span>
      </label>
      <label v-if="!props.id" class="field">{{ t('employees.username') }}
        <input v-model="form.username" autocomplete="off" />
        <span class="err">{{ fieldErrors.username }}</span>
      </label>
      <label class="field">{{ t('employees.department') }}
        <select v-model="form.departmentId" required>
          <option v-for="d in departments" :key="d.id" :value="d.id">{{ d.name }}</option>
        </select>
        <span class="err">{{ fieldErrors.departmentId }}</span>
      </label>
      <label class="field">{{ t('employees.type') }}
        <select v-model="form.employmentType">
          <option value="REGULAR">{{ t('employees.REGULAR') }}</option>
          <option value="CONTRACTUAL">{{ t('employees.CONTRACTUAL') }}</option>
        </select>
      </label>
      <label class="field">{{ t('employees.salary') }}
        <input v-model.number="form.baseSalary" class="font-mono" type="number" min="1" step="0.01" required :aria-invalid="!!fieldErrors.baseSalary" />
        <span class="err">{{ fieldErrors.baseSalary }}</span>
      </label>
      <label v-if="props.id" class="flex items-center gap-2.5 self-end pb-6 text-sm">
        <input v-model="form.active" type="checkbox" class="size-4 accent-[var(--ink)]" />{{ t('employees.active') }}
      </label>
      <div class="flex gap-2 border-t border-dashed border-rule pt-5 sm:col-span-2">
        <button type="submit" class="key" :disabled="saving">
          <LoaderCircle v-if="saving" class="size-4 animate-spin motion-reduce:animate-none" aria-hidden="true" />{{ t('common.save') }}
        </button>
        <RouterLink class="key key-quiet" to="/employees">{{ t('common.cancel') }}</RouterLink>
      </div>
    </form>
  </section>
</template>
