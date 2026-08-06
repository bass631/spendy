package dev.bass631.spendy.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

public final class PeriodResolver {

    private PeriodResolver() {
    }

    public static LocalDateTime resolveFrom(String period, LocalDate from) {
        if ("custom".equals(period) && from != null) {
            return from.atStartOfDay();
        }
        return switch (period) {
            case "day" -> LocalDate.now().atStartOfDay();
            case "week" -> LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay();
            case "month" -> LocalDate.now().withDayOfMonth(1).atStartOfDay();
            case "year" -> LocalDate.now().withDayOfYear(1).atStartOfDay();
            default -> LocalDateTime.of(1970, 1, 1, 0, 0);
        };
    }

    public static LocalDateTime resolveTo(String period, LocalDate to) {
        if ("custom".equals(period) && to != null) {
            return to.atTime(LocalTime.MAX);
        }
        return LocalDateTime.now();
    }
}
