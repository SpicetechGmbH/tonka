import { dtsAxios } from './axios';

export default {
  /**
   * get all lemmata
   * @return {Promise} Axios response promise
   */
  getAllLemmata() {
    return dtsAxios.get("/lemma");
  },

  /**
   * get lemma by ID
   * @param {string} lemmaId
   * @return {Promise} Axios response promise
   */
  getLemmaById(lemmaId) {
    return dtsAxios.get(`/lemma/${ lemmaId }`);
  },

  /**
   * get all lemma types
   * @returns {Promise} Axios response promise
   */
  getLemmaTypes() {
    return dtsAxios.get(`/lemma-types`);
  }
}
