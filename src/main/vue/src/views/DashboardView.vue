<script lang="ts">
import Dashboard from "@/components/Dashboard.vue";

export default {
    components: {
        Dashboard
    }
}
</script>

<template>

    <div class="card">
        <Dashboard />
    </div>
</template>

<style scoped>
.card {
    transition: box-shadow .3s;
    box-shadow: 0 0.5em 1em -0.125em rgba($scheme-invert, 0.3), 0 0 0 1px rgba($scheme-invert, 0.02);
    margin-left: 2em;
    margin-right: 2em;
}

</style>