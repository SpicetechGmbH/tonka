import { syncCssVars } from '@/composables/useColors.js';
import { clearStyleCache } from '@/utils/styleFactory.js';
import { watch } from 'vue';
import { useTheme } from 'vuetify';

export function useA11y() {
  const theme = useTheme();

  function switchTheme() {
    const isDefault = theme.global.name.value === 'defaultDtsTheme';
    if (isDefault) {
      activateA11yTheme();
    } else {
      activateDefaultTheme();
    }

    // refresh shared color values and clear OL style cache so map redraws
    syncCssVars(theme);
    clearStyleCache();
  }

  function activateDefaultTheme() {
    theme.change('defaultDtsTheme');
  }

  function activateA11yTheme() {
    theme.change('a11yDtsTheme');
  }

  watch(() => theme.global.name.value, (newTheme) => { syncCssVars(theme); clearStyleCache(); });

  return { switchTheme, activateDefaultTheme, activateA11yTheme };
}
