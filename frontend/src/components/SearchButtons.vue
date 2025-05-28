<template>
	<div v-if="props.toggleFilterButtons">
		<v-card>
			<v-container>
				<v-row
					no-gutters
					style="flex-wrap: wrap;"
				>
					<v-col
						v-for="filter in filterButtons"
						:key="filter.key"
					>
						<v-btn
							class="ma-2"
							:aria-label="'Filter ' + filter.label"
							@click="handleFilterDataView(filter.key)"
						>{{ props.toggleArtikelPanel ? `${filter.label} (${filteredData[filter.key].length})` : `${filter.label} (${filterDataByQuery[filter.key].length})` }}</v-btn>
					</v-col>
				</v-row>
			</v-container>
		</v-card>
	</div>
</template>
<script setup>
import { computed } from 'vue';
import searchQueryStore from '../store/searchQueryStore';

const searchStore = searchQueryStore();

const filteredData = computed(() => searchStore.filteredData);
const filterDataByQuery = computed(() => searchStore.filteredDataByQuery);

const filterButtons = [
	{ key: 'person', label: 'Personen' },
	{ key: 'orte', label: 'Orte' },
	{ key: 'event', label: 'Ereignisse' },
	{ key: 'topic', label: 'Themen' },
	{ key: 'institution', label: 'Institutionen' },

];

const props = defineProps({
	toggleFilterButtons: {
		type: Boolean,
		default: false
	},
	toggleArtikelPanel: {
		type: Boolean,
		default: false
	},
});

function handleFilterDataView(filterKey) {
	if (props.toggleArtikelPanel) {
		searchStore.activeFilter = filterKey;
	} else {
		searchStore.activeQueryFilter = filterKey;
	}
};
</script>
<style>
.v-row.v-row--no-gutters {
	margin: -12px;
}
</style>
