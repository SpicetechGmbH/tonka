<template>
  <v-toolbar
    rounded="lg"
    border
    floating
    id="searchbar"
    class="searchbar"
    style="background-color:rgba(var(--v-theme-primary), 0.8);"
  >
    <template #prepend>
      <v-btn
        class="mx-1"
        icon="fa fa-magnifying-glass"
        tabindex="4"
        @click="searchQueryResult"
        :loading="fetchingData"
        aria-label="Suche"
        style="color: white;"
      ></v-btn>
    </template>
    <div
      class="mx-1"
      style="flex-grow: 1;"
    >
      <v-text-field
        ref="searchInput"
        v-model="searchStore.query"
        variant="solo"
        placeholder="Suche"
        density="compact"
        @keyup.enter="searchQueryResult"
        :loading="fetchingData"
        aria-label="Suchfeld"
        tabindex="3"
        flat
        hide-details
        single-line
      >
        <template #label>
          <v-hotkey keys="slash/cmd+k" />
        </template>
      </v-text-field>
    </div>
    <template #append>
      <v-btn
        v-if="showFilterButton"
        class="searchbar-button mx-1"
        @click="toggleFilterButton"
        aria-label="Filter"
        tabindex="2"
      >
        <v-icon class="fa fa-filter"></v-icon> {{ filterButtonText }} </v-btn>
      <v-btn
        class="searchbar-button mx-1"
        @click="getAllLemmaListResult"
        aria-label="Alle Artikel"
        tabindex="1"
      > Alle Artikel </v-btn>
    </template>
  </v-toolbar>
  <div
    v-if="showFilterButtons"
    class="panels"
  >
    <SearchButtons :showFilterButtons="showFilterButtons" />
  </div>
  <ResultPanel @update:showQueryPanel="handleShowArticle($event)" />
</template>
<script setup>
import { computed, nextTick, onMounted, onUnmounted, ref, watch } from 'vue';
import { useDisplay } from 'vuetify';
import { useLemmaStore } from '../store/lemmaStore';
import useMapStore from '../store/mapStore';
import { useSearchQueryStore } from '../store/searchQueryStore';
import { useViewControllerStore } from '../store/viewControllerStore';
import ResultPanel from './ResultPanel.vue';
import SearchButtons from './SearchButtons.vue';

const { smAndDown } = useDisplay();

const filterButtonText = computed(() => smAndDown.value ? '' : 'Filter');

const searchInput = ref(null);

const searchStore = useSearchQueryStore();
const lemmaStore = useLemmaStore();
const viewControllerStore = useViewControllerStore();
const mapStore = useMapStore();

const fetchingData = ref(false);

const toggleSearchbar = ref(true);
const showFilterButton = ref(false);
const showFilterButtons = ref(false);

watch(() => searchStore.queryResult, (newValue) => {
  showFilterButton.value = true;
  viewControllerStore.setCurrentView("result");
  searchStore.showResults = true;
});

function onKeydown(e) {
  // ignore if typing in the search input field
  const tag = e.target?.tagName?.toLowerCase();
  if (tag === 'input' || tag === 'textarea' || e.target?.isContentEditable) {
    return;
  }

  const isSlash = e.key === '/' || e.code === 'Slash';
  const isCtrlK = (e.key === 'k' || e.code === 'KeyK') && (e.ctrlKey || e.metaKey);

  if (isSlash || isCtrlK) {
    e.preventDefault();
    searchInput.value?.focus();
  }
}

onMounted(() => {
  lemmaStore.fetchLemmaTypes();
  window.addEventListener('keydown', onKeydown);
});

onUnmounted(() => {
  window.removeEventListener('keydown', onKeydown);
});

async function getAllLemmaListResult() {
  fetchingData.value = true;
  await nextTick();
  await new Promise(r => setTimeout(r, 0)); // ensure loading state is visible before fetching data
  searchStore.fetchAlleArtikel();
  showFilterButton.value = true;
  viewControllerStore.setCurrentView("result");
  searchStore.showResults = true;
  fetchingData.value = false;
}

function searchQueryResult() {
  fetchingData.value = true;
  mapStore.showPoints = true;
  searchStore.search().then(() => {
    showFilterButton.value = true;
    viewControllerStore.setCurrentView("result");
    searchStore.showResults = true;
  }).catch(error => {
    console.error(error);
  }).finally(() => {
    fetchingData.value = false;
  });
}

function toggleFilterButton() {
  showFilterButtons.value = !showFilterButtons.value;
  if (!showFilterButtons.value) {
    searchStore.activeQueryFilter = '';
  }
};

</script>
<style lang="scss">
.searchbar {
  width: 100%;
}

.grid-container {
  width: 100%;
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) min-content min-content;
  grid-template-rows: 54px;
  padding: 8px;
  grid-template-areas:
    "icon-area search artikel button"
    "panels panels panels panels"
    "panel panel panel panel";

  & .searchbar {
    display: flex;
    align-items: center;
    /* occupy the grid area and fill the wrapper width (wrapper already uses --dts-panel-max-width = 48%) */
    grid-column: 1 / 3;
    width: 100%;
    min-width: 0;
    box-sizing: border-box;

    & .v-text-field {
      grid-area: search;
      padding-top: 7px;
      padding-right: 7px;
      padding-bottom: 7px;
      min-width: 0;
      flex: 1 1 auto;
      width: 100%;
      height: 100%;
      box-sizing: border-box;
    }

    & .v-input,
    & .v-field,
    & .v-text-field__slot {
      min-width: 0;
    }

    & .icon-search {
      grid-area: icon-area;
    }
  }
}

.panels {
  grid-area: panels;
}

.searchbar-button {
  background-color: #ffffff;
}

.button-div {
  display: flex;
  gap: 6px;

  & .button {
    grid-area: button;
  }

  & .artikel {
    grid-area: artikel;
  }

  & .fa-filter {
    font-size: 14px;
  }
}

.panel {
  grid-area: panel;

  & p {
    font-size: 14px;
  }

  & .searchTtile {
    font-style: italic;
    color: rgb(160, 160, 160);
  }

  & .titleHeading {
    font-weight: 900;
    font-size: 14px;
  }

  & a {
    text-decoration: none;
    color: black;
  }

  & span {
    font-size: 14px;
  }

  & .pointer {
    cursor: pointer;
  }
}

.illustration-div {
  display: grid;
  row-gap: 10px;
  grid-template-columns: 15px 80px 1fr;
  grid-template-rows: 130px;
  grid-template-areas:
    "icon-image image description";
  align-items: center;
  gap: 8px;
  font-size: 14px;

  & .image {
    max-width: 100%;
  }
}

.v-expansion-panel-text {
  max-height: 450px;
  overflow-y: auto;

  @media only screen and (max-width: 780px) {
    max-height: 230px;
  }
}

.v-card .v-card-text {
  padding: 7px;
}

.v-btn {
  text-transform: unset !important;
}

// .v-btn.v-btn--density-default {
//   height: calc(var(--v-btn-height) + 2.9px);
// }

@media only screen and (max-width: 780px) {
  .searchBar {
    grid-template-areas:
      'search search'
      'panel panel';
  }

  .searchBar .button .v-card {
    display: none;
  }

  .grid-container {
    grid-template-columns: auto 1fr;
    grid-template-areas:
      'icon-area search'
      'panels panels'
      'panel panel';
  }

  #searchBarButtons {
    grid-area: panels;
    width: 100%;
  }

  #searchBarButtons .v-card {
    width: 100%;
    box-sizing: border-box;
  }

  /* ensure the search panel becomes full width on small screens */
  .grid-container .searchbar {
    width: 100%;
  }
}
</style>
