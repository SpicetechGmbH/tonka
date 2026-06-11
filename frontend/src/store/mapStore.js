import { defineStore } from 'pinia';
import services from '../services';

export default defineStore('map', {
  state: () => ({
    showPoints: true,
    historicMaps: [],
    thematicMaps: [],
    mapLayers: [],
    selectedMap: null,
    selectedCompareMap: null,
    rotation: 0,
    transparency: 0,
    // currently shown street marker (null or { streetId, coords: [lon, lat] })
    shownStreet: null,
  }),
  actions: {
    toggleShowPoints() {
      this.showPoints = !this.showPoints;
    },
    // show a street marker on the map (coords in [lon, lat]) and remember the street id
    async showStreetOnMap(street) {
      this.shownStreet = street;

      // Check if street is related to historic map and select it
      if (street && street.historicStreetId) {
        const historicMapResponse = await services.maps.getMapByStreetId(street.historicStreetId);
        if (historicMapResponse && historicMapResponse.data?.map) {
          this.selectedMap = this.historicMaps.find((map) => map.id === historicMapResponse.data.map.map_id);
        } else {
          this.selectedMap = null;
        }
      } else {
        this.selectedMap = null;
      }
    },
    // clear any shown street marker
    clearShownStreet() {
      this.shownStreet = null;
    },
    fetchHistoricMaps() {
      return new Promise((resolve, reject) => {
        services.maps.getAllHistoricMap()
          .then((response) => {
            this.historicMaps = response.data.maps.map(historicMap => {
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
    fetchThematicMaps() {
      return new Promise((resolve, reject) => {
        services.maps.getAllThematicMap()
          .then((response) => {
            this.thematicMaps = response.data.maps.map(thematicMap => {
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
    rotateMap() {
      if (this.selectedMap && this.rotation == 0) {
        this.rotation = this.selectedMap["angle"];
      } else {
        this.rotation = 0;
      }
    }
  }
})
