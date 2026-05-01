import { useEffect, useMemo, useState } from "react";
import { fetchSkillById, type SkillDto } from "../api/skills";
import {
  BOOKING_HORIZON_DAYS,
  buildSkillDateOptions,
  buildSkillTimeOptionsForDate,
  hasSkillAvailabilityConstraints,
} from "../lib/bookingAvailability";

export type UseBookingAvailabilityFromSkillParams = {
  /** Rezervasyon yapılan beceri (eğitmen = skill.owner; müsaitlik skill üzerinde). */
  skillId: string | null;
  enabled: boolean;
  locale: string;
  selectedDateYmd: string;
};

/**
 * SkillDetail ilk book ile Messages refuse-and-offer için ortak: GET /api/skills/:id ile
 * eğitmenin kayıtlı müsaitlik aralığına göre tarih/saat seçenekleri.
 */
export function useBookingAvailabilityFromSkill({
  skillId,
  enabled,
  locale,
  selectedDateYmd,
}: UseBookingAvailabilityFromSkillParams) {
  const [skill, setSkill] = useState<SkillDto | null>(null);
  const [ready, setReady] = useState(true);

  useEffect(() => {
    if (!enabled || !skillId) {
      queueMicrotask(() => {
        setSkill(null);
        setReady(true);
      });
      return;
    }
    let cancelled = false;
    queueMicrotask(() => {
      if (!cancelled) setReady(false);
    });
    fetchSkillById(skillId)
      .then((s) => {
        if (!cancelled) setSkill(s);
      })
      .catch(() => {
        if (!cancelled) setSkill(null);
      })
      .finally(() => {
        if (!cancelled) setReady(true);
      });
    return () => {
      cancelled = true;
    };
  }, [enabled, skillId]);

  const hasConstraints = useMemo(
    () => hasSkillAvailabilityConstraints(skill),
    [skill],
  );

  const bookingDateOptions = useMemo(() => {
    if (!skill || !hasConstraints) return [];
    return buildSkillDateOptions(skill, locale, BOOKING_HORIZON_DAYS);
  }, [skill, hasConstraints, locale]);

  const bookableYmdSet = useMemo(
    () => new Set(bookingDateOptions.map((o) => o.value)),
    [bookingDateOptions],
  );

  const bookingTimeOptions = useMemo(() => {
    if (!skill || !hasConstraints || !selectedDateYmd) return [];
    return buildSkillTimeOptionsForDate(skill, selectedDateYmd);
  }, [skill, hasConstraints, selectedDateYmd]);

  const calendarMonthBounds = useMemo(() => {
    const start = new Date();
    start.setDate(start.getDate() - 1);
    start.setHours(0, 0, 0, 0);
    const end = new Date(start);
    end.setDate(end.getDate() + BOOKING_HORIZON_DAYS);
    return {
      startMonth: new Date(start.getFullYear(), start.getMonth(), 1),
      endMonth: new Date(end.getFullYear(), end.getMonth(), 1),
    };
  }, []);

  return {
    skill,
    ready,
    hasConstraints,
    bookingDateOptions,
    bookingTimeOptions,
    bookableYmdSet,
    calendarMonthBounds,
  };
}

/** İstenen takma ad (instructor = skill owner). */
export { useBookingAvailabilityFromSkill as useInstructorSkillAvailability };
