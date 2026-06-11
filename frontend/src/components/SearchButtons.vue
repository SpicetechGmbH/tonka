<template>
	<div v-if="props.showFilterButtons">
		<v-card>
			<v-btn-toggle
				:model-value="searchStore.activeQueryFilter"
				@update:model-value="handleFilterDataView($event)"
				variant="outlined"
				class="mx-2"
				divided
			>
				<v-btn
					v-for="filter in filterButtons"
					:key="filter.key"
					class="my-2"
					:value="filter.key"
					:aria-label="'Filter ' + filter.label"
				>{{ `${filter.label} (${filterDataByQuery[filter.key].length})` }}</v-btn>
			</v-btn-toggle>
		</v-card>
	</div>
</template>
<script setup>
import { computed } from 'vue';
import { useLemmaStore } from '../store/lemmaStore';
import { useSearchQueryStore } from '../store/searchQueryStore';
import Stringer from '../utils/Stringer';

const lemmaStore = useLemmaStore();
const searchStore = useSearchQueryStore();

const filterDataByQuery = computed(() => searchStore.filteredDataByQuery);

const filterButtons = computed(() => {
	return (lemmaStore.lemmaTypes || []).map(type => ({
		key: type.lemma_type.toLowerCase(),
		label: type.lemma_type_gui_name || Stringer.capitalize(type.lemma_type)
	}));
});

const props = defineProps({
	showFilterButtons: {
		type: Boolean,
		default: false
	}
});

function handleFilterDataView(filterKey) {
	if (filterKey === undefined) {
		searchStore.activeQueryFilter = '';
	} else {
		searchStore.activeQueryFilter = filterKey;
	}
};
</script>
<style lang="scss">
.v-btn-toggle {
	.v-btn {
		// min-width: 120px;

		&.v-btn--active {
			background-color: rgba(var(--v-theme-primary), 0.8);
			color: white;
		}
	}
}
</style>
