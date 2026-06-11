<template>
  <v-icon
    class="plainLanguageButton"
    icon="fas fa-book-open-reader"
    title="Leichte Sprache"
    aria-label="Leichte Sprache Hilfe aufrufen"
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
if (import.meta.env.VITE_SIGN_LANGUAGE) {
  bottomPosition.value += 2;
}
bottomPosition.value += 'rem';

function open() {
  console.log('PlainLanguageButton clicked');
  if (import.meta.env.VITE_PLAIN_LANGUAGE === 'internal') {
    // const url = `/leichte-sprache?theme=${ theme.global.name.value === 'a11yDtsTheme' ? 'a11y' : 'default' }`;
    // window.open(url, '_blank');
    viewControllerStore.plainLanguageView = true;
  } else {
    window.open(import.meta.env.VITE_PLAIN_LANGUAGE, '_blank');
    return;
  }
}
</script>
<style scoped>
.plainLanguageButton {
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
