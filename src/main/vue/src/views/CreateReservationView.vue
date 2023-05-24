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
                <label class="label">Phone Number</label>
                <div class="control">
                  <input
                    class="input"
                    type="tel"
                    v-model="phone"
                    placeholder="Phone Number"
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
              :class="time === null || table < 0 ? 'is-disabled' : ''"
              :title="
                (time === null ? 'Select a time first\n' : '') +
                (table < 0 ? 'Select a table first' : '')
              "
              @click="reserve()"
            >
              <icon icon="circle-check" style="margin-right: 1em" />
              Reserve
            </a>
          </div>
        </div>
      </div>
      <div class="column is-7">
        <div class="card" :class="time === null ? 'is-hidden' : ''">
          <div class="card-header">
            <p class="card-header-title">Select a table</p>
          </div>
          <div class="card-content">
            <div v-html="restaurant.layoutSvg"></div>
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
      freeTables: [] as number[],

      // user selected
      time: null as null | Date,
      table: { tableNumber: 0 },
      reserved: false,
      // added form data
      name: "",
      email: "",
      phone: "",
      // helper
      renderedLayout: false,
      freeTablesPoller: null as null | number,
    };
  },
  methods: {
    dateToLocale: dateToLocale,
    timeToLocale: timeToLocale,

    async reserve() {
      if (this.time == null || this.table.tableNumber < 0 || this.reserved) return;
      this.reserved = true;

      let reservation = await api.reserve({
        user: {
          name: this.name,
          email: this.email,
          phone: this.phone,
        },
        restaurant: this.restaurant,
        startTime: this.time.toISOString(),
        endTime: new Date(
          this.time.getTime() + RESERVATION_DURATION
        ).toISOString(),
        table: this.table,
      });
      // Send reservation confirmation email
      await api.sendReservationConfirmationEmail(this.name, this.email);

      await this.$router.push(`/reservations`);
    },
    async checkFreeTables() {
      if (this.time == null) return;
      this.freeTables = await api.freeTablesAt(this.time);
      if (!this.freeTables.includes(this.table.tableNumber)) this.table.tableNumber = -1;
      this.renderLayout(true);
    },
    renderLayout(force: boolean) {
      if (this.renderedLayout && !force) return;
      for (let table of this.restaurant.tables) {
        let svg = document.getElementById(
          "table-" + table.tableNumber
        ) as unknown as SVGElement;
        if (svg) this.renderedLayout = true;
        svg.classList.remove(
          "svg-table",
          "selected-table",
          "reserved-table",
          "disabled-table"
        );
        svg.classList.add("svg-table");
        if (table.tableNumber == this.table.tableNumber)
          svg.classList.add("selected-table");
        if (!this.freeTables.includes(table.tableNumber))
          svg.classList.add("reserved-table");
        let t = this;
        svg.onclick = () => {
          if (!t.freeTables.includes(table.tableNumber)) return;
          t.table.tableNumber = table.tableNumber;
          t.renderLayout(true);
        };
      }
    },
  },
  computed: {
    timeSlots() {
      return timeSlots(this.restaurant.openingHours, this.date, this.weekDay);
    },
  },
  watch: {
    async time(n) {
      await this.checkFreeTables();
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
    await this.checkFreeTables();
    this.freeTablesPoller = setInterval(() => {
      this.checkFreeTables();
    }, SECONDS);
  },
  beforeUnmount() {
    if (this.freeTablesPoller == null) return;
    clearInterval(this.freeTablesPoller);
    this.freeTablesPoller = null;
  },
});
</script>
