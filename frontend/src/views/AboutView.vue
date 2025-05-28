<template>
  <v-container class="d-flex justify-center align-center">
    <v-card class="about-help mx-auto">
      <template v-slot:prepend>
        <span class="about-help__title-left">
          Wir über uns
        </span>
      </template>
      <template #append>
        <a
          href="[eigene_url_eintragen]"
          class="about-help__title-right"
        >
          <span>
            Zum Lexikon
          </span>
        </a>
      </template>
<v-card-text class="textArea">
  <p>Diese Seite bietet umfassende Informationen zu einem spezifischen Thema und richtet sich sowohl an Fachleute als auch an die interessierte Öffentlichkeit. Die Inhalte werden fortlaufend ergänzt und aktualisiert.</p>
  
  <p>Texte, Abbildungen und Kartenmaterial stammen aus verschiedenen Quellen und wurden sorgfältig aufbereitet. Dabei wird, soweit möglich, auf eine freie Nachnutzung der Materialien geachtet. Bitte beachten Sie die jeweiligen Hinweise zu Urheberrechten und Lizenzbedingungen.</p>
  
  <p>Die Nachnutzung der bereitgestellten Inhalte, insbesondere für Bildungszwecke und öffentliche Präsentationen, ist ausdrücklich erwünscht. Bei Interesse an einer weitergehenden Verwendung oder bei Fragen stehen Kontaktmöglichkeiten zur Verfügung.</p>
  
  <p>Wir freuen uns über Anregungen und Rückmeldungen zur Weiterentwicklung dieser Seite.</p>
  
  <div class="impressum">
    <div style="position: absolute; right: 95px; bottom: 10px; z-index: 1000000;">
      <a href="[Link zur Datenschutzerklärung]" class="dts-impressum">Datenschutzerklärung</a>
    </div>
    <div style="position: absolute; right: 25px; bottom: 10px; z-index: 1000000;">
      <a href="[Link zum Impressum]" class="dts-impressum">Impressum</a>
    </div>
  </div>
</v-card-text>
    </v-card>
  </v-container>
</template>

<script setup>
import { onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { useA11y } from '../composables/a11y';

const route = useRoute();
const queryParams = route.query;
const { switchTheme } = useA11y();

onMounted(() => {
  if (queryParams.a11yTheme === 'true') {
    switchTheme();
  }
});
</script>

<style lang="scss">
.v-card-item .v-card-title {
  text-align: center;
  white-space: break-spaces;
}

.v-card-item__append {
  text-align: end;
}

@media only screen and (max-width: 600px) {
  .v-card-item {
    display: flex;
  }

  .about-help__title {
    font-size: 18px;
  }

  .about-help__title-left {
    font-size: 18px;
  }
}

@media (min-width: 1024px) {
  .about-help {
    width: 70%;
  }
}</style>
