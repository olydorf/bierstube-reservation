import { createRouter, createWebHistory } from "vue-router";
import SearchView from "@/views/SearchView.vue";
import RestaurantDetailsView from "@/views/RestaurantDetailsView.vue";
import CreateReservationView from "@/views/CreateReservationView.vue";
import ReservationDetailsView from "@/views/ReservationDetailsView.vue";
import NotFoundView from "@/views/NotFoundView.vue";
import ReservationListView from "@/views/ReservationListView.vue";

// noinspection TypeScriptValidateTypes
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      redirect: "/search",
    },
    {
      path: "/search",
      name: "search",
      component: SearchView,
    },
    {
      path: "/restaurants/:id",
      name: "restaurant",
      component: RestaurantDetailsView,
    },
    {
      path: "/restaurants/:id/reserve",
      name: "reserve",
      component: CreateReservationView,
    },
    {
      path: "/reservations",
      name: "reservations",
      component: ReservationListView,
    },
    {
      path: "/reservations/:id",
      name: "reservation",
      component: ReservationDetailsView,
    },
    {
      path: "/:path(.*)*",
      name: "notFound",
      component: NotFoundView,
    },
  ],
});

export default router;
