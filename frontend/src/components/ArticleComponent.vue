<template>
  <v-card
    class="article-card"
    :height="windowMinimized ? minimizedArticleComponentHeight : articleComponentHeight"
    style="display: grid;"
  >
    <v-toolbar
      id="article-toolbar"
      class="toolbar"
    >
      <template #prepend>
        <v-icon
          class="mx-3"
          :icon="getIconByLemmaType(localLemmaDetails?.lemma_type)"
          :aria-label="'Artikel-Typ ' + localLemmaDetails?.lemma_type"
        >
        </v-icon>
      </template>
      <v-toolbar-title class="dts-text"> {{ title }} </v-toolbar-title>
      <template #append>
        <v-btn
          v-if="smAndDown && !windowMinimized"
          size="40"
          @click="windowMinimized = true"
          tabindex="0"
          aria-label="Minimieren"
        >
          <v-icon color="rgba(var(--v-theme-closer), 0.4)">fa fa-window-maximize</v-icon>
        </v-btn>
        <v-btn
          size="40"
          v-if="smAndDown && windowMinimized"
          @click="windowMinimized = false"
          tabindex="0"
          aria-label="Maximieren"
        >
          <v-icon color="rgba(var(--v-theme-closer), 0.4)">far fa-window-maximize</v-icon>
        </v-btn>
        <v-btn
          size="40"
          @click="closeArticle"
          tabindex="0"
          aria-label="Schließen"
        >
          <v-icon color="rgba(var(--v-theme-closer), 0.4)">mdi mdi-close-circle</v-icon>
        </v-btn>
      </template>
    </v-toolbar>
    <v-card-text
      v-show="!windowMinimized"
      class="content-panel dts-text"
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
                @keydown.enter.prevent="showCarousel(i)"
                @keydown.space.prevent="showCarousel(i)"
                role="button"
                :aria-label="`Bild ${i + 1}: ${image.title || 'Vorschau'}`"
                tabindex="0"
              />
            </v-row>
          </v-container>
        </v-card-text>
      </v-card>
      <div class="content-panel__abstract dts-text">
        <p v-html="localLemmaDetails?.abstract"></p>
      </div>
      <div
        ref="descriptionRef"
        class="content-panel__description dts-text"
        v-html="lemmaDescriptionWithEmphasis"
      >
      </div>
      <div class="content-meta dts-text">
        <hr />
        <span>Text: {{ localLemmaDetails?.author_name }}</span>
        <hr />
        <div>
          <v-row>
            <v-col cols="auto"> Schlagworte: </v-col>
            <v-col
              cols="auto"
              v-for="keyword in lemmaKeywords"
              :key="keyword.id"
            >
              <v-btn
                style="height: auto;"
                class="keywordButton"
                @click="clickKeyword(keyword.keyword)"
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
        <p>{{ localLemmaDetails?.author_name }}, {{ localLemmaDetails?.title }}, publiziert am {{ getDateString(localLemmaDetails.last_update) }}, in: Stadtlexikon Spicetech,<br> URL: {{ articleUrl }}</p>
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
        <v-card-actions class="content-meta__actions">
          <v-col class="text-left">
            <v-btn
              text
              variant="outlined"
              prepend-icon="fa fa-print"
              @click="routeToArticlePage(null)"
              aria-label="Druckansicht öffnen"
            >Druckfassung</v-btn>
          </v-col>
          <v-col class="text-right">
            <UrlDialog
              :lemmaId="localLemmaDetails['lemma_id']"
              :lemmaVersion="localLemmaDetails['version']"
              :websafeTitle="lemmaStore.lemma.websafeTitle"
            />
          </v-col>
        </v-card-actions>
      </div>
    </v-card-text>
    <hr />
  </v-card>
</template>

<script setup>
import { computed, nextTick, onMounted, onUnmounted, ref, watch } from 'vue';
import { useDisplay, useTheme } from 'vuetify';
import CarouselComponent from '../components/CarouselComponent.vue';
import UrlDialog from '../components/UrlDialog.vue';
import services from '../services';
import { getIconByLemmaType } from '../services/getLemmaIconByType';
import { useLemmaStore } from '../store/lemmaStore';
import { useSearchQueryStore } from '../store/searchQueryStore';
import { useViewControllerStore } from '../store/viewControllerStore';
import getDateString from '../utils/DateHandler';

const theme = useTheme();
const { smAndDown, mdAndUp } = useDisplay();
const windowMinimized = ref(false);

const articleComponentHeight = ref('0px');
const minimizedArticleComponentHeight = computed(() => {
  return document.getElementById('article-toolbar')?.offsetHeight + 'px' || '0px';
});
const showDialog = ref(false);
const carouselModel = ref(0);

const lemmaStore = useLemmaStore();
const searchStore = useSearchQueryStore();
const viewControllerStore = useViewControllerStore();

const descriptionRef = ref(null);
const ensuringAudio = ref(false);

onMounted(() => {
  calculateArticleComponentHeight();
  window.addEventListener('resize', calculateArticleComponentHeight);
  nextTick(() => {
    extendAndAdjustLinksForProperForwarding();
  });
});

onUnmounted(() => {
  window.removeEventListener('resize', calculateArticleComponentHeight);
});

const localLemmaDetails = computed(() => {
  return lemmaStore.lemma?.version?.[lemmaStore.lemma?.version.length - 1] || {};
});

const title = computed(() => {
  return localLemmaDetails.value?.title || localLemmaDetails.value?.timeline_title || '';
});

const carousalTitle = computed(() => {
  return localLemmaDetails.value?.title;
});

function escapeRegExp(string) {
  return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}

function highlightQueryInHtml(html, query) {
  if (!query || !query.trim()) return html;
  const regex = new RegExp(escapeRegExp(query), 'gi');
  const parser = new DOMParser();
  const doc = parser.parseFromString(html, 'text/html');

  function highlightNode(node) {
    if (node.nodeType === Node.TEXT_NODE) {
      const value = node.nodeValue;
      if (!value || !value.trim()) return;

      let match;
      let lastIndex = 0;
      const fragment = doc.createDocumentFragment();
      while ((match = regex.exec(value)) !== null) {
        if (match.index > lastIndex) {
          fragment.appendChild(doc.createTextNode(value.slice(lastIndex, match.index)));
        }
        const bold = doc.createElement('b');
        bold.textContent = match[0];
        fragment.appendChild(bold);
        lastIndex = match.index + match[0].length;
      }
      if (lastIndex > 0) {
        fragment.appendChild(doc.createTextNode(value.slice(lastIndex)));
        node.parentNode.replaceChild(fragment, node);
      }
      return;
    }

    if (node.nodeType === Node.ELEMENT_NODE && node.tagName !== 'A') {
      Array.from(node.childNodes).forEach(highlightNode);
    }
  }

  Array.from(doc.body.childNodes).forEach(highlightNode);
  return doc.body.innerHTML;
}

const lemmaDescriptionWithEmphasis = computed(() => {
  const description = localLemmaDetails.value?.description || '';
  return highlightQueryInHtml(description, searchStore.query);
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

const lemmaImages = computed(() => {
  return lemmaStore.lemma?.illustrations;
});

const lemmaKeywords = computed(() => {
  return lemmaStore.lemma?.keywords || [];
});

const articleUrl = computed(() => {
  return `${ window.origin }/article/${ localLemmaDetails.value['lemma_id'] }/${ localLemmaDetails.value['version'] }/${ lemmaStore.lemma.websafeTitle }.html`;
});

watch(() => viewControllerStore.currentView, (currentView) => {
  if (currentView === 'article' || currentView === 'ort') {
    setTimeout(() => {
      calculateArticleComponentHeight();
    }, 10);
    loadArticleAudioStatus();
  }
}, { immediate: true });

watch(lemmaDescriptionWithEmphasis, () => {
  extendAndAdjustLinksForProperForwarding();
});

watch(() => theme.global.name.value, () => {
  extendAndAdjustLinksForProperForwarding();
});

function calculateArticleComponentHeight() {
  let calculatedHeight = window.innerHeight;

  if (mdAndUp.value) {
    calculatedHeight -= document.getElementById('searchbar')?.offsetTop;
    calculatedHeight -= document.getElementById('searchbar')?.offsetHeight;
    calculatedHeight -= document.getElementById("tl-wrapper")?.offsetHeight;
    calculatedHeight -= 110;
    calculatedHeight -= 24 * 2;
  } else if (smAndDown.value) {
    calculatedHeight -= document.getElementById('searchbar')?.offsetTop;
    calculatedHeight -= document.getElementById('searchbar')?.offsetHeight;
    calculatedHeight -= document.getElementById('footer')?.offsetHeight;
    calculatedHeight -= 40; // Additional padding for mobile view
  }

  articleComponentHeight.value = calculatedHeight + 'px';
};

function closeArticle() {
  viewControllerStore.goBack();
};

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

      const pathnameMatch = url.pathname.match(/^(?:\/dts)?\/link\/([^\/]+)$/);
      const isInternalLinkPath = Boolean(pathnameMatch);
      const isInternalIdLink = url.searchParams.has('id');
      const lemmaId = isInternalIdLink ? url.searchParams.get('id') : pathnameMatch?.[1];

      if (!isInternalIdLink && !isInternalLinkPath) continue;

      if (isInternalIdLink) {
        url.pathname = '/';
      }

      url.searchParams.set('theme', theme.global.name.value === 'a11yDtsTheme' ? 'a11y' : 'default');
      link.href = url.toString();

      if (!link.dataset.routerLinkEnhanced) {
        link.addEventListener('click', async (event) => {
          if (event.defaultPrevented) return;
          if (event.button !== 0 || event.metaKey || event.ctrlKey || event.shiftKey || event.altKey) return;
          event.preventDefault();

          if (lemmaId) {
            try {
              await lemmaStore.fetchLemma(lemmaId);
              viewControllerStore.setCurrentView('article');
            } catch (error) {
              console.error('Error loading lemma from link:', error);
            }
          }

          router.push({
            path: url.pathname,
            query: Object.fromEntries(url.searchParams.entries()),
          });
        });
        link.dataset.routerLinkEnhanced = 'true';
      }
    }
  });
};

const audioAvailable = ref(false);
const audioSrc = ref('');
const audioPlayer = ref(null);

async function loadArticleAudioStatus() {
  console.log('loadArticleAudioStatus');
  try {
    const response = await services.lemma.getArticleAudioStatus(
      localLemmaDetails.value['lemma_id'],
      localLemmaDetails.value['version']
    );
    if (response.status === 200 && response.data?.available && response.data?.audioUrl) {
      audioSrc.value = response.data.audioUrl;
      audioAvailable.value = true;
      await nextTick();
      audioPlayer.value.load();
    } else if (response.status === 204 || !response.data?.available) {
      audioAvailable.value = false;
      audioSrc.value = '';
    } else {
      console.error('Error loading audio:', response.statusText);
    }
  } catch (error) {
    console.error('Error fetching audio:', error);
  }

};

async function ensureArticleAudio() {
  if (ensuringAudio.value) return; // Prevents multiple clicks while the request is running
  ensuringAudio.value = true;
  try {
    const response = await services.lemma.ensureArticleAudio(
      localLemmaDetails.value['lemma_id'],
      localLemmaDetails.value['version']
    );
    if (response.status === 200 || response.status === 202) {
      await loadArticleAudioStatus();
    } else {
      console.error('Error ensuring audio:', response.statusText);
    }
  } catch (error) {
    console.error('Error ensuring audio:', error);
  } finally {
    ensuringAudio.value = false;
  }
};

function showCarousel(index) {
  carouselModel.value = index;
  showDialog.value = true;
};

function routeToArticlePage(version) {
  console.log('routeToArticlePage', version);
  const path = version ? `/article/${ localLemmaDetails.value['lemma_id'] }/${ version }/${ lemmaStore.lemma.websafeTitle }` : `/article/${ localLemmaDetails.value['lemma_id'] }/${ lemmaStore.lemma.websafeTitle }`;
  window.open(`${ path }?theme=${ theme.global.name.value === 'a11yDtsTheme' ? 'a11y' : 'default' }`, '_blank');
};

function clickKeyword(keyword) {
  searchStore.query = keyword;
  viewControllerStore.setCurrentView('result');
  searchStore.search();
}
</script>

<style lang="scss">
.article-card {
  position: fixed;
  right: 8px;
  top: 80px;
  max-width: var(--dts-panel-max-width);

  @media screen and (max-width: 780px) {
    left: 8px;
    right: 8px;
    max-width: none;
    width: auto;
  }
}

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
