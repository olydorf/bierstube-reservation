export interface Restaurant {
  name: string;
  openingHours: Array<OpenHourSlot>;
  tables: Array<Table>;
  layoutSvg: string;
}

export const LOADING_RESTAURANT: Restaurant = {
  name: "",
  openingHours: [],
  tables: [],
  layoutSvg : "",
};


export interface Table {
  tableNumber: number;
}

export interface ReservationRequest {
  user: User;
  restaurant: Restaurant;
  startTime: string;
  endTime: string;
  table: Table;
}

export interface Reservation {
  id: number;
  restaurant: Restaurant;
  startTime: string;
  endTime: string;
  table: Table;
  user: User;
}

export const LOADING_USER: User = {
  name: "",
  email: "",
  phone: "",
};
export const LOADING_RESERVATION: Reservation = {
  id: 0,
  endTime: "",
  startTime: "",
  restaurant: LOADING_RESTAURANT,
  user: LOADING_USER,
  table:{ tableNumber: 0 },
};

export interface User {
  name: string;
  email: string;
  phone: string;
}



export interface OpenHourSlot {
  weekDay: string;
  startTime: string;
  endTime: string;
}

;
