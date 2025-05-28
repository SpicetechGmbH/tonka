import { defineStore } from 'pinia';
import services from '../services';

function filterDataByLemmaType(data, lemmaType) {
  return data.filter(item => item.lemma_type === lemmaType);
};

export default defineStore('search', {
  state: () => ({
    alleArtikel: [],
    activeFilter: '',
    activeQueryFilter: '',
    queryResult: {
      orte: [],
      artikel: [],
      illustration: []
    },
    showResults: false,
  }),
  getters: {
    filteredData: state => {
      return {
        person: filterDataByLemmaType(state.alleArtikel, 'PERSON'),
        orte: filterDataByLemmaType(state.alleArtikel, 'PLACE'),
        institution: filterDataByLemmaType(state.alleArtikel, 'INSTITUTION'),
        event: filterDataByLemmaType(state.alleArtikel, 'EVENT'),
        topic: filterDataByLemmaType(state.alleArtikel, 'TOPIC'),
      };
    },

    getAlleArtikelFilterDataByLemmaType: state => {
      if (state.activeFilter === '') {
        return state.alleArtikel
      }
      return state.filteredData[state.activeFilter]
    },

    filteredDataByQuery: state => {
      return {
        person: filterDataByLemmaType(state.queryResult.artikel, 'PERSON'),
        orte: filterDataByLemmaType(state.queryResult.artikel, 'PLACE'),
        institution: filterDataByLemmaType(state.queryResult.artikel, 'INSTITUTION'),
        event: filterDataByLemmaType(state.queryResult.artikel, 'EVENT'),
        topic: filterDataByLemmaType(state.queryResult.artikel, 'TOPIC'),
      };
    },

    getQueryArtikelsFilterDataByLemmaType: state => {
      if (state.activeQueryFilter === '') {
        return state.queryResult.artikel
      }
      return state.filteredDataByQuery[state.activeQueryFilter]
    },
  },
  actions: {
    fetchAlleArtikel() {
      return new Promise((resolve, reject) => {
        services.searchQuery.getAllQueryResult()
          .then((response) => {
            this.alleArtikel = response.data;
            resolve(response.data);
          })
          .catch((error) => reject(error));
      });
    },

    fetchIllustrationArtikels(queryStringIllustration) {
      return new Promise((resolve, reject) => {
        services.searchQuery.getQueryIllustrationResult(queryStringIllustration)
          .then((response) => {
            this.queryResult.illustration = response.data;
            resolve(response.data);
          })
          .catch((error) => reject(error));
      });
    },

    fetchOrteArtikels(queryStringOrte) {
      return new Promise((resolve, reject) => {
        services.searchQuery.getQueryOrteResult(queryStringOrte)
          .then((response) => {
            this.queryResult.orte = response.data;
            resolve(response.data);
          })
          .catch((error) => reject(error));
      });
    },

    fetchQueryArtikels(queryStringArtikel) {
      return new Promise((resolve, reject) => {
        services.searchQuery.getQueryArtikelResult(queryStringArtikel)
          .then((response) => {
            this.queryResult.artikel = response.data.queryData;
            resolve(response.data);
          })
          .catch((error) => reject(error));
      });
    },

  }
})
