package com.billbharat.sales.util;

import lombok.experimental.UtilityClass;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for date/time operations.
 */
@UtilityClass
public class DateUtil {

    public static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static LocalDateTime startOfDay(LocalDate date) {
        return date.atStartOfDay();
    }

    public static LocalDateTime endOfDay(LocalDate date) {
        return date.atTime(LocalTime.MAX);
    }

    public static LocalDateTime startOfMonth(int year, int month) {
        return LocalDate.of(year, month, 1).atStartOfDay();
    }

    public static LocalDateTime endOfMonth(int year, int month) {
        return YearMonth.of(year, month).atEndOfMonth().atTime(LocalTime.MAX);
    }

    public static LocalDate today() {
        return LocalDate.now();
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
}
