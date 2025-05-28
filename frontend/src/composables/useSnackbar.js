import { ref } from 'vue';

export function useSnackbar() {
  const snackbar = ref({
    show: false,
    message: '',
    color: ''
  });

  function showSnackbar(message, color = 'var(--dts-color-foxbrush)') {
    snackbar.value.message = message;
    snackbar.value.color = color;
    snackbar.value.show = true;
  }

  function hideSnackbar() {
    snackbar.value.show = false;
  }

  return { snackbar, showSnackbar, hideSnackbar };
}
