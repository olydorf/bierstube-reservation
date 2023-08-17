<template>
    <div class="card-header">
        <h1 class="card-header-title">All Reservations</h1>
        <div class="card-sorting">Sort By: </div>
        <div class="card-sorting">
            <select  v-model="sortBy">
                <option value="startTime">Start Time</option>
                <option value="status">Status</option>
                <option value="restaurantTable">Table ID</option>
                <option value="amountGuests">Number of Guests</option>
            </select>
        </div>
    </div>
    <div class="card-body">
        <div class="card-content">
            <table class="table is-striped is-hoverable is-fullwidth">
                <thead>
                <tr>
                    <th>Reservation ID</th>
                    <th>Name</th>
                    <th>Start Time</th>
                    <th>End Time</th>
                    <th>Number of Guests</th>
                    <th>Table ID</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="reservation in sortedReservations" :key="reservation.id">
                    <td>{{ reservation.id }}</td>
                    <td>{{ reservation.name }}</td>
                    <td>{{ reservation.startTime.replace("T", "   ") }}</td>
                    <td>{{ reservation.endTime.replace("T", "   ") }}</td>
                    <td>{{ reservation.amountGuests }}</td>
                    <td>{{ reservation.restaurantTable.id }}</td>
                    <td>
                        <div v-bind:class="{ 'Confirmed': reservation.status === true, 'Pending': reservation.status === false }">
                            {{ reservation.status ? 'Confirmed' : 'Pending' }}
                        </div>
                    </td>
                    <td>
                        <button class="card is-clickable" v-if="!reservation.status" @click="confirmReservation(reservation.id)">Confirm</button>
                        <button class="card is-clickable" @click="cancelReservation(reservation.id)">Cancel</button>
                        <button class="card is-clickable" @click="changeReservation(reservation.id)">Change</button>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>

</template>

<style scoped>
.card {
    margin-right: 15px;
}

.card-sorting {
    padding-top: 10px;
    //padding-bottom: 0px;
    padding-right: 20px;
}

.card-header-title {
    font-size: large;
}

.Confirmed {
    background-color: darkseagreen;
    display: flex;
    justify-content: center;
    align-items: center;
    margin-right: 15px;
}

.Pending {
    background-color: lightgoldenrodyellow;
    display: flex;
    justify-content: center;
    align-items: center;
    margin-right: 15px;
}

</style>

<script>
import axios from "axios";
import * as api from "@/api";
import { defineComponent } from "vue";
import {regex} from "vee-validate/dist/rules.esm";

export default defineComponent({
    computed: {
        regex() {
            return regex
        },
        sortedReservations() {
            return this.reservations.sort((a, b) =>
                JSON.stringify(a[this.sortBy]).localeCompare(JSON.stringify(b[this.sortBy]))
            )
        }
    },
    data() {
        return {
            reservations: [],
            rerenderReservationKey: 0,
            sortBy: 'startTime'
        };
    },
    mounted() {
        this.fetchReservations();
    },
    watch: {
        reservations(reservations) {
            console.log(JSON.stringify(reservations()))
        }
    },
    methods: {
        async fetchReservations() {
            this.reservations = await api.reservations();
        },
        confirmReservation(id) {
            // Make an API call to confirm the reservation with the given ID
            axios.post(`/confirm-reservation/${id}`)
                .then(response => {
                    // Handle the successful confirmation
                    console.log('Reservation confirmed:', response.data);
                    this.reservationRerender()
                })
                .catch(error => {
                    console.error('Failed to confirm reservation:', error);
                });
            console.log('Confirm reservation:', id);
        },
        cancelReservation(id) {
            axios.post(`/cancel-reservation/${id}`)
                .then(async response => {
                    console.log('Reservation cancelled:', response.data);
                    this.reservationRerender()
                })
                .catch(error => {
                    console.error('Failed to cancel reservation:', error);
                });
            console.log('Cancel reservation:', id);
        },
        changeReservation(id) {

        },
        reservationRerender() {
            this.rerenderReservationKey += 1;
        }
    },
})
</script>