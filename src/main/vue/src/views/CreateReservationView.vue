<template>
  <div class="section">
    <div class="columns">
      <div class="column is-5">
        <div class="card">
          <!-- Add this form -->
          <div class="card-section">
            <form @submit.prevent="reserve">
              <div class="field">
                <label class="label">Name</label>
                <div class="control">
                  <input
                    class="input"
                    type="text"
                    v-model="name"
                    placeholder="Name"
                    required
                  />
                </div>
              </div>
              <div class="field">
                <label class="label">Email</label>
                <div class="control">
                  <input
                    class="input"
                    type="email"
                    v-model="email"
                    placeholder="Email"
                    required
                  />
                </div>
              </div>
                <div class="field">
                    <label class="label">Number of Guests</label>
                    <div class="control">
                        <input
                                class="input"
                                type="number"
                                min="2"
                                max="12"
                                v-model="amountGuests"
                                placeholder="Number of Guests"
                                required
                        />
                    </div>
                </div>
                <div class="field">
                    <label class="label">Message</label>
                    <div class="control">
                        <input
                                class="input"
                                type="message"
                                v-model="message"
                                placeholder="Additional information for the Bierstube"
                                required
                        />
                    </div>
                </div>
            </form>
          </div>
          <div class="card-header">
            <p class="card-header-title">
              {{ dateToLocale(date) }}
            </p>
          </div>
          <div class="card-content">
            <table class="table is-striped is-hoverable is-fullwidth">
              <thead>
                <tr>
                  <th>From</th>
                  <th>To</th>
                </tr>
              </thead>
              <tbody>
                <tr
                  v-for="slot in timeSlots"
                  class="is-clickable"
                  @click="time = slot[0]"
                  :class="slot[0] === time ? 'has-background-info' : ''"
                >
                  <td>{{ timeToLocale(slot[0]) }}</td>
                  <td>{{ timeToLocale(slot[1]) }}</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="card-footer">
            <a
              class="card-footer-item"
              :class="time === null || email === '' || name === '' || amountGuests === 0 ? 'is-disabled' : ''"
              :title="
                (time === null ? 'Select a time first\n' : '')
              "
              @click="reserve()"
            >
              <icon icon="circle-check" style="margin-right: 1em" />
              Reserve
            </a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import { LOADING_RESTAURANT } from "@/model";
import {
  SECONDS,
  dateToLocale,
  timeToLocale,
  nextOccurrenceOfWeekDay,
  RESERVATION_DURATION,
  timeSlots,
} from "@/time";
import * as api from "@/api";

export default defineComponent({
  data() {
    return {
      // derived
      weekDay: this.$route.query.weekDay as string,
      restaurant: LOADING_RESTAURANT,
      date: new Date(),
      // user selected
      time: null as null | Date,
      reserved: false,
      // added form data
      name: "",
      email: "",
      amountGuests: 0,
      message: "",
    };
  },
  methods: {
    dateToLocale: dateToLocale,
    timeToLocale: timeToLocale,

    async reserve() {
      if (this.time == null || this.reserved) return;
      this.reserved = true;

      let response = await api.reserve({

          name: this.name,
          email: this.email,
          amountGuests: this.amountGuests,

        startTime: this.time.toISOString(),
        endTime: new Date(
          this.time.getTime() + RESERVATION_DURATION
        ).toISOString(),
          message: this.message,
      });
        if (response.status === 204) {
            // No table available, navigate to specific view or show message

           await this.$router.push('/no-table-available');}
        else if (this.amountGuests>8)
                await this.$router.push('/confirmation-pending');
        else {
            let reservation = await response.json();
      // Send reservation confirmation email
      await api.sendReservationConfirmationEmail(this.name, this.email);

      await this.$router.push('/reservations/' +  reservation.id);
    }},
  },
  computed: {
    timeSlots() {
      return timeSlots(this.restaurant.openingHours, this.date, this.weekDay);
    },
  },

  async mounted() {
    this.weekDay = this.weekDay as string;
    this.restaurant = await api.restaurant();
    let nextNthDay = nextOccurrenceOfWeekDay(this.weekDay);
    if (nextNthDay == null) {
      await this.$router.replace(`/restaurant`);
      return;
    }
    this.date = nextNthDay;

  },

});
</script>
