import { useTheme } from 'vuetify';
import useMapStore from '../store/mapStore';

export function useA11y() {
  let theme = useTheme();
  let mapStore = useMapStore();

  function switchTheme() {
    console.log(theme.global.current.value);
    console.log("current theme:", theme.global.name.value, "switching to:", theme.global.name.value == 'defaultDtsTheme' ? 'a11yDtsTheme' : 'defaultDtsTheme');

    if (theme.global.name.value == 'defaultDtsTheme') {
      theme.global.name.value = 'a11yDtsTheme';
      document.documentElement.style.setProperty('--dts-color-foxbrush', 'rgba(0, 0, 0, 0.8)');
      document.documentElement.style.setProperty('--dts-color-newwarm', 'rgba(255, 255, 255, 1)');
      document.documentElement.style.setProperty('--dts-color-accent', '#ffb300');
      document.documentElement.style.setProperty('--dts-color-closer', 'rgba(255, 255, 255, 1)');
      document.documentElement.style.setProperty('--dts-color-font-hover', 'rgba(255, 255, 255, 1)');
      mapStore.updateColor();
    } else {
      theme.global.name.value = 'defaultDtsTheme';
      document.documentElement.style.setProperty('--dts-color-foxbrush', 'rgba(201, 88, 68, 0.8)');
      document.documentElement.style.setProperty('--dts-color-newwarm', 'rgba(182, 125, 83, 1)');
      document.documentElement.style.setProperty('--dts-color-accent', 'rgba(232, 199, 82, 0.8)');
      document.documentElement.style.setProperty('--dts-color-closer', 'rgba(0, 0, 0, 0.4)');
      document.documentElement.style.setProperty('--dts-color-font-hover', 'rgba(0, 0, 0, 0.5)');
      mapStore.updateColor();
    }
  }

  return { switchTheme };
}
