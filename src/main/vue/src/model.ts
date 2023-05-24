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
  layoutSvg: "",
};


export interface Table {
  id: number;
}

export interface ReservationRequest {
  user: User;
  startTime: string;
  endTime: string;
  restaurantTable: Table;
}

export interface Reservation {
  id: number;
  startTime: string;
  endTime: string;
  restaurantTable: Table;
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
  user: LOADING_USER,
  restaurantTable:{ id: 0 },
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
