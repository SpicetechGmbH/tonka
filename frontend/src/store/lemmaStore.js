import { defineStore } from 'pinia';
import { ref } from 'vue';
import services from '../services';

export const useLemmaStore = defineStore('lemma', () => {
	const allLemmata = ref([]);
	const lemma = ref({});
	const ort = ref(null);
	const lemmaTypes = ref([]);

	async function fetchAllLemmata() {
		return await new Promise((resolve, reject) => {
			services.lemma.getAllLemmata()
				.then((response) => {
					allLemmata.value = response.data.allLemmata;
					resolve(response.data);
				})
				.catch((error) => reject(error));
		});
	}

	async function fetchLemma(lemmaId) {
		return await new Promise((resolve, reject) => {
			services.lemma.getLemmaById(lemmaId)
				.then((response) => {
					ort.value = null; // Reset ort when fetching a new lemma
					lemma.value = response.data;
					resolve(response.data);
				})
				.catch((error) => reject(error));
		});
	}

	async function fetchOrt(ortId, lemmaId) {
		return await new Promise((resolve, reject) => {
			services.lemma.getLemmaById(lemmaId)
				.then((response) => {
					ort.value = ortId;
					lemma.value = response.data;
					resolve(response.data);
				})
				.catch((error) => reject(error));
		});
	}

	async function fetchLemmaTypes() {
		return await new Promise((resolve, reject) => {
			services.lemma.getLemmaTypes()
				.then((response) => {
					lemmaTypes.value = response.data.lemmaTypes;
					resolve(response.data);
				})
				.catch((error) => reject(error));
		});
	}

	return { lemma, ort, lemmaTypes, fetchLemma, fetchOrt, fetchLemmaTypes };
});
