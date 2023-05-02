<template>
  <section class="section">
    <span v-if="reservations.length === 0">No Reservations</span>
    <router-link
      v-for="reservation in reservations"
      :to="'/reservations/' + reservation.id"
    >
      <div class="card is-clickable" style="margin-bottom: 1em">
        <div class="card-header">
          <div class="card-header-title">
            <reservation-title :reservation="reservation" />
          </div>
        </div>
        <p class="card-content">
          {{ reservation.restaurant.description }}
        </p>
      </div>
    </router-link>
  </section>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import * as api from "@/api";
import type { Reservation } from "@/model";
import ReservationTitle from "@/components/ReservationTitle.vue";

export default defineComponent({
  components: { ReservationTitle },
  data() {
    return {
      reservations: [] as Reservation[],
    };
  },
  async mounted() {
    this.reservations = await api.reservations();
  },
});
</script>
