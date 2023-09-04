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
    <div class="navbar-menu">
      <div class="navbar-start">
        <router-link class="navbar-item" to="/reservations">
          Reservations</router-link>
        <!-- TODO this should only be shown when user is logged in as admin -->
        <router-link class="navbar-item" to="/dashboard">
            Dashboard</router-link>
      </div>
    </div>
  </div>
  <RouterView />
</template>

<style lang="scss">
@import "@/assets/base.scss";
.navbar-menu {
  display: flex;
  justify-content: flex-start;
}
</style>
