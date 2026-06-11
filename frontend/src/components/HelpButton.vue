<template>
  <router-link
    target="_blank"
    :to="{ path: '/Hilfe', query: { theme: theme.global.name.value === 'a11yDtsTheme' ? 'a11y' : 'default' } }"
    title="Hilfeseite"
    aria-label="Hilfeseite aufrufen"
  >
    <v-icon
      class="iconQuestion"
      icon="fa fa-question-circle"
    />
  </router-link>
</template>

<script setup>
import { ref } from 'vue';
import { useDisplay, useTheme } from 'vuetify';

const theme = useTheme();
const { smAndDown } = useDisplay();
const isMobile = smAndDown;

const bottomPosition = ref(6);
if (isMobile.value) {
  bottomPosition.value = 4;
}
if (import.meta.env.VITE_A11Y_STATEMENT) {
  bottomPosition.value += 2;
}
if (import.meta.env.VITE_PLAIN_LANGUAGE) {
  bottomPosition.value += 2;
}
if (import.meta.env.VITE_SIGN_LANGUAGE) {
  bottomPosition.value += 2;
}
bottomPosition.value += 'rem';

</script>

<style scoped>
.iconQuestion {
  cursor: pointer;
  color: rgba(var(--v-theme-primary), 0.8);
  bottom: v-bind(bottomPosition);
  right: 1rem;
  position: fixed;

  @media only screen and (max-width: 780px) {
    bottom: v-bind(bottomPosition);
    font-size: 1.5rem;
  }
}
</style>
