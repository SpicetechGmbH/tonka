<script setup>
import { useRoute } from 'vue-router';
import { Map, View } from 'ol';
import 'ol/ol.css';
import Feature from 'ol/Feature.js';
import Attribution from 'ol/control/Attribution';
import WMTSCapabilities from 'ol/format/WMTSCapabilities.js';
import TileGrid from 'ol/tilegrid/TileGrid';
import Point from 'ol/geom/Point.js';
import VectorLayer from 'ol/layer/Vector';
import TileLayer from 'ol/layer/Tile';
import ImageLayer from 'ol/layer/Image';
import ImageWMS from 'ol/source/ImageWMS';
import TileWMS from 'ol/source/TileWMS';
import XYZ from 'ol/source/XYZ';
import ImageArcGISRest from 'ol/source/ImageArcGISRest';
import { addProjection, toLonLat, fromLonLat, transform } from 'ol/proj';
import OSM, { ATTRIBUTION } from 'ol/source/OSM.js';
import Projection from 'ol/proj/Projection.js';
import Cluster from 'ol/source/Cluster.js';
import VectorSource from 'ol/source/Vector.js';
import WMTS, { optionsFromCapabilities } from 'ol/source/WMTS.js';
import { Circle, Fill, Stroke, Style, Text } from 'ol/style.js';
import Overlay from 'ol/Overlay.js';
import services from '../../src/services';
import { ref, reactive, onMounted, watch } from 'vue';
import { boundingExtent } from 'ol/extent.js';
import { getIconByLemmaType } from '../services/getLemmaIconByType';
import { useLemmaStore } from '../store/lemmaStore';
import useMapStore from '../store/mapStore';
import { useViewControllerStore } from '../store/viewControllerStore';
import axios from '../services/axios';
import { useTheme } from 'vuetify';
import { useA11y } from '../composables/a11y';
import proj4 from 'proj4';
import { register } from 'ol/proj/proj4';

register(proj4);
proj4.defs('EPSG:25832', '+proj=utm +zone=32 +ellps=GRS80 +units=m +no_defs');

const { switchTheme } = useA11y();

const route = useRoute();
const theme = useTheme();

const lemmaStore = useLemmaStore();
const mapStore = useMapStore();
const viewControllerStore = useViewControllerStore();

const featureModel = ref([]);
const map = reactive({});
const parser = new WMTSCapabilities();
const featureLayer = ref(null);
const featuredFeatureLayer = ref(null);
const netFeatureLayer = ref(null);

const utmProjection = new Projection({
  code: "EPSG:25832",
  units: "m"
});

onMounted(() => {
  utmProjectionOnMap();
  // initGrayBackgroundOnMap();
  // initializeBaseMap();
  openPopupOnMapClick();
  initFeatureLayer();
  initFeaturedFeatureLayer();
  initFeatures();

  if (route.query.id || route.params.lemmaLink) {
    showArticleFeature();
  }

  // logExtentOnMapMoveEnd();
});

watch(() => lemmaStore.lemma, (newLemma, oldLemma) => {
  if (newLemma) {
    featureLayer.value.setVisible(true);
    if (netFeatureLayer.value) {
      netFeatureLayer.value.setVisible(false);
    }
    showArticleFeature();
  }
});

watch(() => viewControllerStore.currentView, (currentView, lastView) => {
  if (currentView === 'map') {
    featureLayer.value.setVisible(true);
    if (netFeatureLayer.value) {
      netFeatureLayer.value.setVisible(false);
      closeAllPopups();
    }
  } else if (currentView === 'article') {
    featureLayer.value.setVisible(true);
    if (netFeatureLayer.value) {
      netFeatureLayer.value.setVisible(false);
    }
    showArticleFeature();
  } else {
    featureLayer.value.setVisible(true);
    if (netFeatureLayer.value) {
      netFeatureLayer.value.setVisible(false);
      closeAllPopups();
    }
  }
});

watch(() => mapStore.showPoints, (newShowPoints) => {
  toggleMapPoints(newShowPoints);
});

watch(() => mapStore.rotation, (newRotation) => {
  map.value.getView().setRotation((newRotation * Math.PI) / 180);
});

watch(() => mapStore.transparency, (newTransparency) => {
  if (!mapStore.selectedMap) {
    return;
  }
  const id = mapStore.selectedMap["id"];
  mapStore.mapLayers.find((layer) => layer.get("id") === id).setOpacity(1 - newTransparency / 100);
});

watch(() => mapStore.selectedMap, (newSelectedMap, oldSelectedMap) => {
  // hide old map (it can be null if no map was selected yet or if the map was deselected)
  if (oldSelectedMap != null) {
    let oldMapLayer = mapStore.mapLayers.find((layer) => layer.get("id") === oldSelectedMap["id"]);
    if (oldMapLayer) {
      oldMapLayer.setVisible(false);
    }
  }

  if (newSelectedMap == null) {
    return;
  }

  initMap(newSelectedMap);
});

watch(() => mapStore.selectedCompareMap, (newSelectedCompareMap, oldSelectedCompareMap) => {
  // hide old map (it can be null if no map was selected yet or if the map was deselected)
  if (oldSelectedCompareMap != null) {
    let oldMapLayer = mapStore.mapLayers.find((layer) => layer.get("id") === oldSelectedCompareMap["id"]);
    if (oldMapLayer) {
      oldMapLayer.setVisible(false);
    }
  }

  if (newSelectedCompareMap == null) {
    return;
  }

  initMap(newSelectedCompareMap);
});

const webMercatorProjection = new Projection({
  code: "EPSG:3857",
  units: "m"
});

function utmProjectionOnMap() {
  // Stuttgart coordinates: 48.7758° N, 9.1829° E
  const stuttgartLonLat = [9.1829, 48.7758];
  const stuttgartWebMercator = fromLonLat(stuttgartLonLat, webMercatorProjection); // Converts to EPSG:3857

  map.value = new Map({
    target: 'map',
    layers: [
      new TileLayer({
        source: new OSM()
      })
    ],
    view: new View({
      projection: webMercatorProjection,
      center: stuttgartWebMercator,
      zoom: 13, // 12-14 is good for a city
      loadTilesWhileAnimating: true,
      loadTilesWhileInteracting: true,
    })
  });
  map.value.updateSize();
  window.map = map.value;

  function toggleCursorStyle(event) {
    const pixel = event.pixel;
    const feature = map.value.forEachFeatureAtPixel(pixel, (feature) => feature);
    const targetElement = map.value.getTargetElement();

    if (feature) {
      targetElement.classList.add('cursor-pointer');
    } else {
      targetElement.classList.remove('cursor-pointer');
    };
  };
  map.value.on('pointermove', toggleCursorStyle);
};

function initGrayBackgroundOnMap() {
  const osmLayer = new TileLayer({
    source: new OSM()
  });
  map.value.addLayer(osmLayer);
};

function initializeBaseMap() {
  fetch('https://sgx.geodatenzentrum.de/wmts_topplus_open/1.0.0/WMTSCapabilities.xml')
    .then(function (response) { return response.text(); }).then(function (text) {
      const result = parser.read(text);
      const options = optionsFromCapabilities(result, {
        layer: 'web_grau',
        matrixTileSet: 'EU_EPSG_25832_TOPPLUS',
        projection: 'EPSG:25832',
        attributions: [new Attribution(
          {
            html: `Kartendarstellung und Präsentationsgraphiken: © Bundesamt für Kartographie und Geodäsie (2022),
            Datenquellen: https://sgx.geodatenzentrum.de/web_public/gdz/datenquellen/Datenquellen_TopPlusOpen.html`,
          })]
      });

      const layer = new TileLayer({
        zIndex: 0,
        opacity: 0.5,
        source: new WMTS(options)
      });
      map.value.addLayer(layer);
    }).catch(reason => console.error(reason));
};

function initFeatureLayer() {
  let featureVectorSource = new VectorSource({
    features: []
  });

  let featureClusterSource = new Cluster({
    distance: 12,
    source: featureVectorSource
  });

  featureLayer.value = new VectorLayer({
    name: 'featureLayer',
    source: featureClusterSource,
    zIndex: 11001,
    style(feature) {
      const featureCount = feature.get('features').length;

      let textStyle;
      let textFillColor;
      let circleStrokeColor;
      if (theme.global.name.value === 'a11yDtsTheme') {
        textFillColor = '#000'
        circleStrokeColor = 'rgba(0, 0, 0, 0.5)'
      } else {
        textFillColor = '#fff'
        circleStrokeColor = 'rgba(255, 255, 255, 0.5)'
      };

      if (featureCount > 1) {
        textStyle = new Text({
          text: featureCount.toString(),
          fill: new Fill({
            color: textFillColor,
          }),
          font: '10px sans-serif',
          textAlign: 'center',
          textBaseline: 'middle',
          offsetX: 0,
          offsetY: 0,
          stroke: new Stroke({
            color: 'rgba(255, 255, 255, 0.5)',
            width: 1
          }),
        });
      };

      let fillColor = mapStore.defaultPointColor;
      const style = new Style({
        image: new Circle({
          radius: 11,
          stroke: new Stroke({
            color: circleStrokeColor,
          }),
          fill: new Fill({
            color: fillColor,
          }),
          cursor: 'pointer',
        }),
        text: textStyle
      });

      watch(() => mapStore.defaultPointColor, (newColor) => {
        console.log("defaultPointerColor watcher called")
        fillColor = newColor;
        if (style.getImage().getFill()) {
          style.getImage().getFill().setColor(fillColor);
        }
        featureLayer.value.changed();
      });

      return style;
    },
  });
  map.value.addLayer(featureLayer.value);
}

function initFeaturedFeatureLayer() {
  let featuredFeatureVectorSource = new VectorSource({
    features: []
  });

  featuredFeatureLayer.value = new VectorLayer({
    name: 'featuredFeatureLayer',
    source: featuredFeatureVectorSource,
    zIndex: 11002,
    style(feature) {
      const featureCount = 1;

      let textStyle;
      let textFillColor;
      let circleStrokeColor;
      if (theme.global.name.value === 'a11yDtsTheme') {
        textFillColor = '#000'
        circleStrokeColor = 'rgba(0, 0, 0, 0.5)'
      } else {
        textFillColor = '#fff'
        circleStrokeColor = 'rgba(255, 255, 255, 0.5)'
      };

      if (featureCount > 1) {
        textStyle = new Text({
          text: featureCount.toString(),
          fill: new Fill({
            color: textFillColor,
          }),
          font: '10px sans-serif',
          textAlign: 'center',
          textBaseline: 'middle',
          offsetX: 0,
          offsetY: 0,
          stroke: new Stroke({
            color: 'rgba(255, 255, 255, 0.5)',
            width: 1
          }),
        });
      };

      let fillColor = mapStore.resultPointColor;
      const style = new Style({
        image: new Circle({
          radius: 11,
          stroke: new Stroke({
            color: circleStrokeColor,
          }),
          fill: new Fill({
            color: fillColor,
          }),
          cursor: 'pointer',
        }),
        text: textStyle
      });

      watch(() => mapStore.resultPointColor, (newColor) => {
        console.log("resultPointerColor watcher called")
        fillColor = newColor;
        if (style.getImage().getFill()) {
          style.getImage().getFill().setColor(fillColor);
        }
        featureLayer.value.changed();
      });

      return style;
    }
  });
  map.value.addLayer(featuredFeatureLayer.value);
}

async function initFeatures() {
  await services.lemmata.getAllLemmata().then((res) => {
    featureModel.value = res.data.allLemmata;
    let features = [];

    for (let i = 0; i < featureModel.value.length; i++) {
      let featureInfo = featureModel.value[i];

      let featureLocationE = featureInfo["utm_coord_e"];
      let featureLocationN = featureInfo["utm_coord_n"];
      let coordinate = [featureLocationE, featureLocationN];

      // Step 1: UTM (EPSG:25832) to lon/lat (EPSG:4326)
      const lonLat = proj4('EPSG:25832', 'EPSG:4326', coordinate);
      // Step 2: lon/lat to Web Mercator (EPSG:3857)
      const webMercatorCoord = fromLonLat(lonLat, 'EPSG:3857');

      // Create the identifying feature location string.
      let featureLocationStr = "" + featureLocationE + "_" + featureLocationN;
      const feature = new Feature({
        lemmaId: featureInfo["id"],
        title: featureInfo["title"],
        icon: getIconByLemmaType(featureInfo["lemma_type"]),
        popupTitle: featureInfo["timeline_title"],
        featureType: 0,
        img: `/img/${ featureInfo["thumbnail_file_name"] }`,
        geometry: new Point(webMercatorCoord),
        featured: featureInfo["featured"],
      });

      // const src = 'EPSG:25832'
      // const dest = 'EPSG:3857'
      // feature.getGeometry().transform(src, dest)

      if (features[featureLocationStr]) {
        features[featureLocationStr].push(feature);
      } else {
        features[featureLocationStr] = [];
        features[featureLocationStr].push(feature);
      };
    }

    let featureVectorSource = featureLayer.value.getSource().getSource();
    let featuredFeatureVectorSource = featuredFeatureLayer.value.getSource();

    // Distribute features with same coordinates in a rhombus pattern.
    for (let existingLocation in features) {
      let amountOfFeaturesAtLoc = features[existingLocation].length;
      let coords = features[existingLocation][0]?.getGeometry()?.getCoordinates();

      let offset = 5;
      let layer = 0;
      let layerElement = 1;

      let lastElementPosX = coords[0];
      let lastElementPosY = coords[1];

      for (let i = 0; i < amountOfFeaturesAtLoc; i++) {
        if (i > 0) {
          if (layerElement <= layer * 8) {
            if (layerElement <= layer * 2) {
              lastElementPosX = lastElementPosX + offset;
              lastElementPosY = lastElementPosY - offset;
            } else if (layerElement <= layer * 2 * 2) {
              lastElementPosX = lastElementPosX - offset;
              lastElementPosY = lastElementPosY - offset;
            } else if (layerElement <= layer * 3 * 2) {
              lastElementPosX = lastElementPosX - offset;
              lastElementPosY = lastElementPosY + offset;
            } else if (layerElement <= layer * 4 * 2) {
              lastElementPosX = lastElementPosX + offset;
              lastElementPosY = lastElementPosY + offset;
            }
            layerElement++;
          } else {
            lastElementPosX = lastElementPosX + offset;
            lastElementPosY = lastElementPosY + offset;
            layer++;
            layerElement = 2;
          }
        }

        features[existingLocation][i].getGeometry().setCoordinates([lastElementPosX, lastElementPosY]);

        if (features[existingLocation][i].get('featured')) {
          featuredFeatureVectorSource.addFeature(features[existingLocation][i]);
        } else {
          featureVectorSource.addFeature(features[existingLocation][i]);
        }
      }
    }

  });
}

function initMap(mapData) {
  // display new map
  const id = mapData["id"];
  const url = mapData["url"];
  const service = mapData["service"]
  let mapLayer = mapStore.mapLayers.find((layer) => layer.get("id") === id);

  // init layer if it is not available already
  if (!mapLayer) {
    // Decide which type of map is to be initialized.
    if (service === "wms") {
      initWMSLayer(mapData);
    } else {
      // Fetch infos about AGS map service.
      axios.post(url + "?f=json").then((response) => {
        const agsInfo = response.data;
        console.info(url, agsInfo);


        if (typeof agsInfo.error != "undefined") {
          console.warn(
            "Eigenschaften des Kartendienstes " +
            url +
            " konnten nicht abgerufen werden.",
            agsInfo.error
          );
          return;
        }

        if (agsInfo.singleFusedMapCache == true) {
          // add cached service
          initCachedLayer(mapData, url, agsInfo);
        } else {
          // add dynamic service
          initDynamicLayer(mapData, url, agsInfo);
        }

        mapLayer = mapStore.mapLayers.find((layer) => layer.get("id") === id);

        mapLayer.setVisible(true);
        mapLayer.setOpacity(1);

      }).catch((error) => {
        console.error("OHOH", url, error);
      })
    }
  } else {
    mapLayer.setVisible(true);
    mapLayer.setOpacity(1);
  }
}

/**
 * Initialize a WMS layer.
 * @param mapData 
 */
function initWMSLayer(mapData) {
  var layer = new ImageLayer({
    title: mapData["timeline_title"],
    id: mapData["id"],
    type: mapData["map_type"],
    description: mapData["map_description"],
    archiveSignature: mapData["archive_signature"],
    angle: mapData["angle"],
    xmin: mapData["x_min"],
    ymin: mapData["y_min"],
    xmax: mapData["x_max"],
    ymax: mapData["y_max"],
    zIndex: 10,
    source: new ImageWMS({
      url: mapData["url"],
      params: { 'LAYERS': mapData["layer"] },
      serverType: 'geoserver',
      crossOrigin: 'anonymous',
      transition: 0,
    }),
  });

  mapStore.mapLayers.push(layer);
  map.value.addLayer(layer);
}

/**
 * Initialize a cached layer.
 * @param mapData 
 * @param url 
 * @param agsInfo 
 */
function initCachedLayer(mapData, url, agsInfo) {
  var layer = new TileLayer({
    title: mapData["timeline_title"],
    id: mapData["id"],
    type: mapData["map_type"],
    description: mapData["map_description"],
    archiveSignature: mapData["archive_signature"],
    angle: mapData["angle"],
    xmin: mapData["x_min"],
    ymin: mapData["y_min"],
    xmax: mapData["x_max"],
    ymax: mapData["y_max"],
    //visible: true,
    opacity: 1,
    zIndex: 100,
    source: new XYZ({
      minZoom: 0,
      tileGrid: new TileGrid({
        origin: [agsInfo.tileInfo.origin.x, agsInfo.tileInfo.origin.y],
        extent: [
          agsInfo.fullExtent.xmin,
          agsInfo.fullExtent.ymin,
          agsInfo.fullExtent.xmax,
          agsInfo.fullExtent.ymax,
        ],
        minZoom: 0,
        resolutions: agsInfo.tileInfo.lods.map((lod) => lod.resolution),
        tileSize: [agsInfo.tileInfo.rows, agsInfo.tileInfo.cols],
      }),
      projection: utmProjection,
      url: url + "/tile/{z}/{y}/{x}",
    }),
  });

  mapStore.mapLayers.push(layer);

  map.value.addLayer(layer);
}

/**
 * Initialize a dynamic layer.
 * @param mapData 
 * @param url 
 * @param agsInfo 
 */
function initDynamicLayer(mapData, url, agsInfo) {
  var layer = new ImageLayer({
    title: mapData["timeline_title"],
    id: mapData["id"],
    type: mapData["map_type"],
    description: mapData["map_description"],
    archiveSignature: mapData["archive_signature"],
    angle: mapData["angle"],
    xmin: mapData["x_min"],
    ymin: mapData["y_min"],
    xmax: mapData["x_max"],
    ymax: mapData["y_max"],
    //visible: true,
    opacity: 1,
    zIndex: 100,
    source: new ImageArcGISRest({
      ratio: 1,
      params: {
        layers: "show:" + mapData["sublayer"]
      },
      url: mapData["url"],
    }),
  });

  mapStore.mapLayers.push(layer);

  map.value.addLayer(layer);
}

async function showArticleFeature() {
  featureLayer.value.setVisible(false);
  featuredFeatureLayer.value.setVisible(false);
  if (netFeatureLayer.value) {
    netFeatureLayer.value.setVisible(false);
  }
  try {
    if (lemmaStore.lemma.locations.length > 1) {
      let netFeatures = [];
      lemmaStore.lemma.locations.forEach((location) => {

        let mainLocation = location["main_location"];
        let featureLocationE = location["utm_coord_e"];
        let featureLocationN = location["utm_coord_n"];
        let coordinate = [featureLocationE, featureLocationN];

        // Step 1: UTM (EPSG:25832) to lon/lat (EPSG:4326)
        const lonLat = proj4('EPSG:25832', 'EPSG:4326', coordinate);
        // Step 2: lon/lat to Web Mercator (EPSG:3857)
        const webMercatorCoord = fromLonLat(lonLat, 'EPSG:3857');

        // Create the identifying feature location string.
        var featureLocationStr = "" + featureLocationE + "_" + featureLocationN;
        let feature = new Feature({
          geometry: new Point(webMercatorCoord),
          featureType: 0,
          title: location["internal_name"],
          img: `/img/${ location["thumbnail_file_name"] }`,
          lemmaId: location["lemma_id"],
          locationId: location["id"],
          locationRelevance: location["location_relevance"],
          locationNr: location["nr_of_location"]
        });

        // if (netFeatures[featureLocationStr]) {
        //   netFeatures[featureLocationStr].push(feature);
        // } else {
        //   netFeatures[featureLocationStr] = [];
        //   netFeatures[featureLocationStr].push(feature);
        // };

        netFeatures.push(feature);
      });

      let netFeatureVectorSource = new VectorSource({
        features: netFeatures
      });
      let netFeatureClusterSource = new Cluster({
        distance: 12,
        source: netFeatureVectorSource
      });

      netFeatureLayer.value = new VectorLayer({
        name: 'netFeatureLayer',
        source: netFeatureClusterSource,
        zIndex: 11000,
        style(feature) {
          const featureCount = feature.get('features').length;

          let textStyle;
          let textFillColor;
          let circleStrokeColor;
          if (theme.global.name.value === 'a11yDtsTheme') {
            textFillColor = '#000';
            circleStrokeColor = 'rgba(0, 0, 0, 0.5)';
          } else {
            textFillColor = '#fff';
            circleStrokeColor = 'rgba(255, 255, 255, 0.5)';
          }

          if (featureCount > 1) {
            textStyle = new Text({
              text: featureCount.toString(),
              fill: new Fill({
                color: textFillColor,
              }),
              font: '10px sans-serif',
              textAlign: 'center',
              textBaseline: 'middle',
              offsetX: 0,
              offsetY: 0,
              stroke: new Stroke({
                color: 'rgba(255, 255, 255, 0.5)',
                width: 1,
              }),
            });
          }

          let fillColor = mapStore.netPointColor;
          const style = new Style({
            image: new Circle({
              radius: 11,
              stroke: new Stroke({
                color: circleStrokeColor,
              }),
              fill: new Fill({
                color: fillColor,
              }),
              cursor: 'pointer',
            }),
            text: textStyle
          });

          watch(() => mapStore.netPointColor, (newColor) => {
            console.log("netPointerColor watcher called")
            fillColor = newColor;
            if (style.getImage().getFill()) {
              style.getImage().getFill().setColor(fillColor);
            }
            netFeatureLayer.value.changed();
          });

          return style;
        },
      });
      netFeatureLayer.value.setVisible(true);
      map.value.addLayer(netFeatureLayer.value);
      closeAllPopups();
      let i = 0;
      do {
        showFeaturePopup(netFeatures[i], 'netFeatureLayer');
        i++;
      } while (netFeatures.length >= i + 1 && i < 5);
    } else {
      featureLayer.value.setVisible(true);
      showFeaturePopup(featureLayer.value.getSource().getFeatures().find((feature) => feature.get('lemmaId') === lemmaStore.lemma.id), 'featureLayer');
    }
  } catch (error) {
    console.error(error);
  }
}

const overlayZIndex = ref(20000);
const popups = ref([]);
const closer = ref(null);

function openPopupOnMapClick() {
  closer.value = document.getElementById('popup-closer');

  map.value.on('click', (e) => {
    map.value.forEachFeatureAtPixel(e.pixel, (feature, layer) => {
      const layerName = layer.get('name');
      const featureLemmaId = layerName === 'featuredFeatureLayer' || layerName === 'resultFeatureLayer'
        ? feature.get('lemmaId')
        : feature.get('features')[0].get('lemmaId');
      const featureTitle = layerName === 'featuredFeatureLayer' || layerName === 'resultFeatureLayer'
        ? feature.get('title')
        : feature.get('features')[0].get('title');
      console.log(`feature click\nlayer: ${ layerName }\nfeature: ${ featureLemmaId } ${ featureTitle }`);
      console.debug('feature', feature);
      console.debug('layer', layer);

      if (layerName === "featureLayer") {
        const features = feature.get('features');
        if (features.length > 1) {
          const extent = boundingExtent(features.map((r) => r.getGeometry().getCoordinates()));
          map.value.getView().fit(extent, { duration: 1000, padding: [250, 250, 250, 250] });
        } else {
          showFeaturePopup(features[0], layerName);
        }
      } else if (layerName === 'featuredFeatureLayer') {
        showFeaturePopup(feature, layerName);
      } else if (layerName === 'resultFeatureLayer') {
        if (features.length > 1) {
          const extent = boundingExtent(features.map((r) => r.getGeometry().getCoordinates()));
          map.value.getView().fit(extent, { duration: 1000, padding: [250, 250, 250, 250] });
        } else {
          showFeaturePopup(features[0], layerName);
        }
      } else if (layerName === 'netFeatureLayer') {
        const features = feature.get('features');
        if (features.length > 1) {
          const extent = boundingExtent(features.map((r) => r.getGeometry().getCoordinates()));
          map.value.getView().fit(extent, { duration: 1000, padding: [250, 250, 250, 250] });
        } else {
          showFeaturePopup(features[0], layerName);
        }
      }

      // return true so only the top most feature is clicked
      return true;
    });
  });
};

function showFeaturePopup(feature, layerName) {
  console.log(`showFeaturePopup\nlayer: ${ layerName }\nfeature: ${ feature.values_.lemmaId } ${ feature.values_.title }`);
  const featureProperties = feature.values_;
  const coordinates = feature.getGeometry().getCoordinates();

  var foundPopup = undefined;

  if (layerName === 'netFeatureLayer') {
    foundPopup = popups.value.find((popup) =>
      popup.feature.lemmaId === featureProperties.lemmaId
      && popup.feature.locationId === featureProperties.locationId
    );
  } else {
    foundPopup = popups.value.find((popup) => popup.feature.lemmaId === featureProperties.lemmaId);
  }

  if (foundPopup) {
    foundPopup.overlay.setPosition(coordinates);
    foundPopup.content.parentElement.style.zIndex = ++overlayZIndex.value;
    return true; // return true only handle the click on the top most feature
  }

  const content = document.createElement('div');
  content.classList.add('ol-popup');
  if (layerName === 'featuredFeatureLayer') {
    content.id = `featured-popup-${ featureProperties.lemmaId }`;
    content.innerHTML = `
    <div class="popup-click">
      <h3 class="heading">
        <div class="title">
          <i class="${ feature.get('icon') } popup-icon"></i>
          ${ featureProperties.popupTitle ? featureProperties.popupTitle : featureProperties.title }
        </div>
        <span class="fa fa-close popup-closer"></span>
      </h3>
      <br>
      <div class="img-container">
        <img class="popup-img" src="${ featureProperties.img }">
      </div>
    </div>`;
  } else if (layerName === 'netFeatureLayer') {
    content.id = `net-popup-${ featureProperties.lemmaId }-${ featureProperties.locationId }`;
    content.innerHTML = `
      <div class="popup-click">
        <h3 class="net-heading">
          <div class="net-title">
            ${ featureProperties.title }
          </div>
          <span class="fa fa-close popup-closer"></span>
        </h3>
        <div class="popup-text-container">
          ${ featureProperties.locationRelevance }
        </div>
      </div>`;
  } else {
    content.id = `popup-${ featureProperties.lemmaId }`;
    content.innerHTML = `
    <div class="popup-click">
      <h3 class="heading">
        <div class="title">
          <i class="${ feature.get('icon') } popup-icon"></i>
          ${ featureProperties.popupTitle ? featureProperties.popupTitle : featureProperties.title }
        </div>
        <span class="fa fa-close popup-closer"></span>
      </h3>
      <br>
      <div class="img-container">
        <img class="popup-img" src="${ featureProperties.img }">
      </div>
    </div>`;
  }

  const popupOverlay = new Overlay({
    element: content,
    positioning: 'top-center',
    stopEvent: true,
    offset: [0, -10],
  });

  popupOverlay.setPosition(coordinates);
  map.value.addOverlay(popupOverlay);
  content.parentElement.style.zIndex = ++overlayZIndex.value;

  const popup = {
    overlay: popupOverlay,
    content: content,
    feature: featureProperties
  };

  popups.value.push(popup);

  const closeButton = content.querySelector('.popup-closer');
  closeButton.addEventListener('click', () => {
    popupOverlay.setPosition(undefined);
    closer.value.blur();
    return false;
  });

  if (layerName !== 'netFeatureLayer') {
    const imgContainerElement = content.querySelector('.img-container');
    imgContainerElement.addEventListener('click', (event) => {
      handleClickedFeature(featureProperties);
    });

    const titleElement = content.querySelector('.title');
    titleElement.addEventListener('click', (event) => {
      handleClickedFeature(featureProperties);
    });
  }
}

function showNetPopup(lemmaId, locationId) {
  closeAllPopups();
  lemmaStore.allLemmata;
  const feature = netFeatureLayer.value.getSource().getFeatures().find((feature) => {
    return feature.values_.lemmaId === lemmaId && feature.values_.locationId === locationId;
  });
}

function closeAllPopups() {
  popups.value.forEach((popup) => {
    popup.overlay.setPosition(undefined);
  });
};

async function handleClickedFeature(feature) {
  await lemmaStore.fetchArticle(feature.lemmaId);
  viewControllerStore.setCurrentView('article');
  showArticleFeature();
}

function showMapSettings() {
  try {
    mapStore.fetchHistoricMapData();
    mapStore.fetchThematicMapData();
    viewControllerStore.setCurrentView('mapSettings');
  } catch (error) {
    console.error(error);
  };
};

function toggleMapPoints(value) {
  featureLayer.value.setVisible(value);
};

/**
 * Logs the current extent of the map to the console when the map is moved. If needed it can be initialized by calling the function in the onMounted hook.
 */
function logExtentOnMapMoveEnd() {
  map.value.on('moveend', () => {
    console.log(map.value.getView().calculateExtent(map.value.getSize()));
  });
};
</script>
<template>
  <div id="map"></div>
  <div
    id="popup"
    class="ol-popup"
  >
    <span
      id="popup-closer"
      class="fa fa-close popup-closer"
    ></span>
    <div id="popup-content"></div>
  </div>
  <div class="text-center accessibility">
    <!-- <A11Y /> -->
    <v-btn
      v-bind="activatorProps"
      variant="text"
      class="iconA11Y"
      width="auto"
      icon="fa fa-low-vision"
      aria-label="Farbschema Barrierefreiheit"
      @click="switchTheme"
    >
    </v-btn>
  </div>
  <v-btn
    class="iconMap"
    icon="fa fa-map"
    variant="text"
    width="auto"
    @click="showMapSettings"
    aria-label="Karteneinstellungen"
  >
  </v-btn>
  <v-btn
    class="iconLocation"
    icon="fas fa-location-arrow"
    variant="text"
    width="auto"
    aria-label="Lokalisierung"
  >
  </v-btn>
  <router-link
    target="_blank"
    :to="{ path: '/Hilfe', query: { a11yTheme: theme.global.name.value === 'a11yDtsTheme' } }"
    aria-label="Hilfeseite aufrufen"
  >
    <v-icon
      class="iconQuestion"
      icon="fa fa-question-circle"
    />
  </router-link>
</template>
<style lang="scss">
#map {
  position: fixed;
  top: 0;
  left: 0;
  height: 100vh;
  width: 100vw;
}

.title {
  display: flex;
  gap: 5px;
  overflow-wrap: anywhere;

  &:hover {
    color: var(--dts-color-font-hover);
  }
}

.net-title {
  display: flex;
  gap: 5px;
}

.ol-popup {
  position: absolute;
  background-color: var(--dts-color-bg);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.2);
  padding: 1px;
  border-radius: 10px;
  border: 1px solid #cccccc;
  bottom: 10px;
  left: -80px;
  min-width: 160px;
}

.ol-popup:after,
.ol-popup:before {
  top: 100%;
  border: solid transparent;
  content: " ";
  height: 0;
  width: 0;
  position: absolute;
  pointer-events: none;
  opacity: 0.5;
}

.ol-popup:after {
  border-top-color: var(--vt-c-white);
  border-width: 10px;
  left: 50%;
  margin-left: -10px;
}

.ol-popup:before {
  border-width: 11px;
  left: 50%;
  margin-left: -11px;
  border-top-color: #999;
}

.popup-closer {
  font-size: 20px;
  color: var(--dts-color-closer);
  grid-area: close;
  text-align: end;
  cursor: pointer;
}

.heading {
  background-color: var(--dts-color-foxbrush);
  color: var(--dts-color-anticipation);
  border-radius: 5px 5px 0 0;
  margin: 0;
  padding: 6px 6px;
  font-size: 12px;
  font-weight: bold;
  cursor: pointer;
  display: grid;
  grid-template-areas:
    "icon heading close";
  flex-direction: row;
  gap: 5px;
}

.net-heading {
  background-color: var(--dts-color-foxbrush);
  color: var(--dts-color-anticipation);
  border-radius: 5px 5px 0 0;
  margin: 0;
  padding: 6px 6px;
  font-size: 12px;
  font-weight: bold;
  display: grid;
  grid-template-areas:
    "icon heading close";
  flex-direction: row;
  gap: 5px;
}

.popup-icon {
  vertical-align: sub;
}

.img-container {
  height: 80px;
  position: relative;
  cursor: pointer;
}

.popup-text-container {
  padding: 3px 6px;
  font-size: 14px;
  background-color: var(--dts-color-bg);
}

.popup-img {
  position: absolute;
  top: 40%;
  left: 50%;
  transform: translate(-50%, -50%);
  max-height: 100%;
  max-width: 100%;
}

// Mixin for style reusability
@mixin icon_style {
  position: fixed;
  left: 16px;
  cursor: pointer;
  color: rgba(var(--v-theme-foxbrush), 0.8);
}

.accessibility {
  @include icon_style();
  top: 6rem;
  z-index: 1000;
}

.v-btn--icon.v-btn--size-default {
  &.iconMap {
    top: 30%;
    @include icon_style();

    @media only screen and (max-width: 780px) {
      font-size: 1.2rem;
    }
  }
}

.v-btn--icon.v-btn--size-default {
  &.iconLocation {
    @include icon_style();
    top: 43%;

    @media only screen and (max-width: 780px) {
      font-size: 1.6rem;
      left: 13px;
    }
  }
}

.iconQuestion {
  @include icon_style();
  top: 38%;

  @media only screen and (max-width: 780px) {
    font-size: 2.3rem;
    left: 13px;
  }
}

.ol-zoom {
  top: 10rem !important;
  left: 1rem !important;

  @media only screen and (max-width: 780px) {
    display: none;
  }
}

@media (min-width: 780px) and (max-width: 2560px) {
  .iconLocation {
    display: none;
  }
}

@media only screen and (max-width: 780px) {
  .v-btn--icon.v-btn--size-default {
    &.iconLocation {
      top: 36.5%;
    }
  }

  .iconQuestion {
    top: 44%;
  }
}
</style>
