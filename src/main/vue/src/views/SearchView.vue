<template>
  <div class="section">
    <div class="field">
      <div class="control">
        <input
          class="input"
          type="text"
          placeholder="Search for a restaurant"
          v-model="filterName"
        />
      </div>
    </div>

    <div class="field is-grouped">
      <!-- TODO replace by MultiSelect built from dropdown buttons -->
      <label class="label">Cuisine</label>
      <SingleSelect
        :options="allCuisines || []"
        :empty-allowed="true"
        @changed="setFilterCuisines"
      />
      <label class="label">Minimum Price</label>
      <SingleSelect
        :options="PRICE_CATEGORIES"
        :names="PRICE_CATEGORY_NAMES"
        :empty-allowed="true"
        @changed="setFilterMinPrice"
      />
      <label class="label">Maximum Price</label>
      <SingleSelect
        :options="PRICE_CATEGORIES"
        :names="PRICE_CATEGORY_NAMES"
        :empty-allowed="true"
        @changed="setFilterMaxPrice"
      />
      <label class="label">Minimum Rating</label>
      <input
        class="input"
        type="number"
        min="0"
        max="5"
        step="0.5"
        style="width: 5em"
        v-model="filterMinRating"
      />
    </div>

    <div class="columns">
      <div class="column is-one-third">
        <RestaurantReference
          v-for="r in searchResults"
          :restaurant="r"
          :key="r.id"
        />
      </div>
      <div class="column is-two-thirds">
        <RestaurantMap></RestaurantMap>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import RestaurantReference from "@/components/RestaurantReference.vue";
import RestaurantMap from "@/components/RestaurantMap.vue";
import * as api from "@/api";
import { defineComponent } from "vue";
import type { Restaurant } from "@/model";
import { PRICE_CATEGORIES, PRICE_CATEGORY_NAMES } from "@/model";
import SingleSelect from "@/components/SingleSelect.vue";

export default defineComponent({
  components: { RestaurantMap, RestaurantReference, SingleSelect },
  data() {
    return {
      // filters
      filterName: "",
      filterCuisines: [] as string[],
      filterMinPrice: "",
      filterMaxPrice: "",
      filterMinRating: 0,

      // selectable values
      allCuisines: [] as string[],
      searchResults: [] as Restaurant[],
      PRICE_CATEGORIES: PRICE_CATEGORIES,
      PRICE_CATEGORY_NAMES: PRICE_CATEGORY_NAMES,
    };
  },
  watch: {
    filterName(n) {
      this.search();
    },
    filterCuisines(n) {
      this.search();
    },
    filterMinPrice(n) {
      this.search();
    },
    filterMaxPrice(n) {
      this.search();
    },
    filterMinRating(n) {
      if (n > 5.0) this.filterMinRating = 5.0;
      if (n < 0) this.filterMinRating = 0.0;

      this.search();
    },
  },
  async mounted() {
    this.allCuisines = await api.cuisines();
    await this.search();
  },
  methods: {
    setFilterCuisines(v: string) {
      this.filterCuisines = [v];
    },
    setFilterMinPrice(v: string) {
      this.filterMinPrice = v;
    },
    setFilterMaxPrice(v: string) {
      this.filterMaxPrice = v;
    },
    async search() {
      this.searchResults = await api.restaurants(
        this.filterName,
        this.filterCuisines,
        this.filterMinPrice,
        this.filterMaxPrice,
        this.filterMinRating
      );
    },
  },
});
</script>
