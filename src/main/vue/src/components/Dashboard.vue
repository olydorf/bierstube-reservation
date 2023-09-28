<template>
    <div class="card-header">
        <h1 class="card-header-title">All Reservations</h1>
        <div class="padding-plus-sign">
            <button class="circle plus" @click="addReservation"></button>
        </div>
        <div class="card-sorting">Sort By: </div>
        <div class="card-sorting">
            <select  v-model="sortBy">
                <option value="startTime">Start Time</option>
                <option value="status">Status</option>
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
                    <th>Email</th>
                    <th>Message</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="(reservation, index) in sortedReservations" :key="reservation.id">
                    <td>{{ reservation.id }}</td>
                    <td>{{ reservation.name }}</td>
                    <td>{{ reservation.startTime.replace("T", "   ") }}</td>
                    <td>{{ reservation.endTime.replace("T", "   ") }}</td>
                    <td>{{ reservation.amountGuests }}</td>
                    <td>{{ reservation.email }}</td>
                    <td>
                        <div @mouseover="showOverlay(index)" @mouseleave="hideOverlay()">
                              Message
                        </div>
                        <span class="camsg-popup" v-if="show === index && reservation.message !== null ">{{ reservation.message }}</span>
                    </td>
                    <td>
                        <div v-bind:class="{ 'Confirmed': reservation.status === true, 'Pending': reservation.status === false }">
                            {{ reservation.status ? 'Confirmed' : 'Pending' }}
                        </div>
                    </td>
                    <td>
                        <button class="card is-clickable" v-if="!reservation.status" @click="confirmReservation(reservation.id)">Confirm</button>
                        <button class="card is-clickable" @click="cancelReservation(reservation.id)">Cancel</button>
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
    padding-right: 20px;
}

.padding-plus-sign {
    padding-top: 8px;
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

.circle{
    border: 1px solid #aaa;
    box-shadow: inset 1px 1px 3px #fff;
    width: 22px;
    height: 22px;
    border-radius: 100%;
    position: relative;
    margin: 4px;
    display: inline-block;
    vertical-align: middle;
    background: #aaaaaa4f;
}
.circle:hover{
    background: #6363634f;
}
.circle:active{
    background: radial-gradient(#aaa, #fff);
}
.circle:before,
.circle:after{
    content:'';position:absolute;top:0;left:0;right:0;bottom:0;
}
/* PLUS */
.circle.plus:before,
.circle.plus:after {
    background:black;
    box-shadow: 1px 1px 1px #ffffff9e;
}
.circle.plus:before{
    width: 2px;
    margin: 3px auto;
}
.circle.plus:after{
    margin: auto 3px;
    height: 2px;
    box-shadow: none;
}
.camsg-popup {
    margin: 20px auto;
    padding: 20px;
    background: white;
    border-radius: 5px;
    position: fixed;
    z-index: 5;
    border-color: lightgrey;
    border-style: solid;
}

</style>

<script>
import axios from "axios";
import * as api from "@/api";
import { defineComponent } from "vue";
import UserService from "@/services/user.service";
import router from "@/router";

export default defineComponent({
    computed: {
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
            sortBy: 'startTime',
            show: null,
            showWordIndex: null,
        };
    },
    mounted() {
        this.fetchReservations();
        this.getUserToken();
    },
    watch: {
        reservations(reservations) {
            console.log(JSON.stringify(reservations()))
        }
    },
    methods: {
        async getUserToken() {
            const response = await UserService.getUserBoard();
        },
        async fetchReservations() {
            this.reservations = await api.reservations();
        },
        addReservation() {
            router.push("/reserve")
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
        },
        showOverlay(index) {
            this.show = index;
        },
        hideOverlay() {
            this.show = null;
        }
    },
})
</script>