<template>
  <div
    v-if="(viewControllerStore.currentView === 'result' || viewControllerStore.currentView === 'mapSettings')"
    class="panel"
  >
    <v-btn
      v-if="!searchStore.showResults"
      width="100%"
      prepend-icon="fa fa-caret-down"
      @click="searchStore.showResults = true"
    >Suchergebnisse</v-btn>
    <v-expansion-panels
      v-if="searchStore.showResults"
      variant="accordion"
    >
      <template v-for="(panelData) in panels">
        <v-expansion-panel>
          <template #title="expanded">
            <v-row no-gutters>
              <v-col
                class="d-flex justify-start"
                cols="4"
              > {{ panelData.title }} </v-col>
              <v-col cols="8"> Ergebnisse: {{ panelData.amount }} <!-- <span
                  v-if="expanded"
                > expanded </span>
                <span
                  v-else
                > collapsed </span> -->
              </v-col>
            </v-row>
          </template #title>
          <v-expansion-panel-text>
            <div v-if="panelData.type === 'artikel'">
              <span class="searchTtile">Anzahl der Suchergebnisse: {{ panelData.data.length }}</span>
              <hr><br>
              <div v-for="(data) in panelData.data">
                <a
                  class="titleHeading pointer"
                  :aria-label="'Artikel ' + data.title + ' anzeigen'"
                  tabindex="0"
                  @click="showArticle(data.id)"
                  @keyup.enter="showArticle(data.id)"
                >
                  <v-icon
                    :icon="getIconByLemmaType(data.lemma_type)"
                    class="panel-icons"
                  /> {{ data.title }} </a>
                <p>{{ data.abstract }}</p>
              </div>
            </div>
            <div v-if="panelData.type === 'illustration'">
              <div v-for="(data) in panelData.data">
                <div class="illustration-div">
                  <v-icon
                    icon="fa fa-image"
                    class="icon-image"
                  />
                  <router-link
                    target="_blank"
                    :to='`/illustration/${data.id}`'
                  >
                    <img
                      class="image"
                      :src="getImageURL(data.thumbnail_file_name)"
                    >
                  </router-link>
                  <span class="description">
                    <span class="pointer">
                      <router-link
                        target="_blank"
                        :to='`/illustration/${data.id}`'
                      >
                        <b>{{ data.title }}</b>
                      </router-link> In Artikel </span>
                    <span
                      @click="showArticle(data.lemma_id)"
                      class="pointer"
                      tabindex="0"
                    >
                      <v-icon
                        class="panel-icons"
                        :icon="getIconByLemmaType(data.lemma_type)"
                      />
                      <b v-if="data.timeline_title !== null">{{ data.timeline_title }}</b>
                      <b v-else>{{ data.lemma_title }}</b>
                    </span>
                  </span>
                </div>
              </div>
            </div>
            <div v-if="panelData.type === 'orte'">
              <div v-for="(data) in panelData.data">
                <v-icon icon="fa fa-spinner panel-icons" /> <span
                  tabindex="0"
                  class="pointer"
                  @click=""
                ><b>{{ data.internal_name }}</b></span> in Artikel <span
                  tabindex="0"
                  class="pointer"
                  @click="showArticle(data.lemma_id)"
                >
                  <v-icon
                    :icon="getIconByLemmaType(data.lemma_type)"
                    class="panel-icons"
                  />
                  <b>{{ data.timeline_title }}</b>
                </span>
                <p>{{ data.location_relevance }}</p>
              </div>
            </div>
          </v-expansion-panel-text>
        </v-expansion-panel>
      </template>
    </v-expansion-panels>
    <v-btn
      v-if="searchStore.showResults"
      width="100%"
      prepend-icon="fa fa-caret-up"
      @click="searchStore.showResults = false"
    >Suchergebnisse</v-btn>
  </div>
</template>
<script setup>
import { ref, computed, watch } from 'vue';
import { getIconByLemmaType } from '../services/getLemmaIconByType';
import { useLemmaStore } from '../store/lemmaStore';
import { useViewControllerStore } from '../store/viewControllerStore';
import searchQueryStore from '../store/searchQueryStore';

const emit = defineEmits([
  'update:closeQueryPanel'
]);

const props = defineProps({
  closeQueryPanel: {
    type: Boolean,
    default: false
  },
});

const lemmaStore = useLemmaStore();
const lemma = computed(() => lemmaStore.lemma);
const viewControllerStore = useViewControllerStore();

const searchStore = searchQueryStore();
const localQueryResult = computed(() => searchStore.queryResult);
const activeQueryFilter = computed(() => searchStore.getQueryArtikelsFilterDataByLemmaType);

const panels = computed(() => [
  {
    type: 'artikel',
    title: 'Artikel',
    amount: activeQueryFilter.value.length,
    data: activeQueryFilter.value,
  },
  {
    type: 'illustration',
    title: 'Illustrationen',
    amount: localQueryResult.value.illustration.length,
    data: localQueryResult.value.illustration,
  },
  {
    type: 'orte',
    title: 'Mit Artikeln verknüpfte Orte',
    amount: localQueryResult.value.orte.length,
    data: localQueryResult.value.orte,
  },
]);

watch(() => viewControllerStore.currentView, (newView) => {
  if (newView === 'mapSettings') {
    searchStore.showResults = false;
  };
});

function getImageURL(fileName) {
  return `/img/${ fileName }`;
};

async function showArticle(lemmaId) {
  try {
    await lemmaStore.fetchArticle(lemmaId);
    emit('update:closeQueryPanel', false)
    viewControllerStore.setCurrentView('article');
  } catch (error) {
    console.error(error);
  };
};
</script>
