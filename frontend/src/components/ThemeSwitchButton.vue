<template>
  <v-btn
    v-bind="activatorProps"
    :variant="variant"
    class="iconA11Y"
    width="auto"
    :prepend-icon="prependIcon"
    :color="color"
    aria-label="Farbschema umschalten"
    @click="switchTheme"
  >
    <slot></slot>
  </v-btn>
</template>
<script setup>
import { useA11y } from '../composables/a11y';

const { switchTheme } = useA11y();

const props = defineProps({
  variant: {
    type: String,
    default: 'outlined'
  },
  prependIcon: {
    type: String,
    default: 'fa fa-person-walking-with-cane'
  },
  color: {
    type: String,
    default: 'foxbrush'
  }
});

</script>
<style lang="scss">
</style>
