<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const { t } = useI18n()
</script>

<template>
  <section class="card">
    <template v-if="auth.isAuthenticated">
      <h1>{{ t('home.welcome', { name: auth.displayName }) }}</h1>
      <p>{{ t('home.intro') }}</p>
      <p>
        {{ t('home.roles') }}:
        <span v-for="r in auth.roles.filter((x) => ['EMPLOYEE', 'HR', 'PAYROLL_ADMIN'].includes(x))" :key="r" class="badge">{{ r }}</span>
      </p>
    </template>
    <template v-else>
      <h1>{{ t('app.title') }}</h1>
      <p>{{ t('home.intro') }}</p>
      <p class="muted">{{ t('home.signedOut') }}</p>
      <button @click="auth.login('/')">{{ t('app.signIn') }}</button>
    </template>
  </section>
</template>
