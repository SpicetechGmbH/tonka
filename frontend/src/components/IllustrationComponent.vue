<template>
  <v-card class="about-help">
    <template v-slot:title>
      <span class="about-help__title"> {{ illustrationDetailsById?.lemma_title }} </span>
    </template>
    <template #append>
      <ThemeSwitchButton color="#e0e0e0"> Farbschema wechseln </ThemeSwitchButton>
    </template>
    <v-img
      :src="`/img/${illustrationDetailsById?.illustration_file_name}`"
      max-width="100%"
      cover
    ></v-img>
    <v-card-text>
      <p style="padding: 10px;">Bildunterschrift: {{ illustrationDetailsById?.title }}</p>
      <div class="content-meta">
        <p v-if="illustrationDetailsById?.archive_signature">Archivsignatur: {{ illustrationDetailsById?.archive_signature }}</p>
        <p v-if="illustrationDetailsById?.licence">Rechte: {{ illustrationDetailsById?.licence }}</p>
        <p v-if="illustrationDetailsById?.creator">Urheber: {{ illustrationDetailsById?.creator }}</p>
        <p v-if="illustrationDetailsById?.picture_date">Entstehungsdatum: {{ illustrationDetailsById?.picture_date }}</p>
        <p v-if="illustrationDetailsById?.technique_material">Technik: {{ illustrationDetailsById?.technique_material }}</p>
        <p v-if="illustrationDetailsById?.repro">Repro: {{ illustrationDetailsById?.repro }}</p>
        <p v-if="illustrationDetailsById?.transcription_text">Transkription: <span v-html="illustrationDetailsById?.transcription_text"></span></p>
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
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import services from '@/services';
import { useA11y } from '../composables/a11y';
import ThemeSwitchButton from './ThemeSwitchButton.vue';

const { switchTheme } = useA11y();

const route = useRoute();
const queryParams = route.query;

const illustrationDetailsById = ref([]);

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

onMounted(() => {
  if (queryParams.theme === 'a11y' || queryParams.a11yTheme === 'true') {
    switchTheme();
  }
  services.illustration.getLemmaIllustrationDetails(props.lemmaId, props.illustrationNr)
    .then((response) => {
      illustrationDetailsById.value = response.data;
    });
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
