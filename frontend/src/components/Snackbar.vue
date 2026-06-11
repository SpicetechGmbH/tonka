<script setup>
import { useSnackbar } from '@/composables/useSnackbar.js';
import { useDisplay } from 'vuetify';

const { smAndDown } = useDisplay();

const { snackbar, hideSnackbar } = useSnackbar();

function handleCallback() {
  if (snackbar.callback) {
    snackbar.callback();
  }
  hideSnackbar();
}
</script>

<template>
  <v-snackbar
    v-model="snackbar.show"
    :color="snackbar.color"
    :timeout="snackbar.timeout"
    class="snackbar--above-timeline"
  > {{ snackbar.message }} <template v-slot:actions>
      <v-btn
        v-if="snackbar.callback"
        variant="outlined"
        aria-label="Aktion ausführen"
        :max-width="smAndDown ? '100px' : ''"
        @click="handleCallback"
      >{{ snackbar.callbackButton }}</v-btn>
      <v-btn
        variant="icon"
        icon="mdi mdi-close-circle"
        aria-label="Schließen"
        @click="hideSnackbar"
      >
      </v-btn>
    </template>
  </v-snackbar>
</template>

<style lang="scss" scoped>
/* keep snackbar above the timeline (timeline height ≈ 110px) */
.snackbar--above-timeline {
  position: fixed !important;
  left: 50% !important;
  transform: translateX(-50%) !important;
  bottom: calc(110px + 12px) !important; /* timeline height + gap */
  z-index: 21000 !important;
  max-width: calc(100% - 32px) !important;
  
  @media screen and (max-width: 780px) {
    max-width: calc(100% - 16px) !important;
    transform: none !important;
    left: 10% !important;
    right: 10% !important;
    bottom: 16px !important;
  }
}
</style>
