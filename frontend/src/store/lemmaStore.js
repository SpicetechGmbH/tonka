import { defineStore } from 'pinia';
import { ref } from 'vue';
import services from '../services';

export const useLemmaStore = defineStore('lemma', () => {
	const allLemmata = ref([]);
	const lemma = ref({});

	async function fetchAllLemmata() {
		return await new Promise((resolve, reject) => {
			services.lemmata.getAllLemmata()
				.then((response) => {
					allLemmata.value = response.data.allLemmata;
					resolve(response.data);
				})
				.catch((error) => reject(error));
		});
	}
	
	async function fetchArticle(lemmaId) {
		return await new Promise((resolve, reject) => {
			services.lemmata.getLemmataById(lemmaId)
				.then((response) => {
					this.lemma = response.data;
					resolve(response.data);
				})
				.catch((error) => reject(error));
		});
	}

	return { lemma, fetchArticle };
});
