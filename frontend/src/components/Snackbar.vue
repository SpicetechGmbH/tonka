<script setup>
import { useSnackbar } from '../composables/useSnackbar.js';

const { snackbar, hideSnackbar } = useSnackbar();
</script>

<template>
  <v-snackbar
    v-model="snackbar.show"
    :color="snackbar.color"
  > {{ snackbar.message }} <template v-slot:actions>
      <v-btn
        color="var(--dts-color-closer)"
        variant="text"
        aria-label="Schließen"
        @click="hideSnackbar"
      > Schließen </v-btn>
    </template>
  </v-snackbar>
</template>

<style lang="scss" scoped></style>
