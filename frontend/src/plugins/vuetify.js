// Vuetify
import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';
import 'vuetify/styles';

import '@fortawesome/fontawesome-free/css/all.css'; // Ensure your project is capable of handling css files
import '@mdi/font/css/materialdesignicons.css';
import { aliases, fa } from 'vuetify/iconsets/fa';
import { mdi } from 'vuetify/iconsets/mdi';

const defaultDtsTheme = {
  dark: false,
  colors: {
    primary: '#1976D2',
    secondary: '#424242',
    accent: '#82B1FF',
    error: '#FF5252',
    info: '#2196F3',
    success: '#4CAF50',
    warning: '#FFC107',
    foxbrush: '#C95844',
    newwarm: '#b67d53',
    anticipation: '#ede6b3'
  }
}

const a11yDtsTheme = {
  dark: false,
  colors: {
    primary: '#1976D2',
    secondary: '#424242',
    accent: '#82B1FF',
    error: '#FF5252',
    info: '#2196F3',
    success: '#4CAF50',
    warning: '#FFC107',
    foxbrush: '#000000',
    newwarm: '#ffffff',
    anticipation: '#ede6b3'
  }
}
const vuetify = createVuetify({
  components,
  directives,
  icons: {
    defaultSet: 'fa',
    aliases,
    sets: {
      fa,
      mdi
    },
  },
  theme: {
    defaultTheme: 'defaultDtsTheme',
    themes: {
      defaultDtsTheme,
      a11yDtsTheme,
    }
  },
})

export default vuetify
