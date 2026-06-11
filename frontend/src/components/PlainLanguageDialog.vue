<template>
  <v-dialog
    v-model="viewControllerStore.plainLanguageView"
    scrollable
    max-width="600px"
  >
    <v-card>
      <v-toolbar class="toolbar">
        <template #prepend>
          <v-icon
            class="mx-3"
            icon="fas fa-book-open-reader"
            aria-label="Icon Leichte Sprache"
          >
          </v-icon>
        </template>
        <v-toolbar-title class="dts-text"> Leichte Sprache </v-toolbar-title>
        <template #append>
          <v-btn
            @click="viewControllerStore.plainLanguageView = false"
            tabindex="0"
            aria-label="Schließen"
          ><v-icon color="rgba(var(--v-theme-closer), 0.4)">mdi mdi-close-circle</v-icon></v-btn>
        </template>
      </v-toolbar>
      <v-card-text>
        <p>Hier könnte die Erklärung in einfacher Sprache stehen.</p>
        <p>Diese Seite ist derzeit noch in Bearbeitung.</p>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { useViewControllerStore } from '@/store/viewControllerStore';
const viewControllerStore = useViewControllerStore();
</script>

<style scoped></style>
