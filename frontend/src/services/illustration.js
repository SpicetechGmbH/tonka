import { dtsAxios } from "./axios";

export default {
  /**
   * get illustration by ID
   * @param {string} illustrationId The ID of the illustration
   */
  getIllustrationById(illustrationId) {
    const url = `/illustration/${ illustrationId }`;

    return new Promise((resolve, reject) => {
      dtsAxios.get(url)
        .then((response) => {
          resolve(response);
        })
        .catch((error) => {
          reject(error);
        });
    });
  },

  /**
   * get illustration details by lemma ID and illustration number
   * @param {*} lemmaId The ID of the lemma
   * @param {*} illustrationNr The number of the illustration
   * @returns The illustration details
   */
  getLemmaIllustrationDetails(lemmaId, illustrationNr) {
    const url = `/illustration/${ lemmaId }/${ illustrationNr }`;

    return new Promise((resolve, reject) => {
      dtsAxios.get(url)
        .then((response) => {
          resolve(response);
        })
        .catch((error) => {
          reject(error);
        });
    });
  }
}
