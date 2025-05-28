<template>
  <v-dialog
    absolute
    max-width="450"
  >
    <template #activator="{ props: activatorProps }">
      <v-btn
        v-bind="activatorProps"
        variant="text"
        class="iconA11Y"
        width="auto"
        icon="fa fa-low-vision"
        aria-label="Farbschema Barrierefreiheit"
      >
      </v-btn>
    </template>
    <v-card class="mx-auto" prepend-icon="fa fa-low-vision">
      <template #title> Farbschema Barrierefreiheit </template>
      <v-card-text class="bg-surface-light pt-4 pb-4">
        <p> Hier kann das Farbschema für eine barrierefreiere Darstellung der Anwendung aktiviert werden. </p>
      </v-card-text>
      <v-card-actions class="bg-surface-light">
        <ThemeSwitchButton
          variant="outlined"
          prepend-icon="fa fa-low-vision"
          color="foxbrush"
        >Farbschema wechseln</ThemeSwitchButton>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script setup>
import ThemeSwitchButton from './ThemeSwitchButton.vue';
</script>
<style lang="scss">
@mixin icon_style {
  position: fixed;
  left: 16px;
  cursor: pointer;
  color: rgba(var(--v-theme-foxbrush), 0.8);
}

.v-btn--icon.v-btn--size-default {
  &.iconA11Y {
    @include icon_style();
    top: 10%;
    font-size: 1.2rem;

    @media only screen and (max-width: 780px) {
      top: 15%;
      font-size: 1.5rem;
    }
  }
}
</style>
