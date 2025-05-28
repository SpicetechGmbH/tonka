<script setup>
import { ref, computed, watch } from 'vue';
import DialogComponent from '../components/DialogComponent.vue';
import useMapStore from '../store/mapStore';
import { useViewControllerStore } from '../store/viewControllerStore';

const mapStore = useMapStore();
const viewControllerStore = useViewControllerStore();

const historicMap = ref('');
const thematicMap = ref('');
const historicCompareMap = ref('');
const thematicCompareMap = ref('');

const showItemList = ref(true);
const isButtonDisabled = ref(false);
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
  <div class="grid-container">
    <v-card class="card">
      <v-toolbar class="toolbar">
        <v-toolbar-title>
          <v-icon
            style="margin-left: 16px;"
            icon="fa fa-map"
            aria-label="Karteneinstellungen"
          >
          </v-icon> Karteneinstellungen</v-toolbar-title>
        <template #append>
          <v-btn
          tabindex="0"
          aria-label="Schließen"
          @click="closeMapSettings"
          ><v-icon color="var(--dts-color-closer)">mdi mdi-close-circle</v-icon></v-btn>
        </template>
      </v-toolbar>
      <v-container>
        <v-row
          no-gutters
          style="flex-wrap: wrap;"
        >
          <v-col class="mobile-screen">
            <v-btn
              class="ma-2"
              :disabled="!hasSelectedMap"
              aria-label="Karte ausrichten"
              @click="mapStore.rotateMap"
            > Karte<br>ausrichten</v-btn>
          </v-col>
          <v-col class="mobile-screen">
            <v-btn
              class="ma-2"
              :disabled="!hasSelectedMap"
              aria-label="Karten ausblenden"
              @click="noMap"
            > Keine Karte</v-btn>
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
              class="ma-2"
              aria-label="Punkte umschalten"
              @click="mapStore.toggleShowPoints"
            >
              <v-icon
                class="fa fa-circle"
                color="newwarm"
              ></v-icon> aus-/<br>einblenden </v-btn>
          </v-col>
          <v-col>
            <v-btn
              class="ma-2"
              :disabled="!isButtonDisabled"
              aria-label="Straßenmarker anzeigen"
            >
              <v-icon class="fa-solid fa-location-dot"></v-icon> aus-/<br>einblenden </v-btn>
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
      <div class="selectItems">
        <div v-show="showItemList">Historische Karten ({{ mapStore.historicMapData.length }})</div>
        <v-autocomplete
          v-show="showItemList"
          v-model="historicMap"
          :items="mapStore.historicMapData"
          label="Wählen Sie"
          return-object
        ></v-autocomplete>
        <div v-show="!showItemList">Historische Vergleichskarten ({{ mapStore.historicMapData.length }})</div>
        <v-autocomplete
          v-show="!showItemList"
          v-model="historicCompareMap"
          :items="mapStore.historicMapData"
          label="Wählen Sie"
          return-object
        ></v-autocomplete>
        <div v-show="showItemList">Thematische Karten ({{ mapStore.thematicMapData.length }})</div>
        <v-autocomplete
          v-show="showItemList"
          :items="mapStore.thematicMapData"
          v-model="thematicMap"
          label="Wählen Sie"
          return-object
        ></v-autocomplete>
        <div v-show="!showItemList">Thematische Vergleichskarten ({{ mapStore.thematicMapData.length }})</div>
        <v-autocomplete
          v-show="!showItemList"
          :items="mapStore.thematicMapData"
          v-model="thematicCompareMap"
          label="Wählen Sie"
          return-object
        ></v-autocomplete>
        <DialogComponent v-model:showDialog="showDialog">
        </DialogComponent>
      </div>
      <v-container>
        <v-row
          no-gutters
          style="flex-wrap: wrap;"
        >
          <v-col class="mobile-screen">
            <v-btn
              v-show="!showItemList"
              class="ma-2"
              aria-label="Vergleichskarte ausblenden"
              @click="noCompareMap"
            > Keine Vergleichskarte</v-btn>
          </v-col>
        </v-row>
      </v-container>
      <v-card-text></v-card-text>
    </v-card>
  </div>
</template>
<style lang="scss">
.selectItems {
  padding: 10px;
}

.v-container {
  padding: 10px;
}

.slider {
  padding-top: 20px;
}

@media only screen and (max-width: 780px) {
  .slider {
    padding-top: 20px;
  }

  .mobile-screen {
    display: none;
  }
}

@media only screen and (max-width: 1024px) {
  .slider {
    padding-top: 20px;
  }
}

@media only screen and (max-width: 480px) {
  .slider {
    padding-top: 75px;
  }
}
</style>
