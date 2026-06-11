import { reactive, computed, watch } from 'vue';
import { useTheme } from 'vuetify';

export function useColors() {
  const theme = useTheme();
  const c = computed(() => theme.global.current.value.colors || {});

  return {
    defaultPointCircleFillColor: computed(
      () => c.value.secondary ?? '#b67d53'
    ),
    defaultPointCircleStrokeColor: computed(
      () => c.value['default-point-circle-stroke'] ?? '#000000'
    ),
    defaultPointTextColor: computed(
      () => c.value['default-point-text'] ?? '#000000'
    ),
    defaultPointTextStrokeColor: computed(
      () => c.value['default-point-text-stroke'] ?? 'rgba(0,0,0,0.5)'
    ),

    resultPointCircleFillColor: computed(
      () => c.value.primary ?? '#C95844'
    ),
    resultPointCircleStrokeColor: computed(
      () => c.value['result-point-circle-stroke'] ?? '#000000'
    ),
    resultPointTextColor: computed(
      () => c.value['result-point-text'] ?? '#ffffff'
    ),
    resultPointTextStrokeColor: computed(
      () => c.value['result-point-text-stroke'] ?? 'rgba(0,0,0,0.5)'
    ),

    netPointCircleFillColor: computed(
      () => c.value.accent ?? 'rgba(232, 199, 82, 0.8)'
    ),
    netPointCircleStrokeColor: computed(
      () => c.value['net-point-circle-stroke'] ?? '#000000'
    ),
    netPointTextColor: computed(
      () => c.value['net-point-text'] ?? '#ffffff'
    ),
    netPointTextStrokeColor: computed(
      () => c.value['net-point-text-stroke'] ?? 'rgba(0,0,0,0.5)'
    ),
  }
}

export function syncCssVars(theme) {
  const root = document.documentElement;
  const c = theme.global.current.value.colors || {};

  const set = (name, value) => {
    if (value != null) root.style.setProperty(name, value);
  };

  set('--dts-color-foxbrush', c.primary);
  set('--dts-color-newwarm', c.secondary);
  set('--dts-color-accent', c.accent);
  // …other mappings as required…
}
