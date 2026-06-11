import { dtsAxios } from './axios';

export default {

  getAllLemmaListResult() {
    return new Promise((resolve, reject) => {
      dtsAxios.get(`/lemma/list`)
        .then((response) => {
          resolve(response);
        })
        .catch((error) => {
          reject(error);
        });
    });
  },

  /**
   * Get articles for query.
   *@param {string} queryStringArtikel
   */
  queryArticles(queryStringArtikel) {
    return new Promise((resolve, reject) => {
      dtsAxios.get(`/query/article/${ queryStringArtikel }`)
        .then((response) => {
          resolve(response);
        })
        .catch((error) => {
          reject(error);
        });
    });
  },


  /**
   * Get illustrations for query.
   *@param {string} queryStringIllustration
   */
  queryIllustrations(queryStringIllustration) {
    return new Promise((resolve, reject) => {
      dtsAxios.get(`/query/illustration/${ queryStringIllustration }`)
        .then((response) => {
          resolve(response);
        })
        .catch((error) => {
          reject(error);
        });
    });
  },

  /**
   * Get net places for query.
   *@param {string} queryStringOrte
   */
  queryNetPlaces(queryStringOrte) {
    return new Promise((resolve, reject) => {
      dtsAxios.get(`/query/net/${ queryStringOrte }`)
        .then((response) => {
          resolve(response);
        })
        .catch((error) => {
          reject(error);
        });
    });
  },

  /**
   * Get maps for query.
   *@param {string} queryStringMap
   */
  queryMaps(queryStringMap) {
    return new Promise((resolve, reject) => {
      dtsAxios.get(`/query/map/${ queryStringMap }`)
        .then((response) => {
          resolve(response);
        })
        .catch((error) => {
          reject(error);
        });
    });
  },
}
