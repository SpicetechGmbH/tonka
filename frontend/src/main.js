import { library } from '@fortawesome/fontawesome-svg-core';
// import { far } from '@fortawesome/free-regular-svg-icons';
import { fas } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import { createPinia } from 'pinia';
import { createApp } from 'vue';
import App from './App.vue';
import vuetify from './plugins/vuetify';
import router from './router';
import './styles/customStyles.scss';
import './styles/settings.scss';

const app = createApp(App);
const pinia = createPinia();

app.component('font-awesome-icon', FontAwesomeIcon); // Register component globally
library.add(fas); // Include needed solid icons
// library.add(far); // Include needed regular icons

app.use(vuetify);
app
  .use(router)
  .use(pinia);

app.mount('#app');
