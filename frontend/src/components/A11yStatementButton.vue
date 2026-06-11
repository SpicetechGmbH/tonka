<template>
  <v-icon
    class="a11yStatementButton"
    icon="fas fa-bookmark"
    title="Barrierefreiheitserklärung"
    aria-label="Barrierefreiheitserklärung aufrufen"
    @click="open"
  />
</template>
<script setup>
import { useViewControllerStore } from '@/store/viewControllerStore';
import { ref } from 'vue';
import { useDisplay, useTheme } from 'vuetify';

const viewControllerStore = useViewControllerStore();
const theme = useTheme();
const { smAndDown } = useDisplay();
const isMobile = smAndDown;

const bottomPosition = ref(6);
if (isMobile.value) {
  bottomPosition.value = 4;
}
if (import.meta.env.VITE_PLAIN_LANGUAGE) {
  bottomPosition.value += 2;
}
if (import.meta.env.VITE_SIGN_LANGUAGE) {
  bottomPosition.value += 2;
}
bottomPosition.value += 'rem';

function open() {
  console.log('A11yStatementButton clicked');
  if (import.meta.env.VITE_A11Y_STATEMENT === 'internal') {
    // const url = `/barrierefreiheitserklaerung?theme=${ theme.global.name.value === 'a11yDtsTheme' ? 'a11y' : 'default' }`;
    // window.open(url, '_blank');
    viewControllerStore.a11yStatementView = true;
  } else {
    window.open(import.meta.env.VITE_A11Y_STATEMENT, '_blank');
    return;
  }
}
</script>
<style scoped>
.a11yStatementButton {
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
