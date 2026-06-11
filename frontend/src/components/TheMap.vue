<script setup>
import { syncCssVars, useColors } from '@/composables/useColors.js';
import { useSnackbar } from '@/composables/useSnackbar.js';
import { Map as OlMap, View } from 'ol';
import Feature from 'ol/Feature.js';
import Overlay from 'ol/Overlay.js';
import Attribution from 'ol/control/Attribution';
import { boundingExtent, getWidth } from 'ol/extent.js';
import WMTSCapabilities from 'ol/format/WMTSCapabilities.js';
import Point from 'ol/geom/Point.js';
import ImageLayer from 'ol/layer/Image';
import TileLayer from 'ol/layer/Tile';
import VectorLayer from 'ol/layer/Vector';
import 'ol/ol.css';
import proj4 from 'proj4';
import { register } from 'ol/proj/proj4';
import { addProjection, transform } from 'ol/proj';
import Projection from 'ol/proj/Projection.js';
import Cluster from 'ol/source/Cluster.js';
import ImageArcGISRest from 'ol/source/ImageArcGISRest';
import ImageWMS from 'ol/source/ImageWMS';
import VectorSource from 'ol/source/Vector.js';
import WMTS, { optionsFromCapabilities } from 'ol/source/WMTS.js';
import XYZ from 'ol/source/XYZ';
import TileGrid from 'ol/tilegrid/TileGrid';
import { onMounted, reactive, ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import { useDisplay, useTheme } from 'vuetify';
import services from '../../src/services';
import { useA11y } from '../composables/a11y';
import axios from '../services/axios';
import { getIconByLemmaType } from '../services/getLemmaIconByType';
import { useLemmaStore } from '../store/lemmaStore';
import useMapStore from '../store/mapStore';
import { useSearchQueryStore } from '../store/searchQueryStore';
import { useViewControllerStore } from '../store/viewControllerStore';
import { clearStyleCache, createMarkerStyle, createPointStyle } from '../utils/styleFactory.js';
import A11yStatementButton from './A11yStatementButton.vue';
import HelpButton from './HelpButton.vue';
import PlainLanguageButton from './PlainLanguageButton.vue';
import SignLanguageButton from './SignLanguageButton.vue';

const { switchTheme } = useA11y();
const { showSnackbar } = useSnackbar();
const display = useDisplay();

const { defaultPointCircleFillColor, defaultPointCircleStrokeColor, defaultPointTextColor, defaultPointTextStrokeColor, resultPointCircleFillColor, resultPointCircleStrokeColor, resultPointTextColor, resultPointTextStrokeColor, netPointCircleFillColor, netPointCircleStrokeColor, netPointTextColor, netPointTextStrokeColor } = useColors();

const a11yStatementButtonActive = import.meta.env.VITE_A11Y_STATEMENT;
const plainLanguageButtonActive = import.meta.env.VITE_PLAIN_LANGUAGE;
const signLanguageButtonActive = import.meta.env.VITE_SIGN_LANGUAGE;

const route = useRoute();
const theme = useTheme();

const searchQueryStore = useSearchQueryStore();
const lemmaStore = useLemmaStore();
const mapStore = useMapStore();
const viewControllerStore = useViewControllerStore();

const features = ref([]);
const featureById = ref(new Map());
const activePopovers = ref([]);
const storedExtent = ref(null);
const map = reactive({});
const parser = new WMTSCapabilities();
const featureLayer = ref(null);
const resultFeatureLayer = ref(null);
const topResultFeatureLayer = ref(null);
const netFeatureLayer = ref(null);

const streetCenterMarker = ref(null);
const streetCenterLayer = ref(null);
const locationMarker = ref(null);
const locationLayer = ref(null);

const utmProjection = new Projection({
  code: "EPSG:25832",
  units: "m"
});

proj4.defs('EPSG:25832', '+proj=utm +zone=32 +ellps=GRS80 +units=m +no_defs');
register(proj4);

onMounted(async () => {
  syncCssVars(theme);

  initMap();
  enableWMSHover();
  initBackgroundMap();
  initializeBaseMap();
  openPopupOnMapClick();
  initFeatureLayer();
  initResultFeatureLayer();
  initTopResultFeatureLayer();
  initFeatures().then(() => {
    if (route.query.id || route.params.lemmaLink) {
      closeAllPopups();
      showArticleFeature();
      focusMapOnQueryResults();
    }
  });

  logViewOnMapMoveEnd();
});

watch(() => theme.global.name.value, () => {
  // re-read CSS variables, clear cached styles and request OL redraw
  syncCssVars(theme);
  clearStyleCache();
  featureLayer.value?.changed();
  resultFeatureLayer.value?.changed();
  topResultFeatureLayer.value?.changed();
  netFeatureLayer.value?.changed();
});

watch(() => searchQueryStore.queryResult.artikel, (newQueryResultArtikel, oldQueryResultArtikel) => {
  if (newQueryResultArtikel && newQueryResultArtikel.length > 0) {
    // featureLayer.value.setVisible(true);
    // resultFeatureLayer.value.setVisible(false);
    // initFeatures();
    resetActivePopovers();
    showQueryFeatures().then(() => {
      focusMapOnQueryResults();
    });
  }
}, {
  immediate: true,
  deep: true
});

watch(() => lemmaStore.lemma, (newLemma, oldLemma) => {
  if (newLemma && lemmaStore.ort == null) {
    featureLayer.value.setVisible(true);
    if (netFeatureLayer.value) {
      netFeatureLayer.value.setVisible(false);
    }
    showArticleFeature();
  } else if (lemmaStore.ort != null) {
    showArticleFeature(lemmaStore.ort);
  }
});

watch(() => viewControllerStore.currentView, (currentView, lastView) => {
  if (currentView === 'map') {
    if (mapStore.showPoints) {
      featureLayer.value.setVisible(true);
    }
    if (searchQueryStore.query === '') {
      resultFeatureLayer.value.setVisible(true);
      topResultFeatureLayer.value.setVisible(true);
    }
    if (netFeatureLayer.value && netFeatureLayer.value.getVisible()) {
      netFeatureLayer.value.setVisible(false);
      closeAllPopups();
    }
  } else if (currentView === 'article') {
    if (netFeatureLayer.value) {
      netFeatureLayer.value.setVisible(false);
    }
    if (mapStore.showPoints) {
      featureLayer.value.setVisible(true);
      showArticleFeature();
    }
  } else if (currentView === 'result') {
    // showQueryFeatures();
    if (netFeatureLayer.value?.getVisible()) {
      netFeatureLayer.value.setVisible(false);
      closeAllPopups();
    }
    if (mapStore.showPoints) {
      featureLayer.value.setVisible(true);
      resultFeatureLayer.value.setVisible(true);
      topResultFeatureLayer.value.setVisible(true);
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
  map.value.getLayers().getArray().find((layer) => layer.get("id") === id).setOpacity(1 - newTransparency / 100);
});

watch(() => mapStore.selectedMap, (newSelectedMap, oldSelectedMap) => {
  // hide old map (it can be null if no map was selected yet or if the map was deselected)
  if (oldSelectedMap != null) {
    let oldMapLayer = map.value.getLayers().getArray().find((layer) => layer.get("id") === oldSelectedMap["id"]);
    if (oldMapLayer) {
      oldMapLayer.setVisible(false);
    }
  }

  if (newSelectedMap == null) {
    return;
  }

  initMapLayer(newSelectedMap).then((mapLayer) => {
    if (mapLayer) {
      // set map layer to visible
      mapStore.transparency = 50;
      mapLayer.setOpacity(0.5);
      mapLayer.setZIndex(200);
      mapLayer.setVisible(true);
      // map.value.getView().fit(mapLayer.getExtent(), { duration: 1000, padding: [250, 250, 250, 250] });
    }
  });

  // Check if selected map is in current view extent, if not, center map to selected map
  const view = map.value.getView();
  const extent = view.calculateExtent();

  if (
    (newSelectedMap.x_max != null && newSelectedMap.y_max != null && newSelectedMap.x_min != null && newSelectedMap.y_min != null) &&
    (newSelectedMap.x_max != 0 && newSelectedMap.y_max != 0 && newSelectedMap.x_min != 0 && newSelectedMap.y_min != 0) &&
    (newSelectedMap.x_max < extent[0] || newSelectedMap.y_max < extent[1] || newSelectedMap.x_min > extent[2] || newSelectedMap.y_min > extent[3])
  ) {
    const mapToFit = newSelectedMap;
    const callbackButtonText = display.smAndDown.value ? 'Zur Karte' : 'Zur ausgewählten Karte springen?';
    showSnackbar({
      message: 'Karte außerhalb des Sichtbereichs.',
      timeout: "-1",
      callback: () => fitViewToArea(mapToFit),
      callbackButton: callbackButtonText
    });
  }
});

watch(() => mapStore.selectedCompareMap, (newSelectedCompareMap, oldSelectedCompareMap) => {
  // hide old map (it can be null if no map was selected yet or if the map was deselected)
  if (oldSelectedCompareMap != null) {
    let oldMapLayer = map.value.getLayers().getArray().find((layer) => layer.get("id") === oldSelectedCompareMap["id"]);
    if (oldMapLayer) {
      oldMapLayer.setVisible(false);
    }
  }

  if (newSelectedCompareMap == null) {
    return;
  }

  initMapLayer(newSelectedCompareMap).then((mapLayer) => {
    if (mapLayer) {
      // set map layer to visible
      mapLayer.setOpacity(1);
      mapLayer.setZIndex(100);
      mapLayer.setVisible(true);
      // map.value.getView().fit(mapLayer.getExtent(), { duration: 1000, padding: [250, 250, 250, 250] });
    }
  });
});

watch(() => mapStore.shownStreet, (newShownStreet) => {
  if (!newShownStreet) {
    streetCenterLayer.value && streetCenterLayer.value.setVisible(false);
    return;
  }

  // Check if necessary layer is available
  if (!streetCenterLayer.value) initStreetCenterLayer();
  // Add layer to map if not already added
  if (!map.value.getLayers().getArray().includes(streetCenterLayer.value)) {
    map.value.addLayer(streetCenterLayer.value);
  }

  // Check if necessary marker is available
  if (!streetCenterMarker.value) initStreetCenterMarker();
  // Add marker to layer if not already added
  if (!streetCenterLayer.value.getSource().getFeatures().includes(streetCenterMarker.value)) {
    streetCenterLayer.value.getSource().addFeature(streetCenterMarker.value);
  }
  // Update marker position
  streetCenterMarker.value.getGeometry().setCoordinates([newShownStreet.cx, newShownStreet.cy]);
  streetCenterLayer.value && streetCenterLayer.value.setVisible(true);

  fitViewToArea({
    x_min: newShownStreet.cx - 150,
    y_min: newShownStreet.cy - 150,
    x_max: newShownStreet.cx + 150,
    y_max: newShownStreet.cy + 150,
  });
});

watch(() => featureLayer?.value?.getVisible(), (newVisible) => {
  if (newVisible) {
    showActivePopovers();
    if (storedExtent.value) {
      map.value.getView().fit(storedExtent.value, { duration: 1000 });
    }
    // focusMapOnActivePopovers();
  }
});

watch(() => activePopovers.value, () => {
  console.debug("activePopovers changed:", activePopovers.value);
}, {
  deep: true
});

const focusMapOnQueryResults = () => {
  let allCoordinates = [];

  let resultFeatureVectorSource = resultFeatureLayer.value.getSource().getSource();
  let topResultFeatureVectorSource = topResultFeatureLayer.value.getSource();

  resultFeatureVectorSource.getFeatures().forEach((feature) => {
    allCoordinates.push(feature.getGeometry().getCoordinates());
  });

  topResultFeatureVectorSource.getFeatures().forEach((feature) => {
    allCoordinates.push(feature.getGeometry().getCoordinates());
  });

  if (allCoordinates.length > 0) {
    const extent = boundingExtent(allCoordinates);
    fitViewToArea({
      x_min: extent[0],
      y_min: extent[1],
      x_max: extent[2],
      y_max: extent[3],
    });
  }
};

/**
   * Moves the view of the map to the given area.
   * @param {{xmin: number, ymin: number, xmax: number, ymax: number}} area An object with the fields xmin, ymin, xmax and ymax.
   * @param {number[]} padding An array with the padding values for top, right, bottom and left.
   */
const fitViewToArea = async (area, padding = [280, 100, 120, 100]) => {
  const view = map.value.getView();
  const desktopExtent = [area.x_min, area.y_min, area.x_max + (area.x_max - area.x_min), area.y_max];
  const mobileExtent = [area.x_min, area.y_min, area.x_max, area.y_max];
  const fitOptions = { duration: 1000, padding };
  // Move map to the left screen area.
  if (display.mdAndUp.value) {
    view.fit(desktopExtent, fitOptions);
    // fitViewToLeftScreenHalf(area);
  } else {
    view.fit(mobileExtent, fitOptions);
  }
};

/**
 * This function moves the content in the 'area' to the left half of the screen.
 * This is usually used for the large and medium sized screens when there is a menu opened on the right.
 *
 * @param {object} area an object with the fields xmin, ymin, xmax and ymax
 */
const fitViewToLeftScreenHalf = (area) => {
  const view = map.value.getView();
  var mapExtent = view.calculateExtent();
  var extentWidth = getWidth(mapExtent);
  view.fit([
    area.x_min + extentWidth / 4,
    area.y_min,
    area.x_max + extentWidth / 4,
    area.y_max,
  ], { duration: 1000 });
};

function initStreetCenterLayer() {
  streetCenterLayer.value = new VectorLayer({
    name: 'streetCenterLayer',
    source: new VectorSource({
      features: []
    }),
    zIndex: 12000,
  });
}

function initStreetCenterMarker() {
  streetCenterMarker.value = new Feature({
    name: 'streetCenterMarker',
    type: 'geoMarker',
    geometry: new Point([0, 0])
  });

  streetCenterMarker.value.setStyle(createMarkerStyle({}));
}

function initLocationLayer() {
  locationMarker.value = new Feature({
    name: 'locationMarker',
    type: 'userLocation',
    geometry: new Point([0, 0])
  });
  locationMarker.value.setStyle(createMarkerStyle({ color: '#229922' }));

  locationLayer.value = new VectorLayer({
    name: 'locationLayer',
    source: new VectorSource({ features: [locationMarker.value] }),
    zIndex: 13000,
    visible: false
  });
}

function setUserLocationOnMap(utmCoordinate) {
  if (!locationLayer.value) {
    initLocationLayer();
  }

  if (!map.value.getLayers().getArray().includes(locationLayer.value)) {
    map.value.addLayer(locationLayer.value);
  }

  locationMarker.value.getGeometry().setCoordinates(utmCoordinate);
  locationLayer.value.setVisible(true);
  map.value.getView().animate({ center: utmCoordinate, duration: 800, zoom: Math.max(map.value.getView().getZoom(), 14) });
}

function getUserLocationCoordinate(position) {
  return transform([position.coords.longitude, position.coords.latitude], 'EPSG:4326', 'EPSG:25832');
}

function formatGeoError(error) {
  switch (error.code) {
    case error.PERMISSION_DENIED:
      return 'Standortfreigabe verweigert.';
    case error.POSITION_UNAVAILABLE:
      return 'Standort nicht verfügbar.';
    case error.TIMEOUT:
      return 'Standortanfrage zeitlich abgelaufen.';
    default:
      return 'Standort konnte nicht ermittelt werden.';
  }
}

function locateUser() {
  if (!navigator.geolocation) {
    showSnackbar({ message: 'Geolocation wird von diesem Browser nicht unterstützt.' });
    return;
  }

  navigator.geolocation.getCurrentPosition(
    (position) => {
      try {
        const utmCoordinate = getUserLocationCoordinate(position);
        setUserLocationOnMap(utmCoordinate);
        showSnackbar({ message: 'Standort auf der Karte angezeigt.', timeout: 3000 });
      } catch (error) {
        console.error('Fehler bei der Standortumwandlung', error);
        showSnackbar({ message: 'Standort konnte nicht auf der Karte angezeigt werden.' });
      }
    },
    (error) => {
      showSnackbar({ message: formatGeoError(error) });
    },
    { enableHighAccuracy: true, timeout: 10000, maximumAge: 0 }
  );
}

function initMap() {
  addProjection(utmProjection);

  map.value = new OlMap({
    target: 'map',
    layers: [],
    view: new View({
      projection: utmProjection,
      center: [Number(import.meta.env.VITE_START_CENTER_UTM_E), Number(import.meta.env.VITE_START_CENTER_UTM_N)],
      // limits free movement of view to area
      // extent: [663806.7806250211, 5632124.8500854345, 702834.9050611877, 5654644.526484233], 
      zoom: Number(import.meta.env.VITE_START_ZOOM) || 13.5,
      minZoom: Number(import.meta.env.VITE_MIN_ZOOM) || 12,
      maxZoom: Number(import.meta.env.VITE_MAX_ZOOM) || 20,
      // resolutions: [
      //   111.99999999999999, 55.99999999999999, 27.999999999999996,
      //   13.999999999999998, 6.999999999999999, 2.8, 1.4, 0.7, 0.28
      // ],
      pixelRatio: 1,
      loadTilesWhileAnimating: true,
      loadTilesWhileInteracting: true,
    })
  });
  map.value.updateSize();

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

/**
 * Enable hover (GetFeatureInfo) for WMS Image layers.
 * This will request the WMS GetFeatureInfo for the top-most visible ImageWMS layer
 * and show a small overlay tooltip. The WMS server must support GetFeatureInfo and CORS.
 */
function enableWMSHover() {
  const infoElement = document.createElement('div');
  infoElement.className = 'ol-wms-info';
  infoElement.style.pointerEvents = 'none';

  const infoOverlay = new Overlay({ element: infoElement, offset: [12, 0], positioning: 'center-left' });
  map.value.addOverlay(infoOverlay);

  let hoverTimer = null;

  function escapeHtml(str) {
    if (!str) return '';
    return String(str).replace(/[&<>"']/g, (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' })[c]);
  }

  map.value.on('pointermove', (evt) => {
    if (evt.dragging) {
      infoOverlay.setPosition(undefined);
      return;
    }

    const coordinate = evt.coordinate;

    if (hoverTimer) clearTimeout(hoverTimer);
    hoverTimer = setTimeout(async () => {
      // find top-most visible ImageWMS layer
      const layers = map.value.getLayers().getArray().slice().reverse(); // top-first
      let wmsLayer = null;
      for (const l of layers) {
        const src = l.getSource && l.getSource();
        if (src && src instanceof ImageWMS && l.getVisible() && l.get('name') !== 'baseMapLayer' && l.get('name') !== 'backgroundMapLayer') {
          wmsLayer = l;
          break;
        }
      }

      if (!wmsLayer) {
        infoOverlay.setPosition(undefined);
        return;
      }

      const source = wmsLayer.getSource();
      const view = map.value.getView();
      const resolution = view.getResolution();
      const projection = view.getProjection();

      const url = source.getFeatureInfoUrl(coordinate, resolution, projection, { 'INFO_FORMAT': 'application/json', 'FEATURE_COUNT': 5 });
      if (!url) {
        infoOverlay.setPosition(undefined);
        return;
      }

      try {
        const resp = await fetch(url, { mode: 'cors' });
        if (!resp.ok) throw new Error('GetFeatureInfo failed');

        let infoHtml = '';

        const json = await resp.json();
        if (json.features && json.features.length > 0) {
          const featuresHtml = json.features.map(f => {
            const props = f.properties || {};
            const name = props.NAME;
            const datum = props.DATUM;
            const beschreibung = props.BESCHREIBUNG;
            const adresse = props.ADRESSE;
            return `<div>${ name ? escapeHtml(name) + '<br>' : '' }${ datum ? escapeHtml(datum) + '<br>' : '' }${ beschreibung ? escapeHtml(beschreibung) + '<br>' : '' }${ adresse ? escapeHtml(adresse) : '' }</div>`;
          }).join('<hr/>');

          const layerTitle = wmsLayer.get('title') || wmsLayer.get('id') || 'Layer';

          infoHtml = `<div>
              <div class="ol-wms-info-title">
                ${ escapeHtml(layerTitle) }
              </div>
              <div class="ol-wms-info-content">${ featuresHtml }</div>
            </div>`;
        }

        if (infoHtml && infoHtml.trim() !== '') {
          infoElement.innerHTML = infoHtml;
          infoOverlay.setPosition(coordinate);
        } else {
          infoOverlay.setPosition(undefined);
        }
      } catch (err) {
        // silently fail and hide overlay
        console.debug('GetFeatureInfo failed', err);
        infoOverlay.setPosition(undefined);
      }
    }, 150); // debounce
  });
}

function initBackgroundMap() {
  fetch('https://sgx.geodatenzentrum.de/wmts_topplus_open/1.0.0/WMTSCapabilities.xml')
    .then(function (response) { return response.text(); }).then(function (text) {
      const cap = parser.read(text);
      const options = optionsFromCapabilities(cap, {
        layer: 'web_grau',
        matrixTileSet: 'EU_EPSG_25832_TOPPLUS',
        projection: 'EPSG:25832',
        attributions: [new Attribution({
          html: `Kartendarstellung und Präsentationsgraphiken: © Bundesamt für Kartographie und Geodäsie (2022),
            Datenquellen: https://sgx.geodatenzentrum.de/web_public/gdz/datenquellen/Datenquellen_TopPlusOpen.html`,
        })]
      });

      const layer = new TileLayer({
        zIndex: 0,
        opacity: 0.5,
        source: new WMTS(options),
        name: 'backgroundMapLayer',
      });
      map.value.addLayer(layer);
    }).catch(reason => console.error(reason));
};

async function initializeBaseMap() {
  const baseMapResponse = await services.maps.getBaseMap();
  const baseMap = baseMapResponse.data.maps[0];
  if (true) {
    fetch('https://kartenportal.jena.de/mapproxy/wmts/1.0.0/WMTSCapabilities.xml')
      .then((response) => (response.text())).then(function (text) {
        const cap = parser.read(text);
        const options = optionsFromCapabilities(cap, {
          layer: 'Stadtplan',
          matrixTileSet: 'utm32n_th',
          projection: 'EPSG:25832',
          attributions: [new Attribution({ html: 'Basiskarte © Stadt Jena, GDI-Th dl-de/by-2-0', })]
        });

        const layer = new TileLayer({
          zIndex: 1,
          opacity: 1,
          source: new WMTS(options),
          name: 'baseMapLayer',
        });
        map.value.addLayer(layer);
      }).catch(reason => console.error(reason));
  } else {
    const layer = new ImageLayer({
      extent: [664316, 5634538, 699434, 5652719],
      zIndex: 1,
      source: new ImageWMS({
        url: 'https://map.jena.de/mapproxy/wms',
        params: {
          'LAYERS': 'stadtplan'
        },
        ratio: 1
      }),
      name: 'baseMapLayer',
    });
    map.value.addLayer(layer);
  }
}

function initFeatureLayer() {
  let featureVectorSource = new VectorSource({
    features: []
  });

  let featureClusterSource = new Cluster({
    distance: Number(import.meta.env.VITE_CLUSTER_DISTANCE) || 12,
    source: featureVectorSource
  });

  featureLayer.value = new VectorLayer({
    name: 'featureLayer',
    source: featureClusterSource,
    zIndex: 11001,
    style(feature) {
      const featuresArr = feature.get('features');
      const featureCount = featuresArr ? featuresArr.length : 1;
      const label = featureCount > 1 ? String(featureCount) : null;

      return createPointStyle({
        fillColor: defaultPointCircleFillColor.value,
        strokeColor: defaultPointCircleStrokeColor.value,
        textColor: defaultPointTextColor.value,
        textStrokeColor: defaultPointTextStrokeColor.value,
        text: label,
        radius: Number(import.meta.env.VITE_FEATURE_RADIUS) || 11
      });
    }
  });

  if (!map.value) {
    initMap();
  }
  map.value.addLayer(featureLayer.value);
}

function initResultFeatureLayer() {
  let resultFeatureVectorSource = new VectorSource({
    features: []
  });

  let resultFeatureClusterSource = new Cluster({
    distance: Number(import.meta.env.VITE_CLUSTER_DISTANCE) || 12,
    source: resultFeatureVectorSource
  });

  resultFeatureLayer.value = new VectorLayer({
    name: 'resultFeatureLayer',
    source: resultFeatureClusterSource,
    zIndex: 11002,
    style(feature) {
      const featuresArr = feature.get('features');
      const featureCount = featuresArr ? featuresArr.length : 1;
      const label = featureCount > 1 ? String(featureCount) : null;

      return createPointStyle({
        fillColor: resultPointCircleFillColor.value,
        strokeColor: resultPointCircleStrokeColor.value,
        textColor: resultPointTextColor.value,
        textStrokeColor: resultPointTextStrokeColor.value,
        text: label,
        radius: Number(import.meta.env.VITE_RESULT_FEATURE_RADIUS) || 11
      });
    }
  });
  map.value.addLayer(resultFeatureLayer.value);
}

function initTopResultFeatureLayer() {
  let topResultFeatureVectorSource = new VectorSource({
    features: []
  });

  topResultFeatureLayer.value = new VectorLayer({
    name: 'topResultFeatureLayer',
    source: topResultFeatureVectorSource,
    zIndex: 11003,
    style(feature) {
      // top result features are single features (no cluster)
      const label = null;
      return createPointStyle({
        fillColor: resultPointCircleFillColor.value,
        strokeColor: resultPointCircleStrokeColor.value,
        textColor: resultPointTextColor.value,
        textStrokeColor: resultPointTextStrokeColor.value,
        text: label,
        radius: Number(import.meta.env.VITE_TOP_RESULT_FEATURE_RADIUS) || 13
      });
    }
  });
  map.value.addLayer(topResultFeatureLayer.value);
}

async function initFeatures() {
  console.debug("initFeatures called");
  await services.lemma.getAllLemmata().then((res) => {
    const allLemmata = res.data.allLemmata;
    const featuresCoordinatesMap = new Map();

    for (let i = allLemmata.length - 1; i >= 0; i--) {
      const featureInfo = allLemmata[i];
      const coordinate = [featureInfo["utm_coord_e"], featureInfo["utm_coord_n"]];
      const locationKey = `${ coordinate[0] }_${ coordinate[1] }`;

      const feature = new Feature({
        lemmaId: featureInfo["id"],
        title: featureInfo["title"],
        icon: getIconByLemmaType(featureInfo["lemma_type"]),
        popupTitle: featureInfo["timeline_title"],
        featureType: 0,
        img: `/img/${ featureInfo["thumbnail_file_name"] }`,
        geometry: new Point(coordinate),
        featured: featureInfo["featured"],
      });

      const group = featuresCoordinatesMap.get(locationKey);
      if (group) {
        group.push(feature);
      } else {
        featuresCoordinatesMap.set(locationKey, [feature]);
      }
    }

    const featureVectorSource = featureLayer.value.getSource().getSource();
    const resultFeatureVectorSource = resultFeatureLayer.value.getSource().getSource();
    const topResultFeatureVectorSource = topResultFeatureLayer.value.getSource();

    const allFeatures = [];
    const normalFeatures = [];
    const resultFeatures = [];
    const topResultFeatures = [];
    let topResultCount = 0;

    for (const featureGroup of featuresCoordinatesMap.values()) {
      const coords = featureGroup[0]?.getGeometry()?.getCoordinates();
      let offset = 5;
      let layer = 0;
      let layerElement = 1;

      let lastElementPosX = coords[0];
      let lastElementPosY = coords[1];

      for (let i = 0; i < featureGroup.length; i++) {
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

        // Set the coordinates of the feature to the new position.
        featureGroup[i].getGeometry().setCoordinates([lastElementPosX, lastElementPosY]);
        // Add the feature to the features array.
        allFeatures.push(featureGroup[i]);



        if (featureGroup[i].get('featured')) {
          if (topResultCount < 5) {
            // Add the first 5 featured results to the top result layer.
            topResultCount++;
            topResultFeatures.push(featureGroup[i]);
          } else {
            // If the top result layer is full, add to the result layer.
            resultFeatures.push(featureGroup[i]);
          }
        } else {
          // If the feature is not featured, add it to the normal feature layer.
          normalFeatures.push(featureGroup[i]);
        }
      }
    }

    featureVectorSource.addFeatures(normalFeatures);
    resultFeatureVectorSource.addFeatures(resultFeatures);
    topResultFeatureVectorSource.addFeatures(topResultFeatures);

    for (const feature of topResultFeatures) {
      showFeaturePopup(feature, 'topResultFeatureLayer');
    }

    features.value = allFeatures;
    featureById.value = new Map(allFeatures.map((feature) => [feature.get('lemmaId'), feature]));
  });
}

async function showQueryFeatures() {
  console.debug("showQueryFeatures called");
  if (!featureLayer.value) {
    initFeatureLayer();
  }
  let featureVectorSource = featureLayer.value.getSource().getSource();
  if (!resultFeatureLayer.value) {
    initResultFeatureLayer();
  }
  let resultFeatureVectorSource = resultFeatureLayer.value.getSource().getSource();
  if (!topResultFeatureLayer.value) {
    initTopResultFeatureLayer();
  }
  let topResultFeatureVectorSource = topResultFeatureLayer.value.getSource();

  closeAllPopups();
  featureVectorSource.clear();
  resultFeatureVectorSource.clear();
  topResultFeatureVectorSource.clear();

  const queryResults = searchQueryStore.queryResult.artikel || [];
  const top5QueryResult = queryResults.slice(0, 5);
  const resultQueryIds = new Set(queryResults.slice(5).map((artikel) => artikel.id));
  const top5Ids = new Set(top5QueryResult.map((artikel) => artikel.id));

  const normalFeatures = [];
  const resultFeatures = [];
  const topResultFeatures = [];

  for (const feature of features.value) {
    const lemmaId = feature.get('lemmaId');
    if (top5Ids.has(lemmaId)) {
      topResultFeatures.push(feature);
    } else if (resultQueryIds.has(lemmaId)) {
      resultFeatures.push(feature);
    } else {
      normalFeatures.push(feature);
    }
  }

  featureVectorSource.addFeatures(normalFeatures);
  resultFeatureVectorSource.addFeatures(resultFeatures);

  for (let i = top5QueryResult.length - 1; i >= 0; i--) {
    const artikel = top5QueryResult[i];
    const feature = featureById.value.get(artikel.id);
    if (feature) {
      topResultFeatureVectorSource.addFeature(feature);
      showFeaturePopup(feature, 'topResultFeatureLayer');
    }
  }

  // featureLayer.value.changed();
  // resultFeatureLayer.value.changed();
}

function resetActivePopovers() {
  activePopovers.value = [];
}

function showActivePopovers() {
  activePopovers.value.forEach((activeElement) => {
    showFeaturePopup(activeElement.feature, activeElement.layer);
  });
}

function focusMapOnActivePopovers() {
  let allCoordinates = [];

  activePopovers.value.forEach((activeElement) => {
    allCoordinates.push(activeElement.feature.getGeometry().getCoordinates());
  });

  if (allCoordinates.length > 0) {
    const extent = boundingExtent(allCoordinates);
    fitViewToArea({
      x_min: extent[0],
      y_min: extent[1],
      x_max: extent[2],
      y_max: extent[3],
    });
  }
};

async function initMapLayer(mapData) {
  // display new map
  const id = mapData["id"];
  const url = mapData["url"];
  const service = mapData["service"];
  let mapLayer = map.value.getLayers().getArray().find((layer) => layer.get("id") === id);

  // init layer if it is not available already
  if (!mapLayer) {
    // Decide which type of map is to be initialized.
    if (service && service.toLowerCase() === "wms") {
      initWMSLayer(mapData);
      mapLayer = map.value.getLayers().getArray().find((layer) => layer.get("id") === id);
    } else {
      // Fetch infos about AGS map service.
      await axios.post(url + "?f=json").then((response) => {
        const agsInfo = response.data;

        if (typeof agsInfo.error != "undefined") {
          console.warn(`Eigenschaften des Kartendienstes ${ url } konnten nicht abgerufen werden.`, agsInfo.error);
          return;
        }

        if (agsInfo.singleFusedMapCache == true) {
          // add cached service
          initCachedLayer(mapData, url, agsInfo);
        } else {
          // add dynamic service
          initDynamicLayer(mapData, url, agsInfo);
        }

        mapLayer = map.value.getLayers().getArray().find((layer) => layer.get("id") === id);

      }).catch((error) => {
        console.error("OHOH", url, error);
      })
    }
  }

  return mapLayer;
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
    opacity: 0.5,
    source: new ImageWMS({
      url: mapData["url"],
      params: { 'LAYERS': mapData["layer"] },
      serverType: 'geoserver',
      crossOrigin: 'anonymous',
      transition: 0,
    }),
  });

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
    opacity: 0.5,
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
    opacity: 0.5,
    zIndex: 100,
    source: new ImageArcGISRest({
      ratio: 1,
      params: {
        layers: "show:" + mapData["sublayer"]
      },
      url: mapData["url"],
    }),
  });

  map.value.addLayer(layer);
}

async function showArticleFeature(ortId = null) {
  featureLayer.value.setVisible(false);
  resultFeatureLayer.value.setVisible(false);
  topResultFeatureLayer.value.setVisible(false);
  if (netFeatureLayer.value) {
    netFeatureLayer.value.setVisible(false);
  }
  try {
    if (lemmaStore.lemma.locations.length > 1) {
      storedExtent.value = map.value.getView().calculateExtent();
      let netFeatures = [];
      let netXMin = Infinity;
      let netYMin = Infinity;
      let netXMax = -Infinity;
      let netYMax = -Infinity;
      lemmaStore.lemma.locations.forEach((location) => {

        let mainLocation = location["main_location"];
        let featureLocationE = location["utm_coord_e"];
        let featureLocationN = location["utm_coord_n"];
        let coordinate = [featureLocationE, featureLocationN];

        // Update net extent
        if (featureLocationE < netXMin) netXMin = featureLocationE;
        if (featureLocationN < netYMin) netYMin = featureLocationN;
        if (featureLocationE > netXMax) netXMax = featureLocationE;
        if (featureLocationN > netYMax) netYMax = featureLocationN;

        // Create the identifying feature location string.
        var featureLocationStr = "" + featureLocationE + "_" + featureLocationN;
        let feature = new Feature({
          geometry: new Point(coordinate),
          featureType: 0,
          title: location["internal_name"],
          img: `/img/${ location["thumbnail_file_name"] }`,
          lemmaId: location["lemma_id"],
          locationId: location["id"],
          locationDateLabel: location["location_date_label"],
          locationRelevance: location["location_relevance"],
          locationNr: location["nr_of_location"]
        });

        netFeatures.push(feature);
      });

      let netFeatureVectorSource = new VectorSource({
        features: netFeatures
      });
      let netFeatureClusterSource = new Cluster({
        distance: Number(import.meta.env.VITE_CLUSTER_DISTANCE) || 12,
        source: netFeatureVectorSource
      });

      netFeatureLayer.value = new VectorLayer({
        name: 'netFeatureLayer',
        source: netFeatureClusterSource,
        zIndex: 11000,
        style(feature) {
          const featuresArr = feature.get('features');
          const featureCount = featuresArr ? featuresArr.length : 1;
          const label = featureCount > 1 ? String(featureCount) : null;

          return createPointStyle({
            fillColor: netPointCircleFillColor.value,
            strokeColor: netPointCircleStrokeColor.value,
            textColor: netPointTextColor.value,
            textStrokeColor: netPointTextStrokeColor.value,
            text: label,
            radius: Number(import.meta.env.VITE_NET_FEATURE_RADIUS) || 11
          });
        },
      });
      netFeatureLayer.value.setVisible(true);
      map.value.addLayer(netFeatureLayer.value);
      closeAllPopups();
      fitViewToArea({
        x_min: netXMin,
        y_min: netYMin,
        x_max: netXMax,
        y_max: netYMax,
      });
      if (ortId) {
        const netFeature = netFeatures.find((feature) => feature.get('locationId') === ortId);
        if (netFeature) {
          showFeaturePopup(netFeature, 'netFeatureLayer');
        }
      } else if (netFeatures.length <= 7) {
        let i = 0;
        do {
          showFeaturePopup(netFeatures[i], 'netFeatureLayer');
          i++;
        } while (netFeatures.length >= i + 1 && i < 7);
      }
    } else {
      topResultFeatureLayer.value.setVisible(true);
      resultFeatureLayer.value.setVisible(true);
      featureLayer.value.setVisible(true);
      showFeaturePopup(featureById.value.get(lemmaStore.lemma.version[0]['lemma_id']), 'featureLayer');
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
      const featureLemmaId = layerName === 'topResultFeatureLayer'
        ? feature.get('lemmaId')
        : feature.get('features')[0].get('lemmaId');
      const featureTitle = layerName === 'topResultFeatureLayer'
        ? feature.get('title')
        : feature.get('features')[0].get('title');
      console.log(`feature click\nlayer: ${ layerName }\nfeature: ${ featureLemmaId } ${ featureTitle }`);
      console.debug('feature', feature);
      console.debug('layer', layer);

      if (layerName === 'topResultFeatureLayer') {
        showFeaturePopup(feature, layerName);
      } else if (layerName === 'resultFeatureLayer') {
        const features = feature.get('features');
        if (features.length > 1) {
          const extent = boundingExtent(features.map((r) => r.getGeometry().getCoordinates()));
          map.value.getView().fit(extent, { duration: 1000, padding: [250, 250, 250, 250] });
        } else {
          showFeaturePopup(features[0], layerName);
        }
      } else if (layerName === "featureLayer") {
        const features = feature.get('features');
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
  if (!feature) return;
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
    if (layerName !== 'netFeatureLayer' && !activePopovers.value.some((activeElement) => activeElement.feature.get('lemmaId') === featureProperties.lemmaId)) {
      activePopovers.value.push({ feature: feature, layer: layerName });
    }
    foundPopup.content.parentElement.style.zIndex = ++overlayZIndex.value;
    return true; // return true only handle the click on the top most feature
  }

  const content = document.createElement('div');
  content.classList.add('ol-popup');
  if (layerName === 'topResultFeatureLayer' || layerName === 'resultFeatureLayer') {
    content.id = `featured-popup-${ featureProperties.lemmaId }`;
    content.innerHTML = `
    <div class="popup-click">
      <h3 class="heading">
        <div class="title dts-text">
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
          <div class="net-title dts-text">
            ${ featureProperties.title }
          </div>
          <span class="fa fa-close popup-closer"></span>
        </h3>
        <div class="popup-text-container dts-text">
          ${ featureProperties.locationRelevance } ${ featureProperties.locationDateLabel != null ? '(' + featureProperties.locationDateLabel + ')' : '' }
        </div>
      </div>`;
  } else {
    content.id = `popup-${ featureProperties.lemmaId }`;
    content.innerHTML = `
    <div class="popup-click">
      <h3 class="heading">
        <div class="title dts-text">
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
    activePopovers.value.splice(activePopovers.value.findIndex((activeElement) =>
      activeElement.feature.get('lemmaId') === featureProperties.lemmaId
    ), 1);
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

  // Keep track of active popovers (non-netFeatureLayer)
  if (layerName !== 'netFeatureLayer' && !activePopovers.value.some((activeElement) => activeElement.feature.get('lemmaId') === featureProperties.lemmaId)) {
    activePopovers.value.push({ feature: feature, layer: layerName });
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
  try {
    popups.value.forEach((popup) => {
      popup.overlay.setPosition(undefined);
    });
  } catch (ReferenceError) {
    console.debug("No popups to close.");
  }
};

async function handleClickedFeature(feature) {
  await lemmaStore.fetchLemma(feature.lemmaId);
  viewControllerStore.setCurrentView('article');
}

function showMapSettings() {
  try {
    viewControllerStore.setCurrentView('mapSettings');
  } catch (error) {
    console.error(error);
  };
};

function toggleMapPoints(value) {
  featureLayer.value.setVisible(value);
  if (resultFeatureLayer.value) {
    resultFeatureLayer.value.setVisible(value);
  }
  if (topResultFeatureLayer.value) {
    topResultFeatureLayer.value.setVisible(value);
  }
  if (netFeatureLayer.value) {
    netFeatureLayer.value.setVisible(value);
  }
  closeAllPopups();
};

/**
 * Logs the current extent of the map to the console when the map is moved. If needed it can be initialized by calling the function in the onMounted hook.
 */
function logViewOnMapMoveEnd() {
  map.value.on('moveend', () => {
    console.debug('Info', { extent: map.value.getView().calculateExtent(map.value.getSize()), zoom: map.value.getView().getZoom(), center: map.value.getView().getCenter() });
  });
};
</script>

<template>
  <div id="map"></div>
  <div class="text-center accessibility">
    <v-btn
      class="iconA11Y"
      variant="text"
      width="auto"
      icon="fa fa-low-vision"
      title="Farbschema Barrierefreiheit"
      aria-label="Farbschema Barrierefreiheit"
      @click="switchTheme"
    >
    </v-btn>
  </div>
  <v-btn
    class="iconMap"
    variant="text"
    width="auto"
    icon="fa fa-map"
    title="Karteneinstellungen"
    aria-label="Karteneinstellungen"
    @click="showMapSettings"
  >
  </v-btn>
  <v-btn
    class="iconLocation"
    icon="fas fa-location-arrow"
    variant="text"
    width="auto"
    title="Lokalisierung"
    aria-label="Lokalisierung"
    @click="locateUser"
  >
  </v-btn>
  <HelpButton />
  <A11yStatementButton v-if="a11yStatementButtonActive" />
  <PlainLanguageButton v-if="plainLanguageButtonActive" />
  <SignLanguageButton v-if="signLanguageButtonActive" />
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
  hyphens: auto;

  &:hover {
    color: rgba(var(--v-theme-font-hover), 0.5);
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

.ol-wms-info {
  position: absolute;
  background: var(--dts-color-bg);
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 12px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.15);
  max-width: 280px;

  .ol-wms-info-title {
    background-color: rgba(var(--v-theme-primary), 0.8);
    color: rgb(var(--v-theme-white));
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

  .ol-wms-info-content {
    max-height: 200px;
    overflow-y: auto;
    padding: 3px 6px;
    font-size: 14px;
    background-color: var(--dts-color-bg);
  }
}

.ol-popup:after {
  border-top-color: white;
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
  color: rgba(var(--v-theme-closer), 0.4);
  grid-area: close;
  text-align: end;
  cursor: pointer;
}

.heading {
  background-color: rgba(var(--v-theme-primary), 0.8);
  color: rgb(var(--v-theme-white));
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
  background-color: rgba(var(--v-theme-primary), 0.8);
  color: rgb(var(--v-theme-white));
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
  left: 1rem;
  cursor: pointer;
  color: rgba(var(--v-theme-primary), 0.8);
}

.accessibility {
  @include icon_style();
  top: 6rem;
}

.ol-zoom {
  @include icon_style();
  top: 10rem;

  @media only screen and (max-width: 780px) {
    display: none;
  }
}

.ol-rotate {
  @include icon_style();
  top: 20rem;
  font-size: 1.2em;
  display: inline-block;
  width: min-content;

  @media only screen and (max-width: 780px) {
    top: 15rem;
    font-size: 1em;
  }
}

.v-btn--icon.v-btn--size-default {
  &.iconMap {
    @include icon_style();
    top: 15rem;

    @media only screen and (max-width: 780px) {
      top: 9rem;
      font-size: 1rem;
    }
  }

  &.iconLocation {
    @include icon_style();
    top: 20rem;

    @media only screen and (max-width: 780px) {
      top: 12rem;
      font-size: 1.1rem;
    }

    /* hide on medium/large screens */
    @media (min-width: 781px) {
      display: none;
    }
  }
}

</style>
