<script setup>
import { computed } from 'vue';
import { RouterView, useRoute } from 'vue-router';
import { useTheme } from 'vuetify';
import Snackbar from './components/Snackbar.vue';

const theme = useTheme();
const route = useRoute();
const routeClass = computed(() => route.name?.toLowerCase());

</script>

<template>
  <div :class="routeClass">
    <RouterView />
  </div>
  <Snackbar />
</template>

<style scoped>
@media (min-width: 1024px) {}

.printarticle {
  background-color: #a3a3a3;
}
</style>
