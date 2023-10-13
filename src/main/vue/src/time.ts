import type { OpenHourSlot } from "@/model";

export const MILLISECONDS = 1;
export const SECONDS = 1000 * MILLISECONDS;
export const MINUTES = 60 * SECONDS;
export const HOURS = 60 * MINUTES;
export const DAYS = 24 * HOURS;
export const WEEKS = 7 * DAYS;

export function weekdayToLocale(d: string): string {
  let o = 0;
  switch (d) {
    case "MONDAY":
      o = 0;
      break;
    case "TUESDAY":
      o = 1;
      break;
    case "WEDNESDAY":
      o = 2;
      break;
    case "THURSDAY":
      o = 3;
      break;
    case "FRIDAY":
      o = 4;
      break;
    case "SATURDAY":
      o = 5;
      break;
    case "SUNDAY":
      o = 6;
      break;
  }
  return new Date(2022, 6, 4 + o, 1).toLocaleString([], { weekday: "long" });
}

export function timeToDate(t: string, day = "2022-06-04"): Date {
  return new Date(day + "T" + t);
}

export function timeToLocale(t: string | Date): string {
  if (!(t instanceof Date)) t = timeToDate(t as string);
  return t.toLocaleTimeString([], {
    // @ts-ignore
    timeStyle: "short",
  });
}

export function dateToLocale(d: Date): string {
  return new Date(d).toLocaleDateString([], {
    // @ts-ignore
    dateStyle: "full",
  });
}

export function nextOccurrenceOfWeekDay(weekDay: string): Date | null {
  const now = new Date();
  for (let i = 0; i <= 7; i++) {
    switch (weekDay) {
      case "SUNDAY":
        if (now.getDay() == 0) return now;
        break;
      case "MONDAY":
        if (now.getDay() == 1) return now;
        break;
      case "TUESDAY":
        if (now.getDay() == 2) return now;
        break;
      case "WEDNESDAY":
        if (now.getDay() == 3) return now;
        break;
      case "THURSDAY":
        if (now.getDay() == 4) return now;
        break;
      case "FRIDAY":
        if (now.getDay() == 5) return now;
        break;
      case "SATURDAY":
        if (now.getDay() == 6) return now;
        break;
      default:
        return null;
    }
    now.setTime(now.getTime() + DAYS);
  }
  return now;
}

const RESERVATION_GRANULARITY = 30 * MINUTES;
export const RESERVATION_DURATION = 90 * MINUTES;

export function timeSlots(
  openingHours: OpenHourSlot[],
  date: Date,
  weekDay: string | null
): Date[][] {
  const day = date.toISOString().split("T")[0];
  const slots = [];
  const boundaryTime = timeToDate('20:31', day).getTime();
  for (const oh of openingHours) {
    if (oh.weekDay != weekDay) continue;

    const endTime = timeToDate(oh.endTime, day).getTime();
    let start = timeToDate(oh.startTime, day);
    while (start.getTime() < boundaryTime) {
      slots.push([start, new Date(start.getTime() + RESERVATION_DURATION)]);
      start = new Date(start.getTime() + RESERVATION_GRANULARITY);
    }
  }
  return slots;
}
