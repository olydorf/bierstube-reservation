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
  name : string
  email: string
  amountGuests:number
  startTime: string;
  endTime: string;
  message: string;
}

export interface Reservation {
  id: number;
  name : string
  email: string
  amountGuests:number
  startTime: string;
  endTime: string;
  restaurantTable: Table;
  message: string;
}


export const LOADING_RESERVATION: Reservation = {
  id: 0,
  name:"",
  email:"",
  amountGuests:0,
  endTime: "",
  startTime: "",
  restaurantTable:{ id: 0 },
  message: "",
};


export interface OpenHourSlot {
  weekDay: string;
  startTime: string;
  endTime: string;
}

;
