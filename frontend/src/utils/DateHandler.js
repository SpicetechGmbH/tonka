export default function getDateString(date) {
  if(!date) return null;
  const options = { year: 'numeric', month: '2-digit', day: '2-digit' };
  return new Date(date).toLocaleDateString('de-DE', options);
}
