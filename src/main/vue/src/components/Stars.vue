<template>
  <icon v-for="i in range(1, rating)" :key="i" icon="star" />
  <icon v-if="rating - Math.floor(rating) > 0.5" icon="star-half" />
</template>

<script>
import { defineComponent } from "vue";

export default defineComponent({
  props: {
    rating: Number,
  },
  methods: {
    range(lower, upper_inkl) {
      let r = [];
      let i = lower;
      while (i <= upper_inkl) {
        r.push(i);
        i++;
      }

      return r;
    },
  },
});
</script>

<style lang="scss">
span {
  font-size: 2em;
}
</style>
