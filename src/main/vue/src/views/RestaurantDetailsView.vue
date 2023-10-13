<template>
  <div class="section">
    <div class="columns">
      <div class="column is-5">
        <!-- General Information -->
        <h1 class="is-size-1">{{ restaurant.name }}</h1>

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
                    `/reserve?weekDay=${oh.weekDay}`
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
          <!-- Impressum and Datenschutz links -->
          <div class="links-section">
              <a href="https://www.oly-dorf.de/impressum/" target="_blank">Impressum</a>
              |
              <a href="https://www.oly-dorf.de/datenschutz/" target="_blank">Datenschutz</a>
          </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import * as api from "@/api";
import { defineComponent } from "vue";
import { LOADING_RESTAURANT } from "@/model";
import { timeToLocale, weekdayToLocale } from "@/time";

export default defineComponent({
  data() {
    return {
      restaurant: LOADING_RESTAURANT,
    };
  },
  async mounted() {
    this.restaurant = await api.restaurant();

  },
  methods: {
    weekdayToLocale: weekdayToLocale,
    timeToLocale: timeToLocale,
  },
});
</script>
