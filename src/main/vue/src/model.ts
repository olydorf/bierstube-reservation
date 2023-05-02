export interface Restaurant {
  id: number;
  name: string;
  description: string;
  pictures: Array<string>;
  cuisine: string;
  priceCategory: string;
  website: string;
  openingHours: Array<OpenHourSlot>;
  tables: Array<Table>;
  layoutSvg: string;
}

export const LOADING_RESTAURANT: Restaurant = {
  cuisine: "",
  description: "",
  id: 0,
  name: "",
  pictures: [],
  priceCategory: "",
  website: "",
  openingHours: [],
  tables: [],
  layoutSvg: "",
};

export interface Address {
  streetName: string;
  streetNumber: number;
  postCode: number;
}

export interface Table {
  tableNumber: number;
}

export interface Filter {
  name: string | null;
  minRating: number | null;
  minPrice: string | null;
  maxPrice: string | null;
  cuisines: Array<string>;
}

export interface ReservationRequest {
  restaurant: number;
  startTime: string;
  endTime: string;
  table: number;
}

export interface Reservation {
  id: number;
  restaurant: Restaurant;
  startTime: string;
  endTime: string;
  table: Table;
}

export const LOADING_RESERVATION: Reservation = {
  id: 0,
  endTime: "",
  startTime: "",
  restaurant: LOADING_RESTAURANT,
  table: { tableNumber: 0 },
};

export interface User {
  id: number;
  name: string;
  email: string;
  phone: string;
}

export interface Review {
  restaurant: Restaurant;
  comment: string;
  author: User;
  rating: number;
}

export interface OpenHourSlot {
  weekDay: string;
  startTime: string;
  endTime: string;
}

export const PRICE_CATEGORIES = ["Cheap", "Normal", "Expensive"];
export const PRICE_CATEGORY_NAMES = {
  "Cheap": "€",
  "Normal": "€€",
  "Expensive": "€€€",
}