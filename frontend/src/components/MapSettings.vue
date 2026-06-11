<script setup>
import { computed, ref, toRef, watch } from 'vue';
import { useDisplay } from 'vuetify';
import GoToMapDialog from '../components/GoToMapDialog.vue';
import useMapStore from '../store/mapStore';
import { useViewControllerStore } from '../store/viewControllerStore';

const { smAndDown, mdAndUp } = useDisplay();

const mapStore = useMapStore();
const viewControllerStore = useViewControllerStore();

const historicMap = ref('');
const thematicMap = ref('');
const historicCompareMap = ref('');
const thematicCompareMap = ref('');

const showItemList = ref(true);
const showMapSelection = ref(true);
const showDialog = ref(false);

// changes when a new historic map is selected
watch(historicMap, (newHistoricMap, oldHistoricMap) => {
  if (newHistoricMap !== '') {
    mapStore.selectedMap = newHistoricMap;
    thematicMap.value = '';
  }
});

// changes when a new thematic map is selected
watch(thematicMap, (newThematicMap, oldThematicMap) => {
  if (newThematicMap !== '') {
    mapStore.selectedMap = newThematicMap;
    historicMap.value = '';
  }
});

const selectedMapRef = toRef(mapStore, 'selectedMap');

watch(selectedMapRef, (newSelectedMap, oldSelectedMap) => {
  if (newSelectedMap === null) {
    historicMap.value = '';
    thematicMap.value = '';
  }
  if (newSelectedMap.map_type === 'HISTORICMAP') {
    historicMap.value = newSelectedMap;
    thematicMap.value = '';
  }
  if (newSelectedMap.map_type === 'THEMATICMAP') {
    thematicMap.value = newSelectedMap;
    historicMap.value = '';
  }
});

// changes when a new historic compare map is selected
watch(historicCompareMap, (newHistoricCompareMap, oldHistoricCompareMap) => {
  if (newHistoricCompareMap !== '') {
    mapStore.selectedCompareMap = newHistoricCompareMap;
    thematicCompareMap.value = '';
  }
});

// changes when a new thematic compare map is selected
watch(thematicCompareMap, (newThematicCompareMap, oldThematicCompareMap) => {
  if (newThematicCompareMap !== '') {
    mapStore.selectedCompareMap = newThematicCompareMap;
    historicCompareMap.value = '';
  }
});

const hasSelectedMap = computed(() => {
  return thematicMap.value !== '' || historicMap.value !== '';
});

// resets the map settings
function noMap() {
  mapStore.selectedMap = null;
  mapStore.selectedCompareMap = null;
  historicMap.value = '';
  thematicMap.value = '';
  historicCompareMap.value = '';
  thematicCompareMap.value = '';
  showItemList.value = true;
  mapStore.rotateMap();
};

// resets the compare map
function noCompareMap() {
  mapStore.selectedCompareMap = null;
  historicCompareMap.value = '';
  thematicCompareMap.value = '';
};

function toggleItemList() {
  showItemList.value = !showItemList.value;
};

function showDialogs() {
  showDialog.value = true;
};

function closeMapSettings() {
  viewControllerStore.goBack(); //setCurrentView('map');
};
</script>
<template>
  <v-card class="map-settings-card">
    <v-toolbar class="toolbar">
      <v-toolbar-title class="dts-text">
        <v-icon
          class="mx-4"
          icon="fa fa-map"
          aria-label="Karteneinstellungen"
        >
        </v-icon>Karteneinstellungen</v-toolbar-title>
      <template #append>
        <v-btn
          tabindex="0"
          aria-label="Schließen"
          @click="closeMapSettings"
        ><v-icon
            class="closer-icon"
            style="color: rgba(var(--v-theme-closer), 0.4)"
          >mdi mdi-close-circle</v-icon></v-btn>
      </template>
    </v-toolbar>
    <v-container>
      <v-row
        no-gutters
        style="flex-wrap: wrap;"
      >
        <v-col>
          <v-btn
            :class="mdAndUp ? 'ma-2' : ''"
            :disabled="!hasSelectedMap"
            aria-label="Karte ausrichten"
            @click="mapStore.rotateMap"
            :prepend-icon="smAndDown ? 'fa fa-map' : undefined"
            :append-icon="smAndDown ? 'fa fa-rotate' : undefined"
          >
            <span v-if="mdAndUp">Karte<br>ausrichten</span>
          </v-btn>
        </v-col>
        <v-col>
          <v-btn
            :class="mdAndUp ? 'ma-2' : ''"
            :disabled="!hasSelectedMap"
            aria-label="Karten ausblenden"
            @click="noMap"
            :prepend-icon="smAndDown ? 'fa fa-map' : undefined"
            :append-icon="smAndDown ? 'fa fa-xmark' : undefined"
          >
            <span v-if="mdAndUp">Keine Karte</span>
          </v-btn>
        </v-col>
        <v-col class="mobile-screen">
          <v-btn
            class="ma-2"
            :disabled="!hasSelectedMap"
            aria-label="Karten vergleichen"
            @click="toggleItemList"
          > Karte<br>vergleichen mit</v-btn>
        </v-col>
        <v-col>
          <v-btn
            :class="mdAndUp ? 'ma-2' : ''"
            aria-label="Punkte umschalten"
            @click="mapStore.toggleShowPoints"
            :append-icon="smAndDown ? mapStore.showPoints ? 'fa fa-toggle-on' : 'fa fa-toggle-off' : undefined"
          >
            <v-icon
              class="fa fa-circle"
              color="secondary"
              style="text-shadow: 0 0 2px #000"
            ></v-icon><span v-if="mdAndUp">aus-/<br>einblenden</span>
          </v-btn>
        </v-col>
      </v-row>
    </v-container>
    <div class="slider">
      <v-slider
        v-model="mapStore.transparency"
        :step="1"
        min="0"
        max="100"
        label="Transparenz:"
        :disabled="!hasSelectedMap"
      >
        <template v-slot:append>
          <v-text-field
            v-model="mapStore.transparency"
            density="compact"
            style="width: 80px"
            type="number"
            min="0"
            max="100"
            variant="outlined"
            hide-details
          ></v-text-field>
        </template>
      </v-slider>
    </div>
    <div
      v-show="showMapSelection"
      class="selectItems"
    >
      <div v-show="showItemList">Historische Karten ({{ mapStore.historicMaps.length }})</div>
      <v-autocomplete
        v-show="showItemList"
        v-model="historicMap"
        :items="mapStore.historicMaps"
        item-title="title"
        label="Wählen Sie"
        return-object
      >
        <template #item="{ item, index, props }">
          <v-list-item v-bind="props">
            <template #append>
              <v-menu
                location="top"
                :close-on-content-click="false"
              >
                <template #activator="{ props: tooltipProps }">
                  <v-icon-btn
                    v-bind="tooltipProps"
                    icon="fa fa-circle-info"
                    variant="text"
                    size="default"
                    color="primary"
                    aria-label="Vorschau anzeigen"
                  ></v-icon-btn>
                </template>
                <div class="v-menu-tooltip selectable">{{ item.raw.map_description }}<br>Archivsignatur: {{ item.raw.archive_signature }}</div>
              </v-menu>
            </template>
          </v-list-item>
        </template>
      </v-autocomplete>
      <div v-show="!showItemList">Historische Vergleichskarten ({{ mapStore.historicMaps.length }})</div>
      <v-autocomplete
        v-show="!showItemList"
        v-model="historicCompareMap"
        :items="mapStore.historicMaps"
        item-title="title"
        label="Wählen Sie"
        return-object
      >
        <template #item="{ item, index, props }">
          <v-list-item v-bind="props">
            <template #append>
              <v-menu
                location="top"
                :close-on-content-click="false"
              >
                <template #activator="{ props: tooltipProps }">
                  <v-icon-btn
                    v-bind="tooltipProps"
                    icon="fa fa-circle-info"
                    variant="text"
                    size="default"
                    color="primary"
                    aria-label="Vorschau anzeigen"
                  ></v-icon-btn>
                </template>
                <div class="v-menu-tooltip selectable">{{ item.raw.map_description }}<br>Archivsignatur: {{ item.raw.archive_signature }}</div>
              </v-menu>
            </template>
          </v-list-item>
        </template>
      </v-autocomplete>
      <div v-show="showItemList">Thematische Karten ({{ mapStore.thematicMaps.length }})</div>
      <v-autocomplete
        v-show="showItemList"
        v-model="thematicMap"
        :items="mapStore.thematicMaps"
        item-title="title"
        label="Wählen Sie"
        return-object
      >
        <template #item="{ item, index, props }">
          <v-list-item v-bind="props">
            <template #append>
              <v-menu
                location="top"
                :close-on-content-click="false"
              >
                <template #activator="{ props: tooltipProps }">
                  <v-icon-btn
                    v-bind="tooltipProps"
                    icon="fa fa-circle-info"
                    variant="text"
                    size="default"
                    color="primary"
                    aria-label="Vorschau anzeigen"
                  ></v-icon-btn>
                </template>
                <div class="v-menu-tooltip selectable">{{ item.raw.map_description }}<br>Archivsignatur: {{ item.raw.archive_signature }}</div>
              </v-menu>
            </template>
          </v-list-item>
        </template>
      </v-autocomplete>
      <div v-show="!showItemList">Thematische Vergleichskarten ({{ mapStore.thematicMaps.length }})</div>
      <v-autocomplete
        v-show="!showItemList"
        v-model="thematicCompareMap"
        :items="mapStore.thematicMaps"
        item-title="title"
        label="Wählen Sie"
        return-object
      >
        <template #item="{ item, index, props }">
          <v-list-item v-bind="props">
            <template #append>
              <v-menu
                location="top"
                :close-on-content-click="false"
              >
                <template #activator="{ props: tooltipProps }">
                  <v-icon-btn
                    v-bind="tooltipProps"
                    icon="fa fa-circle-info"
                    variant="text"
                    size="default"
                    color="primary"
                    aria-label="Vorschau anzeigen"
                  ></v-icon-btn>
                </template>
                <div class="v-menu-tooltip selectable">{{ item.raw.map_description }}<br>Archivsignatur: {{ item.raw.archive_signature }}</div>
              </v-menu>
            </template>
          </v-list-item>
        </template>
      </v-autocomplete>
      <GoToMapDialog v-model:showDialog="showDialog">
      </GoToMapDialog>
    </div>
    <v-container class="mobile-screen">
      <v-row
        no-gutters
        style="flex-wrap: wrap;"
      >
        <v-col>
          <v-btn
            v-show="!showItemList"
            class="ma-2"
            aria-label="Vergleichskarte ausblenden"
            @click="noCompareMap"
          > Keine Vergleichskarte</v-btn>
        </v-col>
      </v-row>
    </v-container>
    <v-btn
      v-if="smAndDown"
      width="100%"
      :text="showMapSelection ? 'Auswahl ausblenden' : 'Auswahl einblenden'"
      :prepend-icon="showMapSelection ? 'fa fa-caret-up' : 'fa fa-caret-down'"
      @click="showMapSelection = !showMapSelection"
      aria-label="Karteneinstellungen erweitern oder reduzieren"
    ></v-btn>
  </v-card>
</template>
<style lang="scss">
.map-settings-card {
  position: fixed;
  top: 10%;
  right: 8px;
  z-index: 1000;
  max-width: var(--dts-panel-max-width);

  @media screen and (max-width: 780px) {
    left: 8px;
    right: 8px;
    max-width: none;
    width: auto;
  }
}

.selectItems {
  padding: 10px;
}

.v-container {
  padding: 10px;
}

.slider {
  padding-top: 20px;
}

.v-btn:disabled {
  background-color: rgba(var(--v-theme-primary), 0.8) !important;
  color: white !important;
  cursor: not-allowed;
}

@media only screen and (max-width: 780px) {
  .mobile-screen {
    display: none;
  }
}

.v-menu-tooltip {
  max-width: 300px;
  padding: 5px;
  color: rgba(var(--v-theme-primary), 0.8);
  background-color: #f5f4ed;
  border: 1px solid #808074;
  border-radius: 3px;
  box-shadow: 3px 3px 10px rgba(0, 0, 0, 0.2);
  font-size: 12px;
  font-weight: 600;

  &.selectable {
    user-select: text;
    -webkit-user-select: text;
  }
}
</style>
