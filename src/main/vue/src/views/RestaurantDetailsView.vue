<template >
  <div :style="{'background':'url(https://www.oly-dorf.de/wp-content/uploads/2022/11/dorf-2-1024x680-1.jpg)',
                'position': 'fixed',
                'left':'0',
                'right':'0',
                'top':'0',
                'bottom':'0'}">
      <div class="section" style="text-align: center; padding-top: 100px">
              <img src="../../../resources/static/bierstubeLogo.png" alt="Bierstube Logo" style="padding: 50px"/>
                      <!-- General Information -->
                      <!-- Opening Hours -->
              <table class="table is-striped is-hoverable" style="margin-left: auto; margin-right: auto;
                                                                  height: 50%; width: 50%;
                                                                  box-shadow: 3px 3px 5px 6px rgba(0, 0, 0, 0.5);">
                  <thead>
                  <tr>
                      <th>Weekday</th>
                      <th>From</th>
                      <th>To</th>
                  </tr>
                  </thead>
                  <tbody style="border-radius: 10px">
                  <tr
                          v-for="oh in restaurant.openingHours"
                          class="is-clickable"
                          @click="$router.push(`/reserve?weekDay=${oh.weekDay}`)">
                      <th>{{ weekdayToLocale(oh.weekDay) }}</th>
                      <td>{{ timeToLocale(oh.startTime) }}</td>
                      <td>{{ timeToLocale(oh.endTime) }}</td>
                  </tr>
                  </tbody>
              </table>
      </div>
  </div>
    <div class="section" style="background-color: white; z-index: 10">
        <h1>HII THIS IS CONTECT</h1>
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
