<template>
  <div class="grid-container">
    <v-card
      class="card"
      :height="articleComponentHeight"
      style="display: grid;"
    >
      <v-toolbar class="toolbar">
        <v-toolbar-title>
          <v-icon
            style="margin-left: 16px;"
            :icon="getIconByLemmaType(localLemmaDetails?.lemma_type)"
            :aria-label="'Artikel-Typ ' + localLemmaDetails?.lemma_type"
          >
          </v-icon> {{ title }} </v-toolbar-title>
        <template #append>
          <v-btn
            @click="closeArticle"
            tabindex="0"
            aria-label="Schließen"
          ><v-icon color="var(--dts-color-closer)">mdi mdi-close-circle</v-icon></v-btn>
        </template>
      </v-toolbar>
      <v-card-text
        class="textArea"
        :max-height="textAreaHeight"
      >
        <v-card>
          <v-card-text>
            <CarouselComponent
              :carousalTitle="carousalTitle"
              :lemma="localLemmaDetails"
              :lemmaImages="lemmaImages"
              v-model:carouselModel="carouselModel"
              v-model:showDialog="showDialog"
            />
            <v-container>
              <v-row>
                <v-img
                  v-for="(image, i) in lemmaImages"
                  :key="i"
                  :src="`/img/${image.thumbnail_file_name}`"
                  @click="showCarousel(i)"
                />
              </v-row>
            </v-container>
          </v-card-text>
        </v-card>
        <v-container>
          <!-- Button, der die Funktion zum Abspielen der Audio-Datei aufruft -->
          <audio
            v-show="audioAvailable"
            ref="audioPlayer"
            controls
          >
            <source
              :src="audioSrc"
              type="audio/wav"
            /> Ihr Browser unterstützt das Audio-Tag nicht.
          </audio>
          <v-row
            dense
            v-show="!audioAvailable"
          >
            <v-col cols="auto">
              <v-btn
                density="comfortable"
                prepend-icon="fa fa-volume-up"
                @click="requestSyncAudio"
                aria-label="Synchronisation anfordern"
              >Synchronisation anfordern</v-btn>
            </v-col>
          </v-row>
        </v-container>
        <div class="abstract">
          <p> {{ localLemmaDetails?.abstract }} </p>
        </div>
        <div
          class="description"
          v-html="localLemmaDetails?.description"
        >
        </div>
        <div class="greyText">
          <hr />
          <span>Text: {{ localLemmaDetails?.author_name }}</span>
          <hr />
          <div>
            <v-row>
              <v-col cols="auto"> Schlagworte: </v-col>
              <v-col
                cols="auto"
                v-for="keyword in lemma?.keywords"
                :key="keyword.id"
              >
                <v-btn
                  style="height: auto;"
                  class="keywordButton"
                  :aria-label="keyword.keyword"
                >{{ keyword.keyword }}</v-btn>
              </v-col>
            </v-row>
          </div>
          <hr />
          <div
            v-for="reference of lemmaReferences"
            :key="reference.key"
            class="reference-div"
          >
            <span>{{ reference.label }}</span>
            <p v-html="reference.value"></p>
            <hr />
          </div>
          <span>Empfohlene Zitierweise:</span>
          <p> {{ localLemmaDetails?.author_name }} {{ localLemmaDetails?.title }}, publiziert am {{ getDateString(localLemmaDetails.last_update) }} in: Stadtlexikon,<br> URL: {{ articleUrl }} </p>
          <hr />
          <span>Publiziert am: {{ getDateString(localLemmaDetails.last_update) }}</span>
          <div v-if="lemmaStore.lemma.version?.length > 1">Versionen dieses Artikels: <template v-for="version in lemmaStore.lemma.version"><v-btn
                v-if="version.version != localLemmaDetails.version"
                density="comfortable"
                class="ml-1 mr-1"
                @click="routeToArticlePage(version.version)"
              >{{ getDateString(version.last_update) }}</v-btn>
            </template>
          </div>
          <v-card-actions class="buttonAction">
            <v-col class="text-left">
              <v-btn
                icon="fa fa-print"
                @click="routeToArticlePage(null)"
                aria-label="Druckansicht öffnen"
              ></v-btn>
            </v-col>
            <v-col class="text-right">
              <UrlDialog :url=articleUrl />
            </v-col>
          </v-card-actions>
        </div>
      </v-card-text>
      <hr />
    </v-card>
  </div>
</template>
<script setup>
import { computed, onMounted, onUnmounted, ref, watch } from 'vue';
import { useTheme } from 'vuetify';
import CarouselComponent from '../components/CarouselComponent.vue';
import UrlDialog from '../components/UrlDialog.vue';
import { getIconByLemmaType } from '../services/getLemmaIconByType';
import { useLemmaStore } from '../store/lemmaStore';
import { useViewControllerStore } from '../store/viewControllerStore';
import getDateString from '../utils/DateHandler';
import services from '../services';

const theme = useTheme();

const articleComponentHeight = ref('0px');
const showDialog = ref(false);
const carouselModel = ref(0);
const emit = defineEmits([
  'update:toggleArtikelPanel',
  'update:toggleQueryPanel',
  'update:setOpenPanelValue',
  'update:setOpenQueryPanelValue',
]);

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

const lemmaStore = useLemmaStore();
const viewControllerStore = useViewControllerStore();

onMounted(() => {
  calculateArticleComponentHeight();
  window.addEventListener('resize', calculateArticleComponentHeight);
});

onUnmounted(() => {
  window.removeEventListener('resize', calculateArticleComponentHeight);
});

watch(() => viewControllerStore.currentView, (currentView) => {
  if (currentView === 'article') {
    setTimeout(() => {
      calculateArticleComponentHeight();
    }, 10);
    loadSyncAudio();
  }
});

function calculateArticleComponentHeight() {
  articleComponentHeight.value = window.innerHeight
    - document.getElementById('searchbar')?.offsetHeight
    // - document.getElementsByClassName("dts-timeline")[0]?.offsetHeight
    - 110
    - 24 * 2
    + 'px';
};

const localLemmaDetails = computed(() => {
  return lemmaStore.lemma?.version?.[lemmaStore.lemma?.version.length - 1] || {};
});

const title = computed(() => {
  return localLemmaDetails.value?.title || localLemmaDetails.value?.timeline_title || '';
});

const carousalTitle = computed(() => {
  return localLemmaDetails.value?.title;
});

const lemmaImages = computed(() => {
  return lemmaStore.lemma?.illustrations;
});

const lemmaReferences = computed(() => [
  {
    key: 'lemma_reference',
    label: 'Quellenhinweise:',
    value: localLemmaDetails.value?.lemma_reference || null
  },
  {
    key: 'lemma_literature',
    label: 'Literaturhinweise:',
    value: localLemmaDetails.value?.lemma_literature || null
  },
  {
    key: 'gnd_identifier',
    label: 'GND-Identifier:',
    value: localLemmaDetails.value?.gnd_identifier || null
  }
].filter(reference => reference.value !== null)
);

const articleUrl = computed(() => {
  return `${ window.origin }/?id=${ localLemmaDetails.value['lemma_id'] }`;
});


function closeArticle() {
  viewControllerStore.goBack();
};

const audioAvailable = ref(false);
const audioSrc = ref('');
const audioPlayer = ref(null);

async function loadSyncAudio() {
  services.lemmata.getSync(localLemmaDetails.value['lemma_id'], localLemmaDetails.value['version']).then(
    (response) => {
      if (response.status === 200) {
        const blob = new Blob([response.data], { type: 'audio/wav' });
        audioSrc.value = URL.createObjectURL(blob);
        audioPlayer.value.load();
        audioAvailable.value = true;
      } else if (response.status === 204) {
        audioAvailable.value = false;
      } else {
        console.error('Error loading audio:', response.statusText);
      }
    }
  ).catch((error) => {
    console.error('Error fetching audio:', error);
  }
  )
};

function requestSyncAudio() {
  services.lemmata.requestSync(localLemmaDetails.value['lemma_id'], localLemmaDetails.value['version']).then(
    (response) => {
      if (response.status === 200) {
        audioAvailable.value = true;
        loadSyncAudio();
      } else {
        console.error('Error requesting audio:', response.statusText);
      }
    }
  ).catch((error) => {
    console.error('Error requesting audio:', error);
  });
};

function showCarousel(index) {
  carouselModel.value = index;
  showDialog.value = true;
};

function routeToArticlePage(version) {
  console.log('routeToArticlePage', version);
  const path = version ? `/article/${ localLemmaDetails.value['lemma_id'] }/${ version }/${ lemmaStore.lemma.websafeTitle }` : `/article/${ localLemmaDetails.value['lemma_id'] }/${ lemmaStore.lemma.websafeTitle }`;
  window.open(`${ path }?a11yTheme=${ theme.global.name.value === 'a11yDtsTheme' }`, '_blank');
};
</script>
<style lang="scss">
.article-card {}

@mixin text_style {
  text-align: left;
  padding: 10px;
}

.v-responsive {
  flex: none;
}

hr {
  filter: blur(2px);
}

.keywordButton {
  font-size: 10px;
  height: auto;
  padding: 6px;
  font-style: normal;
}

.reference-div {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.v-img {
  cursor: pointer;
}

.v-row {
  gap: 2px;
  flex-wrap: nowrap;
}

.v-img__img--contain {
  height: 70px;
}

.v-img__img {
  position: unset;
  width: auto;
}
</style>
