import type {
  Reservation,
  ReservationRequest,
  Restaurant,
  Review,
  User,
} from "@/model";

const ENDPOINT_BASE = "http://localhost:8080/api";

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

export function cuisines(): Promise<Array<string>> {
  return get("/cuisines", true);
}

export function restaurants(
  name: string,
  cuisines: string[],
  minPrice: string,
  maxPrice: string,
  minRating: number
): Promise<Array<Restaurant>> {
  return get(
    `/restaurants?name=${encodeURI(name)}&cuisines=${cuisines.join(
      ","
    )}&minPrice=${minPrice}&maxPrice=${maxPrice}&minRating=${minRating}`
  );
}

export function restaurant(id: number): Promise<Restaurant> {
  return get("/restaurants/" + id);
}

export function restaurantRating(id: number): Promise<number> {
  return get("/restaurants/" + id + "/reviews/avg");
}

export function reviews(id: number): Promise<Array<Review>> {
  return get("/restaurants/" + id + "/reviews");
}

export function reserve(req: ReservationRequest): Promise<Reservation> {
  return fetch(ENDPOINT_BASE + "/reservations", {
    method: "POST",
    body: JSON.stringify(req),
    headers: { "content-type": "application/json" },
  }).then((res) => res.json());


}

export async function sendReservationConfirmationEmail(

  name: string,
  email: string
): Promise<void> {
  const body = {
    recipientName: name,
    recipientEmail: email,
  };

  await fetch(ENDPOINT_BASE + "/emails/send-confirmation", {
    method: "POST",
    body: JSON.stringify(body),
    headers: { "content-type": "application/json" },
  });
}

export async function createUser(userData: {
  name: string;
  email: string;
  phone: string;
}) {
  try {
    const response = await fetch("/api/users", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(userData),
    });

    if (response.ok) {
      return await response.json();
    } else {
      throw new Error("Error creating user");
    }
  } catch (error) {
    console.error("Error in createUser():", error);
    throw error;
  }
}

export function reservation(id: number): Promise<Reservation> {
  return get("/reservations/" + id);
}

export function reservations(): Promise<Array<Reservation>> {
  return get("/reservations");
}

export function freeTablesAt(
  restaurant: number,
  date: Date
): Promise<number[]> {
  return get(`/restaurants/${restaurant}/tables/freeAt/${date.toISOString()}`);
}
