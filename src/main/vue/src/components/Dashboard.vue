<template>
  <div>
    <h1>Dashboard</h1>
    <table>
      <thead>
        <tr>
          <th>Reservation ID</th>
          <th>Reservation Date</th>
          <th>Status</th>
          <th>Action</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="reservation in reservations" :key="reservation.id">
          <td>{{ reservation.id }}</td>
          <td>{{ reservation.reservationDate }}</td>
          <td>{{ reservation.status }}</td>
          <td>
            <button v-if="reservation.status === 'PENDING'" @click="confirmReservation(reservation.id)">Confirm</button>
            <span v-else>Confirmed</span>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
export default {
  data() {
    return {
      reservations: [],
    };
  },
  methods: {
    confirmReservation(reservationId) {
      // axios.put(`/api/reservations/${reservationId}/confirm`)
      //   .then(response => {
      //     const confirmedReservation = this.reservations.find(reservation => reservation.id === reservationId);
      //     if (confirmedReservation) {
      //       confirmedReservation.status = 'CONFIRMED';
      //     }
      //   })
      //   .catch(error => {
      //     // Handle any errors
      //     console.error(error);
      //   });
    },
    fetchReservations() {
      // axios.get('/api/reservations')
      //   .then(response => {
      //     this.reservations = response.data;
      //   })
      //   .catch(error => {
      //     // Handle any errors
      //     console.error(error);
      //   });
    },
  },
};
</script>
