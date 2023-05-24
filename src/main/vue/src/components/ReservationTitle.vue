<template>
  on
  {{ dateToLocale(reservation.startTime) }}
  at
  {{ timeToLocale(new Date(reservation.startTime)) }}
</template>

<script lang="ts">
import type { PropType } from "vue";
import type { Reservation } from "@/model";
import { defineComponent } from "vue";
import { dateToLocale, timeToLocale } from "@/time";

export default defineComponent({
  props: {
    reservation: Object as PropType<Reservation>,
  },
  methods: {
    dateToLocale: dateToLocale,
    timeToLocale: timeToLocale,
  },
});
</script>
