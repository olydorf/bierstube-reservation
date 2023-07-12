<template>
    <div class="no-table-available">
        <h1>No Tables Available</h1>
        <p>Unfortunately, there are no tables available for the requested time and guest count.</p>
        <button @click="goBack">Try a Different Time</button>
    </div>
</template>

<script>
export default {
    name: 'NoTableAvailable',
    methods: {
        goBack() {
            this.$router.push('/restaurant');
        }
    }
}
</script>

<style scoped>
.no-table-available {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: 100vh;
}

.no-table-available h1 {
    font-size: 2rem;
    color: #333;
}

.no-table-available p {
    font-size: 1.2rem;
    color: #666;
}

.no-table-available button {
    margin-top: 1rem;
    padding: 0.5rem 1rem;
    font-size: 1.2rem;
    color: #fff;
    background-color: #f44336;
    border: none;
    border-radius: 5px;
    cursor: pointer;
}
</style>
