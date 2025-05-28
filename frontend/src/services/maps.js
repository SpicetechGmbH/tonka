import { dtsAxios } from './axios';

export default {
  /**
  * get all Historic maps
  *
  */
  getAllHistoricMap() {
    return new Promise((resolve, reject) => {
      dtsAxios.get(`/map/HISTORICMAP`)
        .then((response) => {
          resolve(response);
        })
        .catch((error) => {
          reject(error);
        });
    });
  },

  /**
 * get all Thematic maps
 *
 */
  getAllThematicMap() {
    return new Promise((resolve, reject) => {
      dtsAxios.get(`/map/THEMATICMAP`)
        .then((response) => {
          resolve(response);
        })
        .catch((error) => {
          reject(error);
        });
    });
  },
}
