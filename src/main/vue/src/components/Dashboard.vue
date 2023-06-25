<template>
  <div>
    <h1>Dashboard</h1>
    <table>
      <thead>
        <tr>
          <th>Reservation ID</th>
          <th>Name</th>
          <th>Date</th>
          <th>Status</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="reservation in reservations" :key="reservation.id">
          <td>{{ reservation.id }}</td>
          <td>{{ reservation.name }}</td>
          <td>{{ reservation.date }}</td>
          <td>{{ reservation.status ? 'Confirmed' : 'Pending' }}</td>
          <td>
            <button v-if="!reservation.status" @click="confirmReservation(reservation.id)">Confirm</button>
            <button @click="cancelReservation(reservation.id)">Cancel</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      reservations: [],
    };
  },
  mounted() {
    this.fetchReservations();
  },
  methods: {
    fetchReservations() {
      // Make an API call to fetch the reservations
       axios.get('/api/reservations')
         .then(response => {
           this.reservations = response.data;
         })
         .catch(error => {
           console.error('Failed to fetch reservations:', error);
         });

      // For demonstration purposes, we'll use a dummy data
      this.reservations = [
        { id: 1, name: 'John Doe', date: '2023-06-20', status: false },
        { id: 2, name: 'Jane Smith', date: '2023-06-22', status: true },
        { id: 3, name: 'Michael Johnson', date: '2023-06-25', status: false },
      ];
    },
    confirmReservation(id) {
      // Make an API call to confirm the reservation with the given ID
       axios.post(`/api/reservations/${id}/confirm`)
         .then(response => {
           // Handle the successful confirmation
           console.log('Reservation confirmed:', response.data);
           // TODO: API call to update the reservations list here
         })
         .catch(error => {
           console.error('Failed to confirm reservation:', error);
         });
      console.log('Confirm reservation:', id);
    },
    cancelReservation(id) {
       axios.post(`/api/reservations/${id}/cancel`)
         .then(response => {
           console.log('Reservation cancelled:', response.data);
           // TODO: API call to update the reservations list here
         })
         .catch(error => {
           console.error('Failed to cancel reservation:', error);
         });
      console.log('Cancel reservation:', id);
    },
  },
};
</script>
