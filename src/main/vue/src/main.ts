import { createApp } from "vue";
import { createPinia } from "pinia";
import App from "./App.vue";
import router from "./router";

import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { library } from "@fortawesome/fontawesome-svg-core";
import {
  faArrowUpRightFromSquare,
  faCalendarPlus,
  faCircleCheck,
  faCircleLeft,
  faCircleRight,
  faPrint,
  faStar,
  faStarHalf,
} from "@fortawesome/free-solid-svg-icons";

const app = createApp(App);

library.add(
  faStar,
  faStarHalf,
  faCircleCheck,
  faArrowUpRightFromSquare,
  faCircleLeft,
  faCircleRight,
  faCalendarPlus,
  faPrint
);

app.use(createPinia());
app.use(router);
app.component("icon", FontAwesomeIcon);

app.mount("#app");
