<template>
  <v-icon
    class="signLanguageButton"
    icon="fas fa-hands"
    title="Gebärdensprache"
    aria-label="Gebärdensprache Hilfe aufrufen"
    @click="open"
  />
</template>

<script setup>
import { useViewControllerStore } from '@/store/viewControllerStore';
import { useTheme } from 'vuetify';

const viewControllerStore = useViewControllerStore();
const theme = useTheme();

function open() {
  console.log('SignLanguageButton clicked');
  if (import.meta.env.VITE_SIGN_LANGUAGE === 'internal') {
    // const url = `/gebaerdensprache?theme=${ theme.global.name.value === 'a11yDtsTheme' ? 'a11y' : 'default' }`;
    // window.open(url, '_blank');
    viewControllerStore.signLanguageView = true;
  } else {
    window.open(import.meta.env.VITE_SIGN_LANGUAGE, '_blank');
    return;
  }
}
</script>

<style scoped>
.signLanguageButton {
  cursor: pointer;
  color: rgba(var(--v-theme-primary), 0.8);
  bottom: 6rem;
  right: 1rem;
  position: fixed;

  @media only screen and (max-width: 780px) {
    bottom: 4rem;
    font-size: 1.5rem;
  }
}
</style>
