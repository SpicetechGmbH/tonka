<template>
  <div
    v-if="toggleSearchbar"
    id="searchbar"
    class="grid-container"
  >
    <v-card
      class="mx-auto searchbar"
      color="rgba(var(--v-theme-foxbrush), 0.8)"
      width="100%"
      min-width="300px"
    >
      <template v-slot:prepend>
        <v-icon
          class="searchbar icon-search"
          icon="mdi:mdi-magnify"
          tabindex="3"
          @click="searchQueryResult"
          aria-label="Suche"
        ></v-icon>
      </template>
      <v-text-field
        v-model="searchQuery"
        variant="solo"
        placeholder="Suche"
        tabindex="2"
        density="compact"
        @keyup.enter="searchQueryResult"
      >
      </v-text-field>
    </v-card>
    <div class="d-sm-flex d-none">
      <v-card
        class="mx-auto"
        color="rgba(var(--v-theme-foxbrush), 0.8)"
        width="100%"
      >
        <v-card-text class="button-div">
          <v-btn
            class="searchbar button"
            @click="toggleFilterButton"
            v-if="showFilterButton"
            aria-label="Filter"
          >
            <v-icon class="fa fa-filter"></v-icon> Filter </v-btn>
          <v-btn
            class="searchbar artikel"
            tabindex="1"
            @click="getAllQueryResult"
            aria-label="Alle Artikel"
          > Alle Artikel </v-btn>
        </v-card-text>
      </v-card>
    </div>
    <div
      v-if="toggleFilterButtons"
      class="panels"
    >
      <SearchButtons
        :toggleArtikelPanel="props.toggleArtikelPanel"
        :toggleFilterButtons="toggleFilterButtons"
      />
    </div>
    <ResultPanel
      :closeQueryPanel="props.toggleQueryPanel"
      @update:showQueryPanel="handleShowArticle($event)"
      @update:closeQueryPanel="emitUpdateToggleQueryPanel"
    />
  </div>
</template>
<script setup>
import { ref } from 'vue';
import ResultPanel from './ResultPanel.vue';
import SearchButtons from './SearchButtons.vue';
import searchQueryStore from '../store/searchQueryStore';
import { useViewControllerStore } from '../store/viewControllerStore';
import { useLemmaStore } from '../store/lemmaStore';

const emit = defineEmits([
  'update:toggleArtikelPanel',
  'update:toggleQueryPanel',
  'update:setOpenPanelValue',
  'update:setOpenQueryPanelValue',
]);
const searchStore = searchQueryStore();
const lemmaStore = useLemmaStore();
const viewControllerStore = useViewControllerStore();

const searchQuery = ref('');
const toggleSearchbar = ref(true);
const showFilterButton = ref(false);
const toggleFilterButtons = ref(false);

const props = defineProps({
  toggleArtikelPanel: {
    type: Boolean,
    default: false
  },
  toggleQueryPanel: {
    type: Boolean,
    default: false
  },
  setOpenPanelValue: {
    type: Boolean,
    default: false
  },
  setOpenQueryPanelValue: {
    type: Boolean,
    default: false
  },
});

function getAllQueryResult() {
  searchStore.activeFilter = '';
  searchStore.fetchAlleArtikel()
  showFilterButton.value = true;
  viewControllerStore.setCurrentView("result");
  // emitUpdateSetOpenPanelValue(true);
  // emitUpdateToggleArtikelPanel(true);
  // emitUpdateToggleQueryPanel(false);
};

function searchQueryResult() {
  const userInput = searchQuery.value;
  searchStore.activeQueryFilter = '';
  Promise.all([
    searchStore.fetchIllustrationArtikels(userInput),
    searchStore.fetchOrteArtikels(userInput),
    searchStore.fetchQueryArtikels(userInput)
  ]).then(() => {
    showFilterButton.value = true;
    viewControllerStore.setCurrentView("result");
    searchStore.showResults = true;
    // emitUpdateSetOpenQueryPanelValue(true);
    // emitUpdateToggleQueryPanel(true);
    // emitUpdateToggleArtikelPanel(false);
  }).catch(error => {
    console.error(error);
  });
}

function toggleFilterButton() {
  toggleFilterButtons.value = !toggleFilterButtons.value;
};

function handleShowArticle(value) {
  emitUpdateToggleArtikelPanel(false);
  emitUpdateToggleQueryPanel(false);
};

function emitUpdateToggleArtikelPanel(value) {
  emit('update:toggleArtikelPanel', value);
};

function emitUpdateToggleQueryPanel(value) {
  emit('update:toggleQueryPanel', value);
};

function emitUpdateSetOpenPanelValue(value) {
  emit('update:setOpenPanelValue', value);
};

function emitUpdateSetOpenQueryPanelValue(value) {
  emit('update:setOpenQueryPanelValue', value);
};

</script>
<style lang="scss">
.grid-container {
  display: grid;
  grid-template-rows: 54px;
  padding: 8px;
  grid-template-areas:
    "icon-area search artikel button"
    "panels panels panels panels"
    "panel panel panel panel";

  & .searchbar {
    display: flex;

    & .v-text-field {
      grid-area: search;
      padding-top: 7px;
      padding-right: 7px;
      padding-bottom: 7px;
    }

    & .icon-search {
      grid-area: icon-area;
    }
  }
}

.panels {
  grid-area: panels;
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
}

.v-card .v-card-text {
  padding: 7px;
}

.v-btn {
  text-transform: unset !important;
}

.v-btn.v-btn--density-default {
  height: calc(var(--v-btn-height) + 2.9px);
}

@media only screen and (max-width: 780px) {
  .searchBar {
    grid-template-areas:
      'search search'
      'panel panel';
  }

  .searchBar .button .v-card {
    display: none;
  }
}
</style>
