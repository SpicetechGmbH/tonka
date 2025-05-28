import { defineStore } from "pinia";
import { ref } from "vue";

export const useViewControllerStore = defineStore("viewController", () => {
  // Views: map, result, article, mapSettings
  const currentView = ref("map");
  const lastView = ref('map');

  function setCurrentView(view) {
    if (currentView.value !== view) {
      lastView.value = currentView.value;
    }
    currentView.value = view;
  }

  function goBack() {
    if (lastView.value === currentView.value) {
      setCurrentView("map");
    } else {
      currentView.value = lastView.value;
    }
  }

  return { currentView, lastView, setCurrentView, goBack };
});
