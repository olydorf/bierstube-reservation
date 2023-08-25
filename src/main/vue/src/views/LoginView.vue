<script lang="ts">
import Login from "@/components/Login.vue";

export default {
    components: {
        Login
    }
}
</script>

<template>

    <div>
        <Login />
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