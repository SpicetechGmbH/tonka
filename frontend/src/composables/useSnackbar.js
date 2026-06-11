import { reactive } from 'vue';

const snackbar = reactive({
  show: false,
  message: '',
  color: '',
  callback: null,
  callbackButton: null,
});

function showSnackbar({ message, color = 'rgba(var(--v-theme-primary), 0.8)', timeout = 6000, callback = null, callbackButton = null }) {
  snackbar.message = message;
  snackbar.color = color;
  snackbar.timeout = timeout;
  snackbar.show = true;
  snackbar.callback = callback;
  snackbar.callbackButton = callbackButton;
}

function hideSnackbar() {
  snackbar.show = false;
  snackbar.callback = null;
  snackbar.callbackButton = null;
}

export function useSnackbar() {
  return { snackbar, showSnackbar, hideSnackbar };
}
