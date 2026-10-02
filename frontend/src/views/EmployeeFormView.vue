<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
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
    if (props.id) {
      await Api.updateEmployee(Number(props.id), form)
    } else {
      await Api.createEmployee(form)
    }
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
  <section class="card narrow">
    <h1>{{ props.id ? t('employees.edit') : t('employees.new') }}</h1>
    <ErrorBanner :error="error" />
    <form class="form" @submit.prevent="save">
      <label v-if="!props.id">{{ t('employees.no') }}
        <input v-model="form.employeeNo" placeholder="E-0123" required />
        <small class="field-error">{{ fieldErrors.employeeNo }}</small>
      </label>
      <label>{{ t('employees.name') }}
        <input v-model="form.fullName" required />
        <small class="field-error">{{ fieldErrors.fullName }}</small>
      </label>
      <label>{{ t('employees.email') }}
        <input v-model="form.email" type="email" required />
        <small class="field-error">{{ fieldErrors.email }}</small>
      </label>
      <label v-if="!props.id">{{ t('employees.username') }}
        <input v-model="form.username" />
      </label>
      <label>{{ t('employees.department') }}
        <select v-model="form.departmentId" required>
          <option v-for="d in departments" :key="d.id" :value="d.id">{{ d.name }}</option>
        </select>
      </label>
      <label>{{ t('employees.type') }}
        <select v-model="form.employmentType">
          <option value="REGULAR">{{ t('employees.REGULAR') }}</option>
          <option value="CONTRACTUAL">{{ t('employees.CONTRACTUAL') }}</option>
        </select>
      </label>
      <label>{{ t('employees.salary') }}
        <input v-model.number="form.baseSalary" type="number" min="1" step="0.01" required />
        <small class="field-error">{{ fieldErrors.baseSalary }}</small>
      </label>
      <label v-if="props.id" class="check"><input v-model="form.active" type="checkbox" /> {{ t('employees.active') }}</label>
      <div class="row">
        <button type="submit" :disabled="saving">{{ t('common.save') }}</button>
        <RouterLink class="button ghost" to="/employees">{{ t('common.cancel') }}</RouterLink>
      </div>
    </form>
  </section>
</template>
