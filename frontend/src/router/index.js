import { createRouter, createWebHistory } from 'vue-router';
import MainView from '../views/MainView.vue';
import { useLemmaStore } from '../store/lemmaStore';
import { useViewControllerStore } from '../store/viewControllerStore';

const routes = [
  {
    path: '/',
    name: 'main',
    component: MainView,
    beforeEnter: async (to, from, next) => {
      const lemmaStore = useLemmaStore();
      const viewControllerStore = useViewControllerStore();
      const id = to.query.id;

      if (id) {
        try {
          await lemmaStore.fetchArticle(id);
          viewControllerStore.setCurrentView('article');
        } catch (error) {
          console.error('Error loading lemma', error);
        }
      }
      next();
    }
  },
  {
    path: '/link/:lemmaLink',
    name: 'Link',
    component: MainView,
    beforeEnter: async (to, from, next) => {
      const lemmaStore = useLemmaStore();
      const viewControllerStore = useViewControllerStore();
      const lemmaLink = to.params.lemmaLink;

      if (lemmaLink) {
        try {
          await lemmaStore.fetchArticle(lemmaLink);
          viewControllerStore.setCurrentView('article');
        } catch (error) {
          console.error('Error loading lemma', error);
        }
      }
      next();
    }
  },
  {
    path: '/article/:lemmaId/:lemmaVersion?/:articleName',
    name: 'PrintArticle',
    component: () => import('../components/PrintArticleComponent.vue'),
    props: true
  },
  {
    path: '/illustration/:lemmaId/:illustrationNr',
    name: 'Illustration',
    component: () => import('../components/IllustrationComponent.vue'),
    props: true
  },
  // {
  //   path: '/illustration/:illustrationId',
  //   name: 'Illustration',
  //   component: () => import('../components/IllustrationComponent.vue'),
  //   props: true
  // },
  {
    path: '/wirueberuns',
    name: 'About',
    component: () => import('../views/AboutView.vue')
  },
  {
    path: '/hilfe',
    name: 'Hilfe',
    component: () => import('../components/HilfeComponent.vue')
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

export default router;
