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
              Name: {{ reservation.name }}
              <br />
              Email: {{ reservation.email }}
              <br />
              Message: {{reservation.message}}

          </div>
        </div>
      </div>
      <div class="column is-1 buttons">
        <div style="height: 5em"></div>
          <button class="button is-rounded" @click="cancel=true">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><path d="M3 6v18h18v-18h-18zm5 14c0 .552-.448 1-1 1s-1-.448-1-1v-10c0-.552.448-1 1-1s1 .448 1 1v10zm5 0c0 .552-.448 1-1 1s-1-.448-1-1v-10c0-.552.448-1 1-1s1 .448 1 1v10zm5 0c0 .552-.448 1-1 1s-1-.448-1-1v-10c0-.552.448-1 1-1s1 .448 1 1v10zm4-18v2h-20v-2h5.711c.9 0 1.631-1.099 1.631-2h5.315c0 .901.73 2 1.631 2h5.712z"/></svg>
          </button>
          <div v-if="cancel" class="popup">
            <h1 style="padding: 8px">Do you want to delete you reservation?</h1>
              <div>
<!--                  todo: cancel reservation functionality-->
                  <button class="button is-rounded" style="position: relative" @click="">
                      Yes
                  </button>
                  <button class="button is-rounded" style="position: relative" @click="cancel=false">
                      No
                  </button>
              </div>
          </div>
          <a
          class="button is-rounded"
          title="Download calendar entry"
          :href="`/api/reservations/${reservation.id}/calendar.ics`"
        >
          <icon icon="calendar-plus" size="xl" />
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
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";

export default defineComponent({
  components: {FontAwesomeIcon, ReservationTitle},
  data() {
    return {
      reservation: LOADING_RESERVATION,
      cancel: false,
    };
  },
  methods: {
    dateToLocale: dateToLocale,
    timeToLocale: timeToLocale,
  },
  async mounted() {


      this.reservation = await api.reservation(parseInt(this.$route.params.id as string));
  },
});
</script>

<style scoped>

.popup {
    padding: 8px;
    border-color: white;
    border-radius: 10px;
    position: absolute;
    top: 40%;
    right: 40%;
    margin: auto;
    z-index: 5;
    box-shadow: 3px 3px 5px 6px #ccc;
}


</style>
