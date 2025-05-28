import { dtsAxios } from './axios';

export default {
  /**
   * get all lemmata
   */
  getAllLemmata() {
    return new Promise((resolve, reject) => {
      dtsAxios.get("/lemma")
        .then((response) => {
          resolve(response)
        })
        .catch((error) => {
          reject(error)
        })
    });
  },

  /**
   * get lemmata by ID
   * @param {string} lemmaId
   */
  getLemmataById(lemmaId) {
    return new Promise((resolve, reject) => {
      dtsAxios.get(`/lemma/${ lemmaId }`)
        .then((response) => {
          resolve(response);
        })
        .catch((error) => {
          reject(error);
        });
    });
  },

  getSync(lemmaId, lemmaVersion) {
    return new Promise((resolve, reject) => {
      dtsAxios.get(`/lemma/${ lemmaId }/sync/${ lemmaVersion }`, {responseType: 'arraybuffer'})
        .then((response) => {
          resolve(response);
        })
        .catch((error) => {
          reject(error);
        });
    });
  }
}
