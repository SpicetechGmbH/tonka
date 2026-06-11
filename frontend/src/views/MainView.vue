<script setup>
import { onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { useDisplay, useTheme } from 'vuetify';
import A11yStatementDialog from '../components/A11yStatementDialog.vue';
import ArticleComponent from '../components/ArticleComponent.vue';
import Footer from '../components/Footer.vue';
import Header from '../components/Header.vue';
import MapSettings from '../components/MapSettings.vue';
import PlainLanguageDialog from '../components/PlainLanguageDialog.vue';
import Searchbar from '../components/Searchbar.vue';
import SignLanguageDialog from '../components/SignLanguageDialog.vue';
import StartMessage from '../components/StartMessage.vue';
import TheMap from '../components/TheMap.vue';
import Timeline from '../components/Timeline.vue';
import { useA11y } from '../composables/a11y';
import useMapStore from '../store/mapStore';
import { useViewControllerStore } from '../store/viewControllerStore';

const route = useRoute();
const theme = useTheme();

const { smAndDown } = useDisplay();
const { activateA11yTheme } = useA11y();

const mapStore = useMapStore();
const viewControllerStore = useViewControllerStore();
const isMobile = smAndDown;

onMounted(() => {
  if (route.query.theme === 'a11y' || route.query.a11yTheme === 'true') {
    activateA11yTheme();
  }
});

</script>

<template>
  <main>
    <TheMap />
    <div
      id="headerWrapper"
      style="display: flex; flex-direction: column; position: fixed; left: 0; top: 0;"
    >
      <Header />
    </div>
    <div
      id="searchbarWrapper"
      class="mt-2"
    >
      <Searchbar />
      <ArticleComponent v-if="mapStore.showPoints && (viewControllerStore.currentView === 'article' || viewControllerStore.currentView === 'ort')" />
      <MapSettings v-show="viewControllerStore.currentView === 'mapSettings'" />
    </div>
    <Timeline />
    <Footer />
    <StartMessage />
    <A11yStatementDialog />
    <PlainLanguageDialog />
    <SignLanguageDialog />
  </main>
</template>

<style lang="scss">
#searchbarWrapper {
  position: fixed;
  right: 8px;
  max-width: var(--dts-panel-max-width);
  width: 100%;
  box-sizing: border-box;
  z-index: 100;

  @media only screen and (max-width: 780px) {
    position: fixed;
    left: 8px;
    right: 8px;
    max-width: none;
    width: auto;
  }
}
</style>
