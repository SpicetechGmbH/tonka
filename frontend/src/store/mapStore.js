import { defineStore } from 'pinia';
import services from '../services';

export default defineStore('map', {
  state: () => ({
    showPoints: true,
    historicMapData: [],
    thematicMapData: [],
    mapLayers: [],
    selectedMap: null,
    selectedCompareMap: null,
    rotation: 0,
    transparency: 0,
    defaultPointColor: getComputedStyle(document.documentElement).getPropertyValue('--dts-color-newwarm').trim(),
    resultPointColor: getComputedStyle(document.documentElement).getPropertyValue('--dts-color-foxbrush').trim(),
    netPointColor: getComputedStyle(document.documentElement).getPropertyValue('--dts-color-accent').trim(),
  }),
  getters: {

  },
  actions: {
    toggleShowPoints() {
      this.showPoints = !this.showPoints;
    },
    fetchHistoricMapData() {
      return new Promise((resolve, reject) => {
        services.maps.getAllHistoricMap()
          .then((response) => {
            this.historicMapData = response.data.maps.map(historicMap => {
              return {
                ...historicMap,
                title: `${ historicMap['timeline_title'] } ${ historicMap['timeline_date_year'] != null ? historicMap['timeline_date_year'] : '' }`
              };
            });
            resolve(response.data.maps);
          })
          .catch((error) => reject(error));
      });
    },
    fetchThematicMapData() {
      return new Promise((resolve, reject) => {
        services.maps.getAllThematicMap()
          .then((response) => {
            this.thematicMapData = response.data.maps.map(thematicMap => {
              return {
                ...thematicMap,
                title: `${ thematicMap['timeline_title'] } ${ thematicMap['timeline_date_year'] != null ? thematicMap['timeline_date_year'] : '' }`
              };
            });
            resolve(response.data.maps);
          })
          .catch((error) => reject(error));
      });
    },
    updateColor() {
      this.defaultPointColor = getComputedStyle(document.documentElement).getPropertyValue('--dts-color-newwarm').trim();
      console.log("defaultPointColor", this.defaultPointColor);
      this.resultPointColor = getComputedStyle(document.documentElement).getPropertyValue('--dts-color-foxbrush').trim();
      console.log("resultPointColor", this.resultPointColor);
      this.netPointColor = getComputedStyle(document.documentElement).getPropertyValue('--dts-color-accent').trim();
      console.log("netPointColor", this.netPointColor);
    },
    rotateMap() {
      if (this.selectedMap && this.rotation == 0) {
        this.rotation = this.selectedMap["angle"];
      } else {
        this.rotation = 0;
      }
    }
  }
})
