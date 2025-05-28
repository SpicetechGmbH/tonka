<script setup>
import { ref } from 'vue';
import ArticleComponent from '../components/ArticleComponent.vue';
import StartMessage from '../components/StartMessage.vue';
import FooterComponent from '../components/FooterComponent.vue';
import HeaderComponent from '../components/HeaderComponent.vue';
import Karteneinstellungen from '../components/Karteneinstellungen.vue';
import SearchbarComponent from '../components/SearchbarComponent.vue';
import TheMap from '../components/TheMap.vue';
import TimelineComponent from '../components/TimelineComponent.vue';
import { useViewControllerStore } from '../store/viewControllerStore';
import { useDisplay } from 'vuetify';

const viewControllerStore = useViewControllerStore();
const { smAndDown } = useDisplay;
const isMobile = smAndDown;

const setOpenPanelValue = ref(false);
const setOpenQueryPanelValue = ref(false);
const toggleQueryPanel = ref(false);

</script>
<template>
  <main>
    <TheMap />
    <div style="display: flex; flex-direction: column; position: fixed; left: 0; top: 0;">
      <HeaderComponent />
    </div>
    <div style="display: flex; flex-direction: column; position: fixed; right: 0; top: 0; max-width: 50%;">
      <SearchbarComponent
        v-model:toggleQueryPanel="toggleQueryPanel"
        v-model:setOpenPanelValue="setOpenPanelValue"
        v-model:setOpenQueryPanelValue="setOpenQueryPanelValue"
      />
      <ArticleComponent
        v-model:toggleQueryPanel="toggleQueryPanel"
        v-model:setOpenPanelValue="setOpenPanelValue"
        v-model:setOpenQueryPanelValue="setOpenQueryPanelValue"
        v-show="viewControllerStore.currentView === 'article'"
      />
      <Karteneinstellungen v-show="viewControllerStore.currentView === 'mapSettings'" />
    </div>
    <TimelineComponent />
    <FooterComponent />
    <StartMessage />
  </main>
</template>
<style lang="scss">
.container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  align-items: flex-start;

  .header-column {
    padding: 0;
  }
}
</style>
