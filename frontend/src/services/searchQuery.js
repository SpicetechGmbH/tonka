import { dtsAxios } from './axios';

export default {

  getAllQueryResult() {
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
   * get query string
   *@param {string} queryStringArtikel
   */
  getQueryArtikelResult(queryStringArtikel) {
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
   * get query string
   *@param {string} queryStringIllustration
   */
  getQueryIllustrationResult(queryStringIllustration) {
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
   * get query string
   *@param {string} queryStringOrte
   */
  getQueryOrteResult(queryStringOrte) {
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
}
