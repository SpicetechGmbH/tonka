<template>
  <v-container id="print-article">
    <v-card
      ref="cardRef"
      class="about-help"
    >
      <template v-slot:title>
        <span class="about-help__title"> {{ lemma.title }} </span>
      </template>
      <template #append>
        <ThemeSwitchButton color="#e0e0e0">
          <span v-if="!smAndDown">Farbschema wechseln</span>
        </ThemeSwitchButton>
        <router-link :to="`/?id=${lemmaId}&theme=${theme.global.name.value === 'a11yDtsTheme' ? 'a11y' : 'default'}`">
          <v-btn
            variant="outlined"
            color="#e0e0e0"
            title="Artikel mit Karten und Bildern"
            aria-label="Artikel mit Karten und Bildern"
          >
            <span v-if="!smAndDown">Artikel mit Karten und Bildern</span>
            <v-icon v-if="smAndDown">fa fa-newspaper</v-icon>
          </v-btn>
        </router-link>
        <v-btn
          variant="outlined"
          color="#e0e0e0"
          title="Zum Stadtlexikon"
          aria-label="Zum Stadtlexikon"
          @click="routeToStadtlexikon"
        >
          <span v-if="!smAndDown">Stadtlexikon</span>
          <v-icon v-if="smAndDown">fa fa-book-atlas</v-icon>
        </v-btn>
      </template>
      <v-card-text class="content-panel">
        <div class="content-panel__abstract">
          <p v-html="lemma.abstract"></p>
        </div>
        <div
          ref="descriptionRef"
          class="content-panel__description"
          v-html="lemma.description"
        ></div>
        <div class="content-meta">
          <span>Text: {{ lemma.author_name }}</span>
          <span>Schlagwort: {{ lemma.keyword }}</span>
          <div
            v-for="reference of lemmaReferences"
            :key="reference.key"
            class="reference-div"
          >
            <span>{{ reference.label }}</span>
            <p v-html="reference.value"></p>
          </div>
          <span>GND-Identifier: {{ lemma.gnd_identifier }}</span>
          <span>Publiziert am: {{ getDateString(lemma.last_update) }}</span>
          <div v-if="lemmaDetailsById.version?.length > 1">
            <v-row>
              <v-col cols="auto"> Versionen des Artikels: </v-col>
              <v-col
                cols="auto"
                v-for="version in lemmaDetailsById.version"
                :key="version.version"
              >
                <v-btn
                  class="version-button"
                  density="compact"
                  :active="version.version == lemma.version ? true : false"
                  aria-label="Artikelversion wechseln"
                  @click="routeToVersion(version['lemma_id'], version.version)"
                > {{ getDateString(version.last_update) }} </v-btn>
              </v-col>
            </v-row>
          </div>
          <span>Empfohlene Zitierweise:</span>
          <p>{{ lemma.author_name }}, {{ lemma.title }}, publiziert am {{ getDateString(lemma.last_update) }}, in: Stadtlexikon,</p>
          <p>URL: {{ citationUrl }}</p>
        </div>
      </v-card-text>
      <div
        ref="impressumRef"
        class="impressum"
      >
        <div style="position: absolute; right: 95px; bottom: 10px;">
          <a
            href="/datenschutzerklaerung"
            class="dts-impressum"
          >Datenschutzerklärung</a>
        </div>
        <div style="position: absolute; right: 25px; bottom: 10px;">
          <a
            href="/impressum"
            class="dts-impressum"
          >Impressum</a>
        </div>
      </div>
    </v-card>
  </v-container>
</template>
<script setup>
import services from '@/services';
import getDateString from '@/utils/DateHandler';
import { computed, nextTick, onMounted, onUnmounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useDisplay, useTheme } from 'vuetify';
import { useA11y } from '../composables/a11y';
import ThemeSwitchButton from './ThemeSwitchButton.vue';

const theme = useTheme();
const { smAndDown } = useDisplay();
const { switchTheme } = useA11y();

const router = useRouter();
const route = useRoute();
const queryParams = route.query;

const cardRef = ref(null);
const descriptionRef = ref(null);
const impressumRef = ref(null);

const lemmaDetailsById = ref([]);
const lemma = ref({});
const props = defineProps({
  lemmaId: {
    type: String,
    required: true,
  },
  lemmaVersion: {
    type: String,
    required: false
  },
  articleName: {
    type: String,
    required: false
  }
});

onMounted(() => {
  window.addEventListener('resize', updateContentPanelHeight);
  init();
});

onUnmounted(() => {
  window.removeEventListener('resize', updateContentPanelHeight);
});

watch(() => props.lemmaVersion, () => {
  init();
});

watch(smAndDown, () => {
  updateContentPanelHeight();
});

watch(() => theme.global.name.value, () => {
  extendAndAdjustLinksForProperForwarding();
});

function init() {
  if (queryParams.theme === 'a11y' || queryParams.a11yTheme === 'true') {
    switchTheme();
  }
  services.lemma.getLemmaById(props.lemmaId).then((response) => {
    lemmaDetailsById.value = response.data;
    lemma.value = props.lemmaVersion ? response.data.version[props.lemmaVersion - 1] : response.data.version[response.data.version.length - 1];
    extendAndAdjustLinksForProperForwarding();
    updateContentPanelHeight();
  });
}

function updateContentPanelHeight() {
  nextTick(() => {
    const cardElement = cardRef.value?.$el || cardRef.value;
    if (!cardElement) {
      return;
    }

    const titleElement = cardElement.querySelector('.v-card-item');
    const titleHeight = titleElement?.offsetHeight || 0;
    const impressumHeight = impressumRef.value?.offsetHeight || 0;

    cardElement.style.setProperty('--print-title-height', `${ titleHeight }px`);
    cardElement.style.setProperty('--print-impressum-height', `${ impressumHeight }px`);
  });
}

/**
 * Extends links with the current theme and adjusts them to work within the app.
 * Links get adjusted depending on whether they use 'id=' as a query parameter or not. These links need adjustment to work when the current page is also using url path.
 */
function extendAndAdjustLinksForProperForwarding() {
  nextTick(() => {
    const links = descriptionRef.value?.getElementsByTagName('a') || [];
    for (let link of links) {
      const url = new URL(link.href);
      if (url.origin !== window.location.origin) continue;
      if (url.search.includes('id=')) { url.pathname = '/'; }
      url.searchParams.set('theme', theme.global.name.value === 'a11yDtsTheme' ? 'a11y' : 'default');
      link.href = url.toString();
    }
  });
};

const citationUrl = computed(() => {
  let url = `${ window.location.protocol }//${ window.location.host }/article/${ props.lemmaId }/`;
  if (props.lemmaVersion) {
    url += `${ props.lemmaVersion }/`;
  }
  url += `${ lemmaDetailsById.value.websafeTitle }.html`;
  return url;
});

const lemmaReferences = computed(() => [
  {
    key: 'LEMMA_REFERENCE',
    label: 'Quellenhinweise:',
    value: lemma.value.lemma_reference || null
  },
  {
    key: 'LEMMA_LITERATURE',
    label: 'Literaturhinweise:',
    value: lemma.value.lemma_literature || null
  }
].filter(reference => reference.value !== null)
);

function routeToStadtlexikon() {
  router.push({ name: 'main' });
};

function routeToVersion(id, version) {
  router.push({ name: 'PrintArticle', params: { lemmaId: id, lemmaVersion: version, articleName: props.articleName } });
};
</script>
<style lang="scss">
#print-article {
  height: 100dvh;
  overflow-y: hidden;
}

#print-article .about-help .content-panel {
  height: calc(100% - var(--print-title-height, 0px) - var(--print-impressum-height, 0px));
}

.v-card-item__append {
  display: flex;
  gap: 10px;
}

.v-btn--active {
  color: rgba(var(--v-theme-primary), 0.8);
}

.version-button:focus {
  color: rgba(var(--v-theme-primary), 0.8);
}

@media only screen and (max-width: 780px) {
  .v-card-item {
    display: grid;
    grid-template-columns: 1fr;
    grid-template-rows: 1fr 1fr;
    grid-template-areas: "content"
      "append";
  }

  .v-card-item__append {
    padding-inline-start: 0px;
  }

  .v-btn {
    letter-spacing: 0;
  }

  .v-btn--size-default {
    padding: 0 3px;
  }
}
</style>
