<script setup lang="ts">
import { RouterView } from "vue-router";
</script>

<template>
    <div class="navbar is-fixed-top is-transparent">
        <div class="navbar-menu">
            <div class="navbar-start">
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

body {
    background-image: url(~@/assets/dorf-2-1024x680-1.jpg);
    background-size: cover;
    background-repeat: no-repeat;
    background-position: center center;
    margin: 0;
    font-family: 'Avenir', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
}
.navbar-menu {
  display: flex;
  justify-content: flex-start;
}
</style>
