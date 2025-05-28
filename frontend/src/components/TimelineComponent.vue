<template>
  <div class="timelineStyle">
    <v-dialog
      class="cluster-menu"
      width="250px"
      v-model="clusterDialogActive"
      scrim="transparent"
      contained
      persitent
      scrollable="false"
    >
      <v-card>
        <v-card-title>
          <v-container>
            <v-row justify="center">
              <span>Artikel aus Cluster</span>
              <v-spacer></v-spacer>
              <span
                class="fa fa-close popup-closer"
                @click="clusterDialogActive = false"
              ></span>
            </v-row>
          </v-container>
        </v-card-title>
        <v-card-text>
          <v-list>
            <v-list-item
              v-for="(item, index) in clusterItems"
              :key="index"
              :value="index"
              @click="showArticle(item.id)"
              :prepend-icon="getIconByLemmaType(item.dtsType)"
            > {{ item.content }} </v-list-item>
          </v-list>
        </v-card-text>
      </v-card>
    </v-dialog>
    <div
      ref="timelineContainer"
      class="dts-timeline timeline"
    >
    </div>
  </div>
</template>
<script setup>
import { Timeline } from "vis-timeline/standalone";
import { computed, nextTick, onMounted, ref } from 'vue';
import useSearchQueryStore from '../store/searchQueryStore';
import '@fortawesome/fontawesome-free/css/all.css';
import { getIconByLemmaType } from "../services/getLemmaIconByType";
import { useLemmaStore } from '../store/lemmaStore';
import { useViewControllerStore } from "../store/viewControllerStore";

const lemmaStore = useLemmaStore();
const lemma = computed(() => lemmaStore.lemma);
const searchQueryStore = useSearchQueryStore();
const viewControllerStore = useViewControllerStore();

const clusterDialogActive = ref(false);
const clusterItems = ref([]);

const timelineContainer = ref(null);

const options = ref({
  // timeline options go here
  maxHeight: '110px',
  stack: false,
  min: '0000-01-01',
  // max: '2000-12-31',
  locale: 'de',
  format: {
    minorLabels: {
      year: 'y'
    }
  },
  cluster: {
    showStipes: true,
  },
  zoomMin: 1000 * 60 * 60 * 24 * 10,
  showCurrentTime: false,
  xss: {
    filterOptions: {
      whiteList: {
        i: ['class'],
        br: [],
        div: []
      }
    }
  },
  tooltip: {
    overflowMethod: 'cap',
    template: function (item) {
      var html = '';
      if (item.id) {
        html += `${ item.date }<br>${ item.description }`;
      } else {
        for (var i = 0; i < item.items.length; i++) {
          html += `${ item.items[i].date }<br>${ item.items[i].description }<br>`;
        }
      }
      return html;
    },
  },
  template: function (item) {
    var html = `<i class="${ getIconByLemmaType(item.dtsType) }"></i> ${ item.content }`;
    return html;
  },
});

// Create an accessible reference for the timeline
const timeline = ref();

onMounted(async () => {
  // Initialize data
  if (searchQueryStore.alleArtikel.length === 0) {
    await searchQueryStore.fetchAlleArtikel();
  }
  const artikelData = searchQueryStore.alleArtikel.reduce((acc, artikel) => {
    if (artikel.id && artikel["timeline_date_year"]) {
      acc.push({
        id: artikel["id"],
        content: artikel["timeline_title"] ? artikel["timeline_title"] : artikel["title"],
        start: `${ artikel["timeline_date_year"] }-${ artikel["timeline_date_month"] ? artikel["timeline_date_month"] : '01' }-${ artikel["timeline_date_day"] ? artikel["timeline_date_day"] : '01' }`,
        group: 1,
        date: `${ artikel["timeline_date_label"] }`,
        description: `${ artikel["timeline_date_relevance"] }`,
        dtsType: `${ artikel["lemma_type"] }`,
        selectable: true
      });
    }
    return acc;
  }, []);

  // Create a Timeline
  timeline.value = new Timeline(timelineContainer.value, artikelData, options.value);
  timeline.value.on('select', function (properties, event) {
    const selectedArticle = searchQueryStore.alleArtikel.find(article => article.id === properties.items[0]);
    if (selectedArticle) {
      // Open selected article
      showArticle(properties.items[0]);
    } else {
      // Find cluster within timeline.
      const cluster = timeline.value.itemSet.clusters.find(cluster => cluster.id === properties.items[0]);
      // cluster.data.items
      if (cluster) {
        clusterDialogActive.value = true;
        clusterItems.value = cluster.data.items;
      }
    }
  });

  window.addEventListener('keydown', handleKeyPress);
});

function handleKeyPress(event) {
  const range = timeline.value.getWindow();
  const interval = range.end - range.start;
  if (event.key === 'ArrowUp') {
    timeline.value.setWindow({ start: range.start.valueOf() + interval * 0.2, end: range.end.valueOf() - interval * 0.2 });
  } else if (event.key === 'ArrowDown') {
    timeline.value.setWindow({ start: range.start.valueOf() - interval * 0.2, end: range.end.valueOf() + interval * 0.2 });
  } else if (event.key === 'ArrowLeft') {
    timeline.value.moveTo((range.start.valueOf() + (interval / 2)) - (interval / 5));
  } else if (event.key === 'ArrowRight') {
    timeline.value.moveTo((range.start.valueOf() + (interval / 2)) + (interval / 5));
  }
}

async function showArticle(id) {
  try {
    await lemmaStore.fetchArticle(id);
    viewControllerStore.setCurrentView('article');
  } catch (error) {
    console.error('Error fetching article: ', error);
  }
}

</script>
<style lang="scss">
.vis-time-axis.vis-foreground {
  background-color: rgb(255 255 255);
}

.timelineStyle {
  position: fixed;
  bottom: 0;
  width: -webkit-fill-available;
  background-color: rgb(255 255 255 / 40%);
  padding-bottom: 15px;
}

.dts-timeline .vis-timeline {
  .vis-item {
    border-color: black;
    background-color: var(--dts-color-foxbrush);
    opacity: 0.9;
    color: white;
    font-size: 12px;
    font-weight: bold;
    cursor: pointer;

    .vis-selected {
      border-color: black;
      background-color: var(--dts-color-foxbrush);
      color: white;
    }
  }

  .vis-tooltip {
    font-size: 12px;
    font-weight: 600;
  }
}

.timelineStyle {
  width: -moz-available;
}

@media only screen and (max-width: 780px) {
  .timeline {
    display: none;
  }
}

.cluster-menu {
  z-index: 1000;
  height: 200px !important;


  .v-card {
    background-color: white;
    color: white;


    .v-card-title {
      background-color: var(--dts-color-foxbrush);
      color: white;
    }

    .v-card-text {
      color: white;
      padding: 0 !important;

      .v-list {
        background-color: var(--dts-color-foxbrush);
        padding: 0;
      }

      .v-list-item {
        color: white;
        font-size: 0.9rem;

        .v-icon {
          margin-right: 10px;
          font-size: 1rem;
        }
      }
    }
  }

  .v-overlay__content {
    bottom: 100px;
  }

  .popup-closer {
    cursor: pointer;
  }
}
</style>
