<template>
  <div class="section">
    <div class="columns">
      <div class="column is-5">
        <div class="card">
          <div class="card-header">
            <p class="card-header-title">
              <reservation-title :reservation="reservation" />
            </p>
          </div>
          <div class="card-content">
            Table: {{ reservation.restaurantTable.id }}
              <br />
              Name: {{ reservation.user.name }}
              <br />
              Email: {{ reservation.user.email }}

          </div>
        </div>
      </div>
      <div class="column is-1 buttons">
        <div style="height: 5em"></div>
        <a
          class="button is-rounded"
          title="Download calendar entry"
          :href="`/api/reservations/${reservation.id}/calendar.ics`"
        >
          <icon icon="calendar-plus" size="xl" />
        </a>
        <br />
        <a class="button is-rounded" title="Print reservation" :href="''">
          <icon icon="print" size="xl" />
        </a>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import { LOADING_RESERVATION } from "@/model";
import { dateToLocale, timeToLocale } from "@/time";
import * as api from "@/api";
import ReservationTitle from "@/components/ReservationTitle.vue";

export default defineComponent({
  components: {ReservationTitle},
  data() {
    return {
      reservation: LOADING_RESERVATION,
    };
  },
  methods: {
    dateToLocale: dateToLocale,
    timeToLocale: timeToLocale,
  },
  async mounted() {
      console.log('Fetched reservation:', this.reservation);

      this.reservation = await api.reservation(this.reservation.id);
  },
});
</script>
