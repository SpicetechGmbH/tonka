import { Circle, Fill, Stroke, Style, Text } from 'ol/style.js';
import Icon from 'ol/style/Icon';
import { faMapMarkerAlt } from '@fortawesome/free-solid-svg-icons/faMapMarkerAlt';

const styleCache = new Map();

export function createPointStyle({ fillColor, strokeColor, textColor, textStrokeColor, text = null, radius = 11 }) {
  const key = [fillColor, strokeColor, textColor, textStrokeColor, text, radius].join('|');
  if (styleCache.has(key)) return styleCache.get(key);

  const image = new Circle({
    radius,
    fill: new Fill({ color: fillColor }),
    stroke: new Stroke({ color: strokeColor }),
  });

  let textStyle = null;
  if (text) {
    textStyle = new Text({
      text: String(text),
      fill: new Fill({ color: textColor || '#fff' }),
      stroke: new Stroke({ color: textStrokeColor || 'rgba(0,0,0,0.5)', width: 1 }),
      font: '10px sans-serif',
      textAlign: 'center',
      textBaseline: 'middle',
    });
  }

  const style = new Style({ image, text: textStyle });
  styleCache.set(key, style);
  return style;
}

export function clearStyleCache() {
  styleCache.clear();
}

/**
 * createMarkerStyle - use FontAwesome svg path as an Icon-style marker
 * - color: fill color for the SVG path
 * - size: desired pixel size (height) of the rendered icon
 * - iconDef: optional FontAwesome icon definition (defaults to faMapMarkerAlt)
 */
export function createMarkerStyle({
  color = '#337ab7',
  size = 32,
  iconDef = faMapMarkerAlt,
  anchor = [0.5, 1] // anchor so marker base sits on coordinate
} = {}) {
  const key = ['marker', color, size, iconDef.iconName, anchor.join(',')].join('|');
  if (styleCache.has(key)) return styleCache.get(key);

  // iconDef.icon is [width, height, ligatures, unicode, svgPathData]
  const [width, height] = iconDef.icon;
  const path = iconDef.icon[4];

  // include explicit width/height and preserveAspectRatio so the SVG rasterizes cleanly
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="${width}" height="${height}" viewBox="0 0 ${width} ${height}" preserveAspectRatio="xMidYMid meet"><path fill="${color}" d="${path}"/></svg>`;
  const src = 'data:image/svg+xml;utf8,' + encodeURIComponent(svg);

  // scale so the rendered icon height ~ size px (use height for consistent vertical sizing)
  const scale = size / Math.max(width, height);

  const icon = new Icon({
    src,
    scale,
    anchor,
    anchorXUnits: 'fraction',
    anchorYUnits: 'fraction',
    imgSize: [width, height]
  });

  const style = new Style({ image: icon });
  styleCache.set(key, style);
  return style;
}
