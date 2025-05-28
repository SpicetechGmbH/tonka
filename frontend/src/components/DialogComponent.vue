<template>
  <v-row justify="space-around">
    <v-col cols="auto">
      <v-dialog
        transition="dialog-bottom-transition"
        width="auto"
        :model-value="showDialog"
        @update:model-value="handleUpdateShowDialog($event)"
      >
        <v-card>
          <v-toolbar
            class="toolbar"
            title="Hinweis"
          >
            <template #prepend>
              <v-icon
                icon="fa fa-map"
              ></v-icon>
            </template>
          </v-toolbar>
          <v-card-text>
            <div class="dialogue-text">
              <p> Die ausgewählte Karte liegt nicht im sichtbaren Kartenausschnitt. Jetzt zur ausgewählten Karte springen? </p>
            </div>
          </v-card-text>
          <v-card-actions class="justify-center">
            <v-btn
              variant="text"
              class="close-button"
              aria-label="Ja"
              @click="jumpToMap()"
            >Ja</v-btn>
            <v-btn
              variant="text"
              class="close-button"
              aria-label="Nein"
              @click="handleUpdateShowDialog(false)"
            >Nein</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-col>
  </v-row>
</template>
<script setup>

const emit = defineEmits(['update:showDialog', 'update:jumpToMap']);

const props = defineProps({
  showDialog: {
    type: Boolean,
    default: false,
  },
});

function jumpToMap() {
  emit('update:jumpToMap', true);
  emit('update:showDialog', false);
}

function handleUpdateShowDialog(event) {
  emit('update:showDialog', event);
}
</script>
<style lang="scss">
.toolbar {
  text-align: left;
  font-weight: bold;
  background-color: rgba(var(--v-theme-foxbrush), 0.8);
  color: white;
}

.v-toolbar-title {
  font-weight: bold;
  font-size: 16px;
  margin-inline-start: -6px !important;
  line-height: 1.4rem;
  padding-top: 6px;
}

.v-toolbar-title__placeholder {
  white-space: normal;
}

.dialogue-text {
  text-align: center;
}

.v-toolbar__content {
  height: auto !important;
  align-items: start;
}

.v-toolbar__prepend {
  margin-inline-start: 0px;
}

.close-button {
  font-size: 13px;
  height: auto;
  padding: 6px;
  font-style: normal;
  background-color: rgba(var(--v-theme-foxbrush), 0.8);
  color: white;
  font-weight: bold;
}

.v-icon {
  --v-icon-size-multiplier: 0.9;
}

/* Desktop */
@media screen and (min-width: 768px) {
  .v-dialog {
    width: 75%;
    height: auto;
  }
}

@media only screen and (max-width: 480px) {
  .v-toolbar-title__placeholder {
    font-size: 14px;
  }
}
</style>
