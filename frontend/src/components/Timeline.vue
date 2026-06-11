<template>
  <div
    id="tl-wrapper"
    class="timelineStyle hidden-sm-and-down"
  >
    <v-dialog
      class="cluster-menu"
      width="300px"
      v-model="clusterDialogActive"
      scrim="transparent"
      contained
      persistent
      scrollable
    >
      <v-card>
        <v-card-title>
          <v-container>
            <v-row justify="center">
              <span>{{ clusterItems.length }} Artikel in diesem Zeitraum</span>
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
            > {{ item.content }} <v-tooltip
                activator="parent"
                location="top"
                :open-delay="500"
              >
                <span class="vis-tl-tooltip">{{ item.date }}<br>{{ item.description }}</span>
              </v-tooltip>
            </v-list-item>
          </v-list>
        </v-card-text>
      </v-card>
    </v-dialog>
    <div
      ref="timelineContainer"
      class="dts-timeline timeline"
      tabindex="0"
      @click="focusTimeline"
    >
    </div>
  </div>
</template>
<script setup>
import '@fortawesome/fontawesome-free/css/all.css';
import { Timeline } from "vis-timeline/standalone";
import { onMounted, ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import { getIconByLemmaType } from "../services/getLemmaIconByType";
import { useLemmaStore } from '../store/lemmaStore';
import { useSearchQueryStore } from '../store/searchQueryStore';
import { useViewControllerStore } from "../store/viewControllerStore";

const route = useRoute();

const lemmaStore = useLemmaStore();
const searchQueryStore = useSearchQueryStore();
const viewControllerStore = useViewControllerStore();

const timelineContainer = ref(null);

const clusterDialogActive = ref(false);
const clusterItems = ref([]);

const options = ref({
  // timeline options go here
  maxHeight: '110px',
  stack: false,
  min: '0000-01-01',
  max: '2265-12-31',
  locale: 'de',
  showMinorLabels: true,
  showMajorLabels: false,
  format: {
    minorLabels: {
      millisecond: '',
      second: '',
      minute: '',
      hour: '',
      weekday: 'D. MMM y',
      day: 'D. MMM y',
      month: 'MMM y',
      year: 'y'
    }
  },
  maxMinorChars: 12,
  cluster: {
    showStipes: true,
  },
  zoomMin: 1000 * 60 * 60 * 24 * 7, // 7 days in milliseconds
  showCurrentTime: false,
  xss: {
    filterOptions: {
      whiteList: {
        i: ['class'],
        br: [],
        div: [],
        span: ['class']
      }
    }
  },
  tooltip: {
    overflowMethod: 'cap',
    template: function (item) {
      var html = '<span class="vis-tl-tooltip">';
      // Only show date and description for single items
      if (item.id) {
        html += `${ item.date }<br>${ item.description }`;
      } else {
        return;
      }
      html += '</span>';
      return html;
    },
  },
  template: function (item) {
    var html = `<i class="${ item.dtsType ? getIconByLemmaType(item.dtsType) : '' }"></i> ${ item.content }`;
    return html;
  },
});

// Create an accessible reference for the timeline
const timeline = ref();

onMounted(async () => {
  // Initialize data
  if (searchQueryStore.alleArtikel.length === 0) {
    await searchQueryStore.initAlleArtikel();
  }
  const artikelData = searchQueryStore.alleArtikel.reduce((acc, artikel) => {
    if (artikel.id && artikel["timeline_date_year"]) {
      acc.push({
        id: artikel["id"],
        content: artikel["timeline_title"] ? artikel["timeline_title"] : artikel["title"],
        start: formatIsoDate(
          artikel["timeline_date_year"],
          artikel["timeline_date_month"] ? artikel["timeline_date_month"] : '01',
          artikel["timeline_date_day"] ? artikel["timeline_date_day"] : '01'),
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
      // Open cluster dialog if a cluster is selected
      if (cluster) {
        clusterDialogActive.value = true;
        clusterItems.value = cluster.data.items;
        const clusterItemIds = clusterItems.value.map(item => item.id.toString());
        timeline.value.focus(clusterItemIds, { zoom: false });

        // Set focus on the first item in the cluster
        // timeline.value.focus(clusterItems.value[0].id, { zoom: false });
      }
    }
  });

  timeline.value.once('rangechanged', () => {
    if (route.query.id || route.params.lemmaLink) {
      timeline.value.focus(route.query.id, { zoom: true });
    }
  });

  // Add keydown listener only when timeline is focused
  timelineContainer.value.addEventListener('keydown', handleKeyPress);

  window.timeline = timeline.value; // Make timeline accessible globally for debugging
});

watch(() => lemmaStore.lemma, (newLemma) => {
  if (newLemma && newLemma.version[0].lemma_id) {
    timeline.value.focus(newLemma.version[0].lemma_id);
  }
});

function handleKeyPress(event) {
  // Only prevent default for handled keys, so they don't scroll the page.
  if (['ArrowUp', 'ArrowDown', 'ArrowLeft', 'ArrowRight'].includes(event.key)) {
    event.preventDefault();
  }
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

function pad2(value) {
  return String(value).padStart(2, '0');
}

function padYear(value) {
  const yearStr = String(value);
  // Ensure years < 1000 are treated as 4-digit years (e.g. 5 -> 0005)
  if (yearStr.startsWith('-')) {
    return '-' + yearStr.slice(1).padStart(4, '0');
  }
  return yearStr.padStart(4, '0');
}

function formatIsoDate(year, month = '01', day = '01') {
  return `${ padYear(year) }-${ pad2(month) }-${ pad2(day) }`;
}

async function showArticle(id) {
  try {
    await lemmaStore.fetchLemma(id);
    viewControllerStore.setCurrentView('article');
  } catch (error) {
    console.error('Error fetching article: ', error);
  }
}

function focusTimeline() {
  timelineContainer.value && timelineContainer.value.focus();
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

  .vis-time-axis.vis-foreground {
    height: 30px !important;
  }

  .vis-item {
    border-color: black;
    background-color: rgba(var(--v-theme-primary), 0.8);
    opacity: 0.9;
    color: white;
    font-size: 12px;
    font-weight: bold;
    cursor: pointer;

    .vis-selected {
      border-color: black;
      background-color: rgba(var(--v-theme-primary), 0.8);
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

.vis-tl-tooltip {
  font-size: 12px;
  font-weight: 600;
  color: rgba(var(--v-theme-primary), 0.8);
}

.v-tooltip .v-overlay__content {
  padding: 5px;
  background-color: #f5f4ed !important;
  box-shadow: 3px 3px 10px rgba(0, 0, 0, 0.2); // soften the shadow
  color: rgba(var(--v-theme-primary), 0.8);
  border-radius: 3px;
  border: 1px solid #808074;
}

.cluster-menu {
  z-index: 1000;
  height: 200px !important;

  .v-card {
    background-color: white;
    color: white;

    .v-card-title {
      background-color: rgba(var(--v-theme-primary), 0.8);
      color: white;
    }

    .v-card-text {
      color: white;
      padding: 0 !important;

      .v-list {
        background-color: rgba(var(--v-theme-primary), 0.8);
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

.dts-timeline.timeline:focus {
  outline: 2px solid rgba(var(--v-theme-primary), 0.8);
}
</style>
