import type {
  Reservation,
  ReservationRequest,
  Restaurant,
} from "@/model";

//const ENDPOINT_BASE = "https://reservation.olydorf.com/api" ;
const ENDPOINT_BASE = "http://localhost:8080/api" ;


const cache: Map<string, unknown> = new Map();
export function get<T>(ep: string, cached = false): Promise<T> {
  if (cached) {
    const v = cache.get(ep);
    if (v !== null && v !== undefined) {
      return Promise.resolve(v as T);
    }
  }

  let p = fetch(ENDPOINT_BASE + ep)
    .then((res) => res.json())
    .then((res) => res as T);
  if (cached) {
    p = p.then((res) => {
      cache.set(ep, res);
      return res;
    });
  }
  return p;
}





export function reserve(req: ReservationRequest): Promise<Response> {
  return fetch(ENDPOINT_BASE + "/reservations", {
    method: "POST",
    body: JSON.stringify(req),
    headers: { "content-type": "application/json" },
  });


}

export async function sendReservationConfirmationEmail(

  name: string,
  email: string
): Promise<void> {
  const body = {
    recipientName: name,
    recipientEmail: email,
  };

  await fetch(ENDPOINT_BASE + "/send-confirmation", {
    method: "POST",
    body: JSON.stringify(body),
    headers: { "content-type": "application/json" },
  });
}



export function reservation(id: number): Promise<Reservation> {
  return get("/reservations/" + id);
}

export function reservations(): Promise<Array<Reservation>> {
  return get("/reservations");
}

export function restaurant(): Promise<Restaurant>{
  return get("/restaurant");
}


export function freeTablesAt(
  date: Date
): Promise<number[]> {
  return get(`/tables/freeAt/${date.toISOString()}`);
}
