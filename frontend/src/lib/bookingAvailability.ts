import type { SkillDto } from "../api/skills";

export const BOOKING_HORIZON_DAYS = 365;

function pad2(v: number): string {
  return String(v).padStart(2, "0");
}

function toMinutes(hhmm: string): number {
  const [h, m] = hhmm.split(":").map(Number);
  return (h || 0) * 60 + (m || 0);
}

/** Skill sayfası ile aynı: müsait aralıkta 30 dk slotlar */
export function buildHalfHourSlots(from: string, until: string): string[] {
  const out: string[] = [];
  let cur = toMinutes(from);
  const end = toMinutes(until);
  while (cur < end) {
    const h = Math.floor(cur / 60);
    const m = cur % 60;
    out.push(`${pad2(h)}:${pad2(m)}`);
    cur += 30;
  }
  return out;
}

export function dateToYmd(d: Date): string {
  return `${d.getFullYear()}-${pad2(d.getMonth() + 1)}-${pad2(d.getDate())}`;
}

export function hasSkillAvailabilityConstraints(
  skill: SkillDto | null | undefined,
): boolean {
  return Boolean(
    skill?.availableDays?.length &&
      skill?.availableFrom &&
      skill?.availableUntil,
  );
}

export type BookingDateOption = { value: string; label: string };

/** İlk book / öğrenci karşı teklifi: seçilebilir günler (en az 1 saat sonrası slot ile). */
export function buildSkillDateOptions(
  skill: SkillDto,
  locale: string,
  horizonDays: number,
): BookingDateOption[] {
  const days = skill.availableDays ?? [];
  const from = skill.availableFrom;
  const until = skill.availableUntil;
  if (!days.length || !from || !until) return [];

  const allowedDays = new Set(days);
  const dayKeys = [
    "SUNDAY",
    "MONDAY",
    "TUESDAY",
    "WEDNESDAY",
    "THURSDAY",
    "FRIDAY",
    "SATURDAY",
  ];
  const baseSlots = buildHalfHourSlots(from, until);
  const now = new Date();
  const minMs = now.getTime() + 60 * 60 * 1000;
  const options: BookingDateOption[] = [];

  for (let i = 0; i < horizonDays; i++) {
    const d = new Date(now);
    d.setHours(0, 0, 0, 0);
    d.setDate(d.getDate() + i);
    const dayCode = dayKeys[d.getDay()];
    if (!allowedDays.has(dayCode)) continue;
    const ymd = dateToYmd(d);
    const validSlotExists = baseSlots.some((slot) => {
      const candidate = new Date(`${ymd}T${slot}:00`);
      return candidate.getTime() >= minMs;
    });
    if (!validSlotExists) continue;
    options.push({
      value: ymd,
      label: d.toLocaleDateString(locale === "tr" ? "tr-TR" : "en-US", {
        weekday: "short",
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
      }),
    });
  }
  return options;
}

export function buildSkillTimeOptionsForDate(
  skill: SkillDto,
  bookDateYmd: string,
): string[] {
  const from = skill.availableFrom;
  const until = skill.availableUntil;
  if (!from || !until || !bookDateYmd) return [];
  const minMs = Date.now() + 60 * 60 * 1000;
  return buildHalfHourSlots(from, until).filter((slot) => {
    const candidate = new Date(`${bookDateYmd}T${slot}:00`);
    return candidate.getTime() >= minMs;
  });
}

export function isWithinSkillAvailability(
  skill: SkillDto,
  dateStr: string,
  timeStr: string,
): boolean {
  const days = skill.availableDays ?? [];
  const from = skill.availableFrom;
  const until = skill.availableUntil;
  if (!days.length || !from || !until) return true;
  const dayKeys = [
    "SUNDAY",
    "MONDAY",
    "TUESDAY",
    "WEDNESDAY",
    "THURSDAY",
    "FRIDAY",
    "SATURDAY",
  ];
  const [y, m, d] = dateStr.split("-").map(Number);
  const weekday = dayKeys[new Date(y, m - 1, d).getDay()];
  if (!days.includes(weekday)) return false;
  const t = toMinutes(timeStr);
  return t >= toMinutes(from) && t < toMinutes(until);
}

/** Ortak dokümantasyon / import takma adları (SkillDetail, Messages hook). */
export { buildSkillDateOptions as getAvailableDatesForSkill };
export { buildSkillTimeOptionsForDate as getAvailableTimeSlotsForSkillDate };

export function isDateAvailableForSkillBooking(
  skill: SkillDto,
  ymd: string,
  locale: string,
  horizonDays: number,
): boolean {
  if (!hasSkillAvailabilityConstraints(skill)) return false;
  return buildSkillDateOptions(skill, locale, horizonDays).some(
    (o) => o.value === ymd,
  );
}

export function isTimeSlotAvailableForSkillBooking(
  skill: SkillDto,
  dateYmd: string,
  timeHhmm: string,
): boolean {
  return isWithinSkillAvailability(skill, dateYmd, timeHhmm);
}
