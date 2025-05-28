<template>
  <v-container id="print-article">
    <v-card class="about-help">
      <template v-slot:title>
        <span class="about-help__title"> {{ lemma.title }} </span>
      </template>
      <template #append>
        <ThemeSwitchButton color="#e0e0e0"> Farbschema wechseln </ThemeSwitchButton>
        <router-link :to="'/?id=' + lemmaId">
          <v-btn
            variant="outlined"
            color="#e0e0e0"
            aria-label="Artikel mit Karten und Bildern"
          > Artikel mit Karten und Bildern </v-btn>
        </router-link>
        <v-btn
          variant="outlined"
          color="#e0e0e0"
          aria-label="Zum Stadtlexikon"
          @click="routeToStadtlexikon"
        > Stadtlexikon </v-btn>
      </template>
      <v-card-text class="textArea">
        <div class="abstract">
          <p> {{ lemma.abstract }} </p>
        </div><br>
        <div
          class="description"
          v-html="lemma.description"
        >
        </div>
        <div class="greyText">
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
          <p> {{ lemma.author_name }}, {{ lemma.title }}, publiziert am {{ getDateString(lemma.last_update) }} in: Stadtlexikon,</p>
          <p>URL: {{ citationUrl }} </p>
        </div>
        <div class="impressum">
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
      </v-card-text>
    </v-card>
  </v-container>
</template>
<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import services from '@/services';
import getDateString from '@/utils/DateHandler';
import { useRouter, useRoute } from 'vue-router';
import ThemeSwitchButton from './ThemeSwitchButton.vue';
import { useA11y } from '../composables/a11y';

const { switchTheme } = useA11y();

const router = useRouter();
const route = useRoute();
const queryParams = route.query;

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
  init();
});

watch(() => props.lemmaVersion, () => {
  init();
});

function init() {
  if (queryParams.a11yTheme === 'true') {
    switchTheme();
  }
  services.lemmata.getLemmataById(props.lemmaId).then((response) => {
    lemmaDetailsById.value = response.data;
    lemma.value = props.lemmaVersion ? response.data.version[props.lemmaVersion - 1] : response.data.version[response.data.version.length - 1];
  });
}

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
.v-card-item__append {
  display: flex;
  gap: 10px;
}

.v-btn--active {
  color: var(--dts-color-foxbrush);
}

.version-button:focus {
  color: var(--dts-color-foxbrush);
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
