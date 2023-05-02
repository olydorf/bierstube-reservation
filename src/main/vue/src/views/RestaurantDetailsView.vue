<template>
  <div class="section">
    <div class="columns">
      <div class="column is-5">
        <!-- General Information -->
        <h1 class="is-size-1">{{ restaurant.name }}</h1>
        <Stars :rating="rating" />

        <p style="margin-top: 1em">
          {{ restaurant.description }}
        </p>

        <!-- Opening Hours -->
        <section class="section">
          <table class="table is-striped is-hoverable is-fullwidth">
            <thead>
              <tr>
                <th>Weekday</th>
                <th>From</th>
                <th>To</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="oh in restaurant.openingHours"
                class="is-clickable"
                @click="
                  $router.push(
                    `/restaurants/${restaurant.id}/reserve?weekDay=${oh.weekDay}`
                  )
                "
              >
                <th>{{ weekdayToLocale(oh.weekDay) }}</th>
                <td>{{ timeToLocale(oh.startTime) }}</td>
                <td>{{ timeToLocale(oh.endTime) }}</td>
              </tr>
            </tbody>
          </table>
        </section>

        <ul>
          <li v-for="review in reviews">
            <div class="card" style="margin-bottom: 1em">
              <p class="card-content">{{ review.comment }}</p>
              <div class="card-footer">
                <p class="card-footer-item">{{ review.author.name }}</p>
                <p class="card-footer-item" style="justify-content: left">
                  <Stars :rating="review.rating" />
                </p>
              </div>
            </div>
          </li>
        </ul>
      </div>
      <div class="buttons column is-1">
        <div style="height: 5em"></div>
        <a
          class="button is-rounded"
          title="Visit their website"
          :href="restaurant.website"
        >
          <icon icon="arrow-up-right-from-square" />
        </a>
      </div>
      <div class="column is-6">
        <img
          v-for="pic in restaurant.pictures"
          alt="A picture of the restaurant."
          :src="pic"
          class="image"
        />
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import Stars from "@/components/Stars.vue";
import * as api from "@/api";
import { defineComponent } from "vue";
import { LOADING_RESTAURANT } from "@/model";
import { timeToLocale, weekdayToLocale } from "@/time";
import type { Review } from "@/model";

export default defineComponent({
  components: { Stars },
  props: {
    id: Number,
  },
  data() {
    return {
      restaurant: LOADING_RESTAURANT,
      rating: -1,
      reviews: [] as Review[],
    };
  },
  async mounted() {
    let id = parseInt(this.$route.params.id as string);

    this.restaurant = await api.restaurant(id);
    this.rating = await api.restaurantRating(id);
    this.reviews = await api.reviews(id);
  },
  methods: {
    weekdayToLocale: weekdayToLocale,
    timeToLocale: timeToLocale,
  },
});
</script>
