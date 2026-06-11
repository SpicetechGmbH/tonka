<script setup>
import { computed, onMounted } from 'vue';
import { RouterView, useRoute } from 'vue-router';
import Snackbar from './components/Snackbar.vue';
import useMapStore from './store/mapStore';
import { useSearchQueryStore } from './store/searchQueryStore';

const route = useRoute();
const routeClass = computed(() => route.name?.toLowerCase());

const searchQueryStore = useSearchQueryStore();
const mapStore = useMapStore();

onMounted(() => {
  // Load inital data for lemmas and articles
  searchQueryStore.fetchFeaturedArticles();
  // Load data for historic and thematic maps
  mapStore.fetchHistoricMaps();
  mapStore.fetchThematicMaps();
});

</script>

<template>
  <div :class="routeClass">
    <RouterView />
  </div>
  <Snackbar />
</template>

<style scoped>
.printarticle {
  background-color: #a3a3a3;
  height: 100dvh;
  overflow-y: hidden;
}
</style>
