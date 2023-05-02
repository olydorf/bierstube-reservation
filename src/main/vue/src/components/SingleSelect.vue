<template>
  <div class="control">
    <div class="select">
      <select v-model="selected">
        <option v-if="emptyAllowed" value=""></option>
        <option v-for="o in options" :key="o" :value="o">
          {{ names ? names[o] || o : o }}
        </option>
      </select>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import type { PropType } from "vue";

export default defineComponent({
  data() {
    return {
      selected: this.emptyAllowed ? "" : this.options ? this.options[0] : "",
    };
  },
  props: {
    emptyAllowed: Boolean,
    value: String,
    options: Object as PropType<Array<string>>,
    names: Object,
  },
  watch: {
    selected(n) {
      this.$emit("changed", n);
    },
  },
  emits: {
    changed(v: string) {
      return true;
    },
  },
});
</script>
