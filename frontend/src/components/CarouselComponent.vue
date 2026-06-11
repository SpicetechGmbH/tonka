<template>
  <v-dialog
    min-width="95%"
    min-height="95%"
    :model-value="showDialog"
    @update:model-value="handleUpdateShowDialog($event)"
  >
    <v-card>
      <v-toolbar
        class="toolbar"
        :title="carousalTitle"
        style="text-align: center;"
      >
        <template #append>
          <v-btn
            icon="mdi:mdi-close-circle"
            variant="text"
            @click="handleUpdateShowDialog(false)"
            aria-label="Schließen"
          ></v-btn>
        </template>
      </v-toolbar>
      <v-card-text>
        <v-carousel
          :model-value="carouselModel"
          show-arrows="hover"
          @update:model-value="handleUpdateCarouselModel($event)"
          hide-delimiter-background
        >
          <v-carousel-item
            v-for="(image, i) in lemmaImages"
            :key="i"
            alt="Image"
          >
            <router-link
              target="_blank"
              :to='`/illustration/${lemma["lemma_id"]}/${lemmaImages[carouselModel].nr}?theme=${theme.global.name.value === "a11yDtsTheme" ? "a11y" : "default"}`'
            >
              <img
                class="carousel-image"
                :src="`/img/${image.illustration_file_name}`"
                alt="Image"
              />
            </router-link>
          </v-carousel-item>
        </v-carousel>
        <div class="image-info">
          <span> {{ lemmaImages[carouselModel].title }} </span><br>
          <span> Archivsignatur: {{ lemmaImages[carouselModel].archive_signature }} </span><br>
          <span> {{ lemmaImages[carouselModel].licence }} </span><br>
        </div>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>
<script setup>
import { onMounted } from 'vue';
import { useTheme } from 'vuetify';

const theme = useTheme();

const emit = defineEmits(['update:showDialog', 'update:carouselModel']);

function handleUpdateShowDialog(event) {
  emit('update:showDialog', event);
};

function handleUpdateCarouselModel(event) {
  emit('update:carouselModel', event);
};

const props = defineProps({
  showDialog: {
    type: Boolean,
    default: false,
  },
  lemma: {
    type: Object,
    default: () => { },
  },
  lemmaImages: {
    type: Array,
    default: () => [],
  },
  carouselModel: {
    type: Number,
    default: 0,
  },
  carousalTitle: {
    type: String,
    default: () => ''
  },
});

function handleKeyPress(event) {
  if (event.key === 'ArrowLeft') {
    let newCarouselValue = (props.carouselModel + props.lemmaImages.length - 1) % props.lemmaImages.length;
    handleUpdateCarouselModel(newCarouselValue);
  } else if (event.key === 'ArrowRight') {
    let newCarouselValue = (props.carouselModel + 1) % props.lemmaImages.length;
    handleUpdateCarouselModel(newCarouselValue);
  }
}

onMounted(() => {
  window.addEventListener('keydown', handleKeyPress);
});

</script>
<style lang="scss">
.image-info {
  color: rgb(var(--v-theme-font-lvl3));
  font-style: italic;
  text-align: center;
  font-size: 14px;
  line-height: 22px;
}

.toolbar {
  text-align: center;
  font-weight: bold;
  background-color: rgba($color: var(--v-theme-primary), $alpha: 0.8);
  color: white;
}

.v-toolbar-title {
  font-weight: bold;
}

.v-toolbar-title__placeholder {
  white-space: normal;
}

.v-carousel__progress {
  color: rgba($color: var(--v-theme-primary), $alpha: 0.8);
}

.carousel-image {
  height: 100%;
  width: auto;
  max-width: 100%;
  display: block;
  margin: auto;
  object-fit: contain;
}

.close-button {
  font-size: 13px;
  height: auto;
  padding: 6px;
  font-style: normal;
  background-color: rgba($color: var(--v-theme-primary), $alpha: 0.8);
  color: white;
  font-weight: bold;
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
