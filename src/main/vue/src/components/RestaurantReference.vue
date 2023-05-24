<template>
  <router-link :to="'/restaurants/'">
    <div class="card is-clickable" style="margin-bottom: 1em">
      <div class="card-header">
        <div class="card-header-title">{{ restaurant.name }}</div>
      </div>
      <!-- TODO add more details and maybe a picture -->
    </div>
  </router-link>
</template>

<script lang="ts">
import type { PropType } from "vue";
import type { Restaurant } from "@/model";
import { defineComponent } from "vue";

export default defineComponent({
  name: "RestaurantReference",
  props: {
    restaurant: Object as PropType<Restaurant>,
  },
});
</script>
