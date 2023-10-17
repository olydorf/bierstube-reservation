<template>
  <div style="background:url(https://www.oly-dorf.de/wp-content/uploads/2022/11/dorf-2-1024x680-1.jpg);
                background-size: cover;
                display: grid;
                margin: 0;
                grid-template-rows: auto 1fr auto;
                left:0;
                right:0;
                top:0;
                bottom:0">
      <div class="section" style="text-align: center; padding-top: 70px; padding-bottom: 160px">
              <img src="../../../resources/static/bierstubeLogo.png" alt="Bierstube Logo" style="padding: 50px"/>
              <table class="table is-striped is-hoverable" style="margin-left: auto; margin-right: auto;
                                                                  height: 50%; width: 50%;
                                                                  box-shadow: 3px 3px 5px 6px rgba(0, 0, 0, 0.5);">
                  <thead>
                  <tr>
                      <th>Weekday</th>
                      <th>From</th>
                      <th>To</th>
                  </tr>
                  </thead>
                  <tbody style="border-radius: 10px">
                  <tr
                          v-for="oh in restaurant.openingHours"
                          class="is-clickable"
                          @click="$router.push(`/reserve?weekDay=${oh.weekDay}`)">
                      <th>{{ weekdayToLocale(oh.weekDay) }}</th>
                      <td>{{ timeToLocale(oh.startTime) }}</td>
                      <td>{{ timeToLocale(oh.endTime) }}</td>
                  </tr>
                  </tbody>
              </table>

      </div>
      <div class="footer" style="bottom: 0; text-align: center; background-color: #e7ddb5">
          <div class="sideBySide">
              <p style="font-size: large; padding-bottom: 20px; font-weight: bold">Geschäftszeiten</p>
              <p>Montag: 19:00pm – 00:30am</p>
              <p>Dienstag: 19:00pm – 00:30am</p>
              <p>Mittwoch: 19:00pm – 00:30am</p>
              <p>Donnerstag: 19:00pm – 00:30am</p>
              <p>Freitag: 19:00pm – 00:30am</p>
              <p>Samstag: 19:00pm – 00:30am</p>
              <p>Sonntag: GESCHLOSSEN</p>
          </div>
          <div class="sideBySide">
              <p style="font-size: large; padding-bottom: 20px; font-weight: bold">Kontakt</p>
              <p style="padding-bottom: 10px">Email:  info@diebierstube.de</p>
              <iframe src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2660.3796491293488!2d11.550360577506725!3d48.180036248202754!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x479e76889d9b1aa7%3A0x56b07aac624a5b26!2sDie%20Bierstube!5e0!3m2!1sen!2sde!4v1697544858637!5m2!1sen!2sde" width="400" height="300" style="border:0;" loading="lazy" referrerpolicy="no-referrer-when-downgrade"></iframe>          </div>
      </div>
      <!-- Impressum and Datenschutz links -->
      <div class="links-section">
          <a href="https://www.oly-dorf.de/impressum/" target="_blank">Impressum</a>
          |
          <a href="https://www.oly-dorf.de/datenschutz/" target="_blank">Datenschutz</a>
      </div>
  </div>
</template>

<script lang="ts">
import * as api from "@/api";
import { defineComponent } from "vue";
import { LOADING_RESTAURANT } from "@/model";
import { timeToLocale, weekdayToLocale } from "@/time";

export default defineComponent({
  data() {
    return {
      restaurant: LOADING_RESTAURANT,
    };
  },
  async mounted() {
    this.restaurant = await api.restaurant();

  },
  methods: {
    weekdayToLocale: weekdayToLocale,
    timeToLocale: timeToLocale,
  },
});
</script>

<style scoped>
.sideBySide {
    float: left;
    width: 50%;
    background-color: #e7ddb5;
}
</style>