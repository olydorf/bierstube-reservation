<template>
    <div class="confirmation-pending">
        <h1>Reservation Confirmation Pending</h1>
        <p>
            Thank you for your reservation request. As your party has more than 8 guests, our team at Bierstube is currently reviewing it.
            You will receive a confirmation email once your reservation is confirmed.
        </p>
        <router-link to="/">Return to Homepage</router-link>
    </div>
</template>

<script>
export default {
    name: 'ConfirmationPending'
}
</script>

<style scoped>
.confirmation-pending {
    text-align: center;
    margin-top: 50px;
}

h1 {
    margin-bottom: 20px;
}
</style>
