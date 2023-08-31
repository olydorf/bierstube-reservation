<script setup lang="ts">
import { RouterView } from "vue-router";
</script>

<template>
  <div class="navbar is-fixed-top is-transparent">
    <div class="navbar-brand">
      <a class="navbar-item" @click="$router.go(-1)">
        <icon icon="circle-left" size="xl" />
      </a>
    </div>
  </div>
  <RouterView />
</template>

<style lang="scss">
@import "@/assets/base.scss";
</style>
