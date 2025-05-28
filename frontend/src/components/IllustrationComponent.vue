<template>
  <v-card class="about-help">
    <template v-slot:title>
      <span class="about-help__title"> {{ illustrationDetailsById?.lemma_title }} </span>
    </template>
    <v-img
      :src="`/img/${illustrationDetailsById?.illustration_file_name}`"
      max-width="100%"
      cover
    ></v-img>
    <v-card-text>
      <p style="padding: 10px;">Bildunterschrift: {{ illustrationDetailsById?.title }}</p>
      <div class="greyText">
        <p v-if="hasValue(illustrationDetailsById?.archive_signature)">Archivsignatur: {{ illustrationDetailsById?.archive_signature }}</p>
        <p v-if="hasValue(illustrationDetailsById?.licence)">Rechte: {{ illustrationDetailsById?.licence }}</p>
        <p v-if="hasValue(illustrationDetailsById?.creator)">Urheber: {{ illustrationDetailsById?.creator }}</p>
        <p v-if="hasValue(illustrationDetailsById?.picture_date)">Entstehungsdatum: {{ illustrationDetailsById?.picture_date }}</p>
        <p v-if="hasValue(illustrationDetailsById?.technique_material)">Technik: {{ illustrationDetailsById?.technique_materiel }}</p>
        <p v-if="hasValue(illustrationDetailsById?.repro)">Repro: {{ illustrationDetailsById?.repro }}</p>
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
</template>
<script setup>
import { ref } from 'vue';
import services from '../../src/services';

const illustrationDetailsById = ref([]);
const hasValue = (value) => value !== null;

const props = defineProps({
  lemmaId: {
    type: String,
    required: true,
  },
  illustrationNr: {
    type: Number,
    required: true,
  },
});

services.illustration.getLemmaIllustrationDetails(props.lemmaId, props.illustrationNr)
  .then((response) => {
    illustrationDetailsById.value = response.data;
  });
</script>
<style lang="scss">
.v-img {
  padding: 20px;
}

.v-img__img {
  width: -webkit-fill-available;
}

body {
  background-color: #a3a3a3;
}
</style>
