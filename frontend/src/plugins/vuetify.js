// Vuetify
import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';
import { de } from 'vuetify/locale';

import '@fortawesome/fontawesome-free/css/all.css'; // Ensure your project is capable of handling css files
import '@mdi/font/css/materialdesignicons.css';
import { aliases, fa } from 'vuetify/iconsets/fa';
import { mdi } from 'vuetify/iconsets/mdi';
import { VIconBtn } from 'vuetify/labs/VIconBtn';

const defaultDtsTheme = {
  dark: false,
  colors: {
    primary: '#C95844',
    secondary: '#b67d53',
    accent: 'rgba(232, 199, 82, 0.8)',
    error: '#FF5252',
    info: '#2196F3',
    success: '#4CAF50',
    warning: '#FFC107',
    white: '#fff',
    anticipation: '#ede6b3',
    'font-lvl0': '#000000',
    'font-lvl1': '#505050',
    'font-lvl2': '#787878',
    'font-lvl3': '#a0a0a0',
    'font-hover': 'rgba(0, 0, 0, 0.5)',
    closer: 'rgba(0, 0, 0, 0.4)',
    'default-point-circle-stroke': 'rgba(255, 255, 255, 0.5)',
    'default-point-text': 'rgba(255, 255, 255, 1)',
    'default-point-text-stroke': 'rgba(255, 255, 255, 0.5)',
    'result-point-circle-stroke': 'rgba(255, 255, 255, 0.5)',
    'result-point-text': 'rgba(255, 255, 255, 1)',
    'result-point-text-stroke': 'rgba(255, 255, 255, 0.5)',
    'net-point-circle-stroke': 'rgba(255, 255, 255, 0.5)',
    'net-point-text': 'rgba(255, 255, 255, 1)',
    'net-point-text-stroke': 'rgba(255, 255, 255, 0.5)',
  }
}

const a11yDtsTheme = {
  dark: false,
  colors: {
    primary: 'rgba(0, 0, 0, 0.8)',
    secondary: 'rgba(255, 255, 255, 1)',
    accent: '#ffb300',
    error: '#FF5252',
    info: '#2196F3',
    success: '#4CAF50',
    warning: '#FFC107',
    white: '#fff',
    anticipation: '#ede6b3',
    'font-lvl0': '#000000',
    'font-lvl1': '#000000',
    'font-lvl2': '#000000',
    'font-lvl3': '#000000',
    'font-hover': 'rgba(255, 255, 255, 1)',
    closer: 'rgba(255, 255, 255, 1)',
    'default-point-circle-stroke': 'rgba(0, 0, 0, 0.5)',
    'default-point-text': 'rgba(0, 0, 0, 1)',
    'default-point-text-stroke': 'rgba(0, 0, 0, 0.5)',
    'result-point-circle-stroke': 'rgba(255, 255, 255, 0.8)',
    'result-point-text': 'rgba(255, 255, 255, 1)',
    'result-point-text-stroke': 'rgba(255, 255, 255, 0.5)',
    'net-point-circle-stroke': 'rgba(0, 0, 0, 0.5)',
    'net-point-text': 'rgba(0, 0, 0, 1)',
    'net-point-text-stroke': 'rgba(0, 0, 0, 0.5)',
  }
}

const vuetify = createVuetify({
  components: {
    ...components,
    VIconBtn,
  },
  directives,
  theme: {
    defaultTheme: 'defaultDtsTheme',
    themes: {
      defaultDtsTheme,
      a11yDtsTheme,
    }
  },
  icons: {
    defaultSet: 'fa',
    aliases,
    sets: {
      fa,
      mdi
    },
  },
  locale: {
    locale: 'de',
    fallback: 'en',
    messages: { de },
  }
});

export default vuetify
