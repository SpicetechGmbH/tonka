import { defineStore } from 'pinia';
import { computed, ref, unref } from 'vue';
import services from '../services';
import { useLemmaStore } from './lemmaStore';

function filterDataByLemmaType(data, lemmaType) {
  return data.filter(item => item.lemma_type === lemmaType);
};

export const useSearchQueryStore = defineStore('search', () => {
  const lemmaStore = useLemmaStore();

  // State
  const query = ref('');
  const alleArtikel = ref([]);
  const activeQueryFilter = ref('');
  const queryResult = ref({
    artikel: [],
    illustration: [],
    orte: [],
    map: [],
    street: [],
    streetCurrent: [],
    streetHistoric: []
  });
  const alternativeQuery = ref(null);
  const showResults = ref(false);

  // Getters
  const filteredDataByQuery = computed(() => {
    const types = unref(lemmaStore.lemmaTypes) || [];
    const map = {};

    types.forEach(type => {
      const key = type.lemma_type.toLowerCase();
      map[key] = filterDataByLemmaType(queryResult.value.artikel, type.lemma_type);
    });

    return map;
  });

  const getQueryArtikelsFilterDataByLemmaType = computed(() => {
    if (activeQueryFilter.value === '') {
      return queryResult.value.artikel
    }
    return filteredDataByQuery.value[activeQueryFilter.value];
  });

  // Actions
  async function initAlleArtikel() {
    try {
      const response = await services.searchQuery.getAllLemmaListResult();
      alleArtikel.value = response.data;
      return response.data;
    } catch (error) {
      console.error('Error fetching all articles:', error);
      throw error;
    }
  }

  async function fetchFeaturedArticles() {
    try {
      const response = await services.lemma.getAllLemmata();
      queryResult.value.artikel = response.data.allLemmata.filter(lemma => lemma.featured);
    } catch (error) {
      console.error('Error fetching featured articles:', error);
    }
  }

  function fetchAlleArtikel() {
    queryResult.value.artikel = alleArtikel.value;
    queryResult.value.illustration = [];
    queryResult.value.orte = [];
    queryResult.value.map = [];
    queryResult.value.street = [];
    queryResult.value.streetCurrent = [];
    queryResult.value.streetHistoric = [];
  }

  async function search() {
    activeQueryFilter.value = '';
    return Promise.all([
      fetchArtikel(query.value),
      fetchIllustration(query.value),
      fetchOrte(query.value),
      fetchMaps(query.value)
    ]);
  }

  async function fetchArtikel(queryString) {
    try {
      const response = await services.searchQuery.queryArticles(queryString);
      alternativeQuery.value = response.data.alternativeQuery || null;
      queryResult.value.artikel = response.data.queryData;
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  async function fetchIllustration(queryString) {
    try {
      const response = await services.searchQuery.queryIllustrations(queryString);
      queryResult.value.illustration = response.data;
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  async function fetchOrte(queryString) {
    try {
      const response = await services.searchQuery.queryNetPlaces(queryString);
      queryResult.value.orte = response.data;
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  async function fetchMaps(queryString) {
    try {
      const response = await services.searchQuery.queryMaps(queryString);
      queryResult.value.map = response.data;
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  return {
    query,
    alleArtikel,
    activeQueryFilter,
    queryResult,
    alternativeQuery,
    showResults,
    filteredDataByQuery,
    getQueryArtikelsFilterDataByLemmaType,
    initAlleArtikel,
    fetchFeaturedArticles,
    fetchAlleArtikel,
    search,
    fetchArtikel,
    fetchIllustration,
    fetchOrte,
    fetchMaps,
  };
});
