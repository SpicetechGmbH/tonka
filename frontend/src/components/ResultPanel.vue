<template>
  <div
    v-show="(viewControllerStore.currentView === 'result' || viewControllerStore.currentView === 'mapSettings')"
    class="panel"
  >
    <v-btn
      v-if="!searchStore.showResults"
      width="100%"
      prepend-icon="fa fa-chevron-down"
      @click="searchStore.showResults = true"
    >Suchergebnisse</v-btn>
    <v-expansion-panels
      v-show="searchStore.showResults"
      variant="accordion"
    >
      <div
        v-if="searchStore.alternativeQuery"
        class="alternative-query"
      ><span> Meinten Sie: </span>
        <a
          class="pointer link"
          @click="searchStore.query = searchStore.alternativeQuery; searchStore.search()"
          @keyup.enter="searchStore.query = searchStore.alternativeQuery; searchStore.search()"
          tabindex="0"
        > {{ searchStore.alternativeQuery }} </a>
      </div>
      <div
        v-if="panels.every(panel => panel.amount == 0)"
        class="no-results"
      >
        <p class="dts-text">Keine Ergebnisse gefunden.</p>
      </div>
      <template v-for="(panelData) in panels">
        <v-expansion-panel
          v-if="panelData.amount > 0"
          class="dts-text"
          :static="smAndDown"
        >
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
              <!-- <span class="searchTtile">Anzahl der Suchergebnisse: {{ panelData.data.length }}</span>
              <hr><br> -->
              <div v-for="(data) in panelData.data">
                <table>
                  <tbody>
                    <tr>
                      <td style="align-content: start;">
                        <v-icon
                          :icon="getIconByLemmaType(data.lemma_type)"
                          class="panel-icons"
                        />
                      </td>
                      <td>
                        <a
                          class="titleHeading pointer"
                          :aria-label="'Artikel ' + data.title + ' anzeigen'"
                          tabindex="0"
                          @click="showArticle(data.id)"
                          @keyup.enter="showArticle(data.id)"
                        > {{ data.title }} </a>
                      </td>
                    </tr>
                  </tbody>
                </table>
                <p v-html="data.abstract"></p>
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
                    :to='`/illustration/${data.lemma_id}/${data.nr}`'
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
                        :to='`/illustration/${data.lemma_id}/${data.nr}`'
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
                <v-icon
                  icon="fa fa-spinner"
                  class="panel-icons"
                />
                <span
                  tabindex="0"
                  class="pointer"
                  @click="showOrt(data.id, data.lemma_id)"
                  @keyup.enter="showOrt(data.id, data.lemma_id)"
                > <b>{{ data.internal_name }}</b></span> in Artikel <span
                  tabindex="0"
                  class="pointer"
                  @click="showArticle(data.lemma_id)"
                  @keyup.enter="showArticle(data.lemma_id)"
                >
                  <v-icon
                    :icon="getIconByLemmaType(data.lemma_type)"
                    class="panel-icons"
                  /><b>{{ data.timeline_title }}</b>
                </span>
                <p>{{ data.location_relevance }}</p>
              </div>
            </div>
            <div v-if="panelData.type === 'map'">
              <div v-for="(data) in panelData.data">
                <v-icon
                  icon="fa fa-map-marked-alt"
                  class="panel-icons"
                />
                <span
                  tabindex="0"
                  class="pointer"
                  @click="openMap(data.id)"
                ><b>{{ data.timeline_title }}</b>
                </span>
                <p>{{ data.map_description }}</p>
              </div>
            </div>
            <div v-if="panelData.type === 'strassen'">
              <div class="my-2"> aktuelle Straßennamen <div
                  v-for="(data, index) in panelData.data.filter(item => item.type === 'Straßenname')"
                  :key="data.id ?? index"
                >
                  <a
                    class="titleHeading pointer"
                    :aria-label="'Straße ' + data.name + ' anzeigen'"
                    tabindex="0"
                    @click="showStreet(data)"
                    @keyup.enter="showStreet(data)"
                  >
                    <v-icon
                      icon="fa fa-map-marker-alt"
                      class="panel-icons"
                    />
                    <span>
                      <b>{{ data.name }}</b>
                    </span>
                  </a>
                </div>
              </div>
              <div class="my-2"> historische Straßennamen <div
                  v-for="(data, index) in panelData.data.filter(item => item.type === 'historischer Straßenname')"
                  :key="data.id ?? index"
                >
                  <a
                    class="titleHeading pointer"
                    :aria-label="'Straße ' + data.name + ' anzeigen'"
                    tabindex="0"
                    @click="showStreet(data)"
                    @keyup.enter="showStreet(data)"
                  >
                    <v-icon
                      icon="fa fa-map-marker-alt"
                      class="panel-icons"
                    />
                    <span>
                      <b>{{ data.name }}</b>
                      <span
                        v-if="data.verwaltungseinheit"
                        class="street-result__context"
                      > ({{ data.verwaltungseinheit }})</span>
                    </span>
                  </a>
                </div>
              </div>
            </div>
          </v-expansion-panel-text>
        </v-expansion-panel>
      </template>
    </v-expansion-panels>
    <v-btn
      v-if="searchStore.showResults"
      width="100%"
      prepend-icon="fa fa-chevron-up"
      @click="searchStore.showResults = false"
    ></v-btn>
  </div>
</template>
<script setup>
import { computed, watch } from 'vue';
import { useDisplay } from 'vuetify';
import { getIconByLemmaType } from '../services/getLemmaIconByType';
import { useLemmaStore } from '../store/lemmaStore';
import useMapStore from '../store/mapStore';
import { useSearchQueryStore } from '../store/searchQueryStore';
import { useViewControllerStore } from '../store/viewControllerStore';

const { smAndDown } = useDisplay();

const mapStore = useMapStore();

const lemmaStore = useLemmaStore();
const viewControllerStore = useViewControllerStore();

const searchStore = useSearchQueryStore();
const localQueryResult = computed(() => searchStore.queryResult);
const activeQueryFilter = computed(() => searchStore.getQueryArtikelsFilterDataByLemmaType);

const panels = computed(() => [
  {
    type: 'artikel',
    title: 'Artikel',
    amount: Array.isArray(activeQueryFilter.value) ? activeQueryFilter.value.length : 0,
    data: activeQueryFilter.value ?? [],
  },
  {
    type: 'illustration',
    title: 'Illustrationen',
    amount: Array.isArray(localQueryResult.value?.illustration) ? localQueryResult.value.illustration.length : 0,
    data: localQueryResult.value?.illustration ?? [],
  },
  {
    type: 'orte',
    title: 'Mit Artikeln verknüpfte Orte',
    amount: Array.isArray(localQueryResult.value?.orte) ? localQueryResult.value.orte.length : 0,
    data: localQueryResult.value?.orte ?? [],
  },
  {
    type: 'map',
    title: 'Karten',
    amount: Array.isArray(localQueryResult.value?.map) ? localQueryResult.value.map.length : 0,
    data: localQueryResult.value?.map ?? [],
  },
  {
    type: 'strassen',
    title: 'Straßennamen',
    amount: Array.isArray(localQueryResult.value.street) ? localQueryResult.value.street.length : 0,
    data: localQueryResult.value.street ?? [],
  },
]);



watch(() => viewControllerStore.currentView, (newView) => {
  if (newView === 'mapSettings') {
    searchStore.showResults = false;
  };
});

function getImageURL(fileName) {
  return `/img/${ fileName }`;
}

/**
 * showStreet: toggle a street marker on the map for the given street object.
 * - If the same street is already shown, it clears it (toggle off).
 * - Tries to obtain coordinates (from the street object, locationFinder or API).
 * - Calls the backend endpoint mapbystreet/<streetId> and activates a historic map if found.
 */
async function showStreet(street) {
  // Toggle off if already shown
  if (mapStore.shownStreet && mapStore.shownStreet.streetId === street.id) {
    mapStore.clearShownStreet();
    return;
  }

  mapStore.showStreetOnMap(street);
}

// optional helper exposed to template / other components
function clearShownStreet() {
  mapStore.clearShownStreet();
}

async function showArticle(lemmaId) {
  mapStore.showPoints = true;
  try {
    await lemmaStore.fetchLemma(lemmaId);
    viewControllerStore.setCurrentView('article');
  } catch (error) {
    console.error(error);
  };
}

async function showOrt(ortId, lemmaId) {
  mapStore.showPoints = true;
  try {
    await lemmaStore.fetchOrt(ortId, lemmaId);
    viewControllerStore.setCurrentView('ort');
  } catch (error) {
    console.error(error);
  };
}

function openMap(mapId) {
  const map = [...mapStore.historicMaps, ...mapStore.thematicMaps].find((map) => map.id === mapId);
  mapStore.selectedMap = map;
}
</script>
<style scoped>
.panel {
  width: 100%;
  max-width: 100%;
  box-sizing: border-box;
}

.alternative-query {
  padding: 8px;
  font-size: 14px;
  background-color: white;
  width: 100%;
  border-radius: 4px;
  box-shadow: 0px 3px 1px -2px var(--v-shadow-key-umbra-opacity, rgba(0, 0, 0, 0.2)), 0px 2px 2px 0px var(--v-shadow-key-penumbra-opacity, rgba(0, 0, 0, 0.14)), 0px 1px 5px 0px var(--v-shadow-key-ambient-opacity, rgba(0, 0, 0, 0.12));

  & .link {
    color: blue;
    text-decoration: underline;
  }
}

.no-results {
  padding: 8px;
  font-size: 14px;
  background-color: white;
  width: 100%;
  border-radius: 4px;
}
</style>
