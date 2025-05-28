<script setup>
import { ref, watch, nextTick } from 'vue';
import QRCode from 'qrcode';
import { useSnackbar } from '@/composables/useSnackbar';

const props = defineProps({
  url: {
    type: String,
    default: ''
  }
});

const { snackbar, showSnackbar } = useSnackbar();
const isActive = ref(false);
const qrcodeCanvas = ref();

watch(() => isActive.value, (activeValue) => {
  if (activeValue) { 
    generateQRCode();
  }
});

function generateQRCode() {
  nextTick(() => {
    QRCode.toCanvas(qrcodeCanvas.value, props.url, function (error) {
      if (error) console.error(error);
    });
  });
}

function copyToClipboard() {
  navigator.clipboard.writeText(props.url).then(() => {
    showSnackbar('URL in Zwischenablage kopiert');
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
    width="auto"
  >
    <template #activator="{ props: activatorProps }">
      <v-btn
        text
        :="activatorProps"
        variant="outlined"
        prepend-icon="fa fa-qrcode"
        aria-label="URL anzeigen"
      > URL anzeigen </v-btn>
    </template>
    <template #default="{ isActive }">
      <v-card>
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
            ><v-icon color="var(--dts-color-closer)">mdi mdi-close-circle</v-icon></v-btn>
          </template>
        </v-toolbar>
        <v-card-text>
          <div class="dialogue-text">
            <canvas ref="qrcodeCanvas"></canvas>
            <p> {{ url }} </p>
          </div>
        </v-card-text>
        <v-card-actions class="justify-center">
          <v-btn
            variant="text"
            class="close-button"
            @click="copyToClipboard"
          >URL kopieren</v-btn>
        </v-card-actions>
      </v-card>
    </template>
  </v-dialog>
</template>
<style lang="scss"></style>
