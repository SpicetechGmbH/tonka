<script setup>
import { useSnackbar } from '@/composables/useSnackbar.js';
import QRCode from 'qrcode';
import { computed, nextTick, ref, watch } from 'vue';
import { useDisplay } from 'vuetify';

const { smAndDown } = useDisplay();

const props = defineProps({
  lemmaId: {
    type: String,
    default: ''
  },
  lemmaVersion: {
    type: Number,
    default: 0
  },
  websafeTitle: {
    type: String,
    default: ''
  }
});

const { showSnackbar } = useSnackbar();
const isActive = ref(false);
const qrcodeArticleCanvas = ref();
const qrcodePermalinkCanvas = ref();

watch(() => isActive.value, (activeValue) => {
  if (activeValue) {
    generateQRCode();
  }
});

const articleUrl = computed(() => {
  return `${ window.origin }/?id=${ props.lemmaId }`;
});

const permalink = computed(() => {
  return `${ window.origin }/article/${ props.lemmaId }/${ props.lemmaVersion }/${ props.websafeTitle }.html`;
});

function generateQRCode() {
  nextTick(() => {
    const options = { width: 150, margin: 2 };

    QRCode.toCanvas(qrcodeArticleCanvas.value, articleUrl.value, options, function (error) {
      if (error) console.error(error);
    });
    QRCode.toCanvas(qrcodePermalinkCanvas.value, permalink.value, options, function (error) {
      if (error) console.error(error);
    });
  });
}

function copyToClipboard(url) {
  navigator.clipboard.writeText(url).then(() => {
    showSnackbar({ message: 'URL in Zwischenablage kopiert', callback: () => { console.log('Snackbar action executed') }, callbackButton: 'OK' });
    closeDialog();
  }).catch(err => {
    console.error('Failed to copy URL: ', err);
  });
}

function closeDialog() {
  isActive.value = false;
}
</script>
<template>
  <v-dialog
    v-model="isActive"
    :class="smAndDown ? 'ma-2' : ''"
    width="100%"
    max-width="760px"
    :height="smAndDown ? '100%' : 'auto'"
    max-height="100%"
    scrollable
  >
    <template #activator="{ props: activatorProps }">
      <v-btn
        text
        v-bind="activatorProps"
        variant="outlined"
        prepend-icon="fa fa-qrcode"
        aria-label="URL anzeigen"
      >URL anzeigen</v-btn>
    </template>
    <template #default="{ isActive }">
      <v-card class="url-dialog-card">
        <v-toolbar
          class="toolbar"
          title="URL anzeigen"
        >
          <template #prepend>
            <v-btn
              icon="fa fa-qrcode"
              variant="text"
            ></v-btn>
          </template>
          <template #append>
            <v-btn
              @click="closeDialog"
              tabindex="0"
              aria-label="Schließen"
            ><v-icon color="rgba(var(--v-theme-closer), 0.4)">mdi mdi-close-circle</v-icon></v-btn>
          </template>
        </v-toolbar>

        <v-card flat>
          <v-card-title>
            <span class="dialogue-title">Artikel mit Karte und Bildern</span>
          </v-card-title>
          <v-card-text class="px-4 text-center">
            <canvas
              class="qrCodeCanvas"
              ref="qrcodeArticleCanvas"
            ></canvas>
            <v-text-field
              v-model="articleUrl"
              type="text"
              readonly
              variant="solo"
              density="compact"
              :hint="smAndDown ? 'Icon am Ende tippen zum Kopieren' : 'Icon am Ende klicken zum Kopieren'"
              persistent-hint
              tabindex="0"
              aria-label="URL Textfeld"
            >
              <template #append-inner>
                <v-tooltip
                  location="top"
                  text="Kopieren"
                >
                  <template v-slot:activator="{ props }">
                    <v-icon
                      v-bind="props"
                      iconColor="rgba(var(--v-theme-closer), 0.4)"
                      class="copy-icon"
                      @click="copyToClipboard(articleUrl)"
                      tabindex="0"
                      aria-label="URL kopieren"
                    >fa fa-copy</v-icon>
                  </template>
                </v-tooltip>
              </template>
            </v-text-field>
          </v-card-text>
        </v-card>
        <v-card flat>
          <v-card-title>
            <span class="dialogue-title">Permalink zur Druckfassung</span>
          </v-card-title>
          <v-card-text class="px-4 text-center">
            <canvas
              class="qrCodeCanvas"
              ref="qrcodePermalinkCanvas"
            ></canvas>
            <v-text-field
              v-model="permalink"
              type="text"
              readonly
              variant="solo"
              density="compact"
              :hint="smAndDown ? 'Icon am Ende tippen zum Kopieren' : 'Icon am Ende klicken zum Kopieren'"
              persistent-hint
              tabindex="0"
              aria-label="URL Textfeld"
            >
              <template #append-inner>
                <v-tooltip
                  location="top"
                  text="Kopieren"
                >
                  <template v-slot:activator="{ props }">
                    <v-icon
                      v-bind="props"
                      iconColor="rgba(var(--v-theme-closer), 0.4)"
                      class="copy-icon"
                      @click="copyToClipboard(permalink)"
                      tabindex="0"
                      aria-label="URL kopieren"
                    >fa fa-copy</v-icon>
                  </template>
                </v-tooltip>
              </template>
            </v-text-field>
          </v-card-text>
        </v-card>
      </v-card>
    </template>
  </v-dialog>
</template>
<style lang="scss">
.url-dialog-card {
  overflow-y: auto;
  overflow-x: hidden;
}

.qrCodeCanvas {
  display: block;
  margin: 0 auto;
}
</style>
