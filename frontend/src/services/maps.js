import { dtsAxios } from './axios';

export default {
  /**
   * get the base map
   * @returns {Promise} Axios response promise
   */
  getBaseMap() {
    return dtsAxios.get(`/map/BASEMAP`);
  },

  /**
  * get all Historic maps
  * @returns {Promise} Axios response promise
  */
  getAllHistoricMap() {
    return dtsAxios.get(`/map/HISTORICMAP`);
  },

  /**
   * get all Thematic maps
   * @returns {Promise} Axios response promise
   */
  getAllThematicMap() {
    return dtsAxios.get(`/map/THEMATICMAP`);
  },

  /**
   * get map by street ID
   * @param {*} streetId 
   * @returns {Promise} Axios response promise
   */
  getMapByStreetId(streetId) {
    return dtsAxios.get(`/map/mapbystreet/${ streetId }`);
  }

}
