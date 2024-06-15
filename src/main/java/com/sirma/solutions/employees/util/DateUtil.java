package com.sirma.solutions.employees.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    private static final SimpleDateFormat[] DATE_FORMATS = {
            new SimpleDateFormat("yyyy-MM-dd"),
            new SimpleDateFormat("MM/dd/yyyy"),
            new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH),
            new SimpleDateFormat("dd/MM/yyyy"),
            new SimpleDateFormat("yyyy/MM/dd"),
            new SimpleDateFormat("MM-dd-yyyy"),
            new SimpleDateFormat("yyyyMMdd"),
            new SimpleDateFormat("MM.dd.yyyy"),
            new SimpleDateFormat("dd.MM.yyyy"),
            new SimpleDateFormat("yyyy.MM.dd"),
            new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH),
            new SimpleDateFormat("yyyy MMM dd", Locale.ENGLISH),
            new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH),
            new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH),
            new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    };


    public static Instant parseDate(String dateString) {
        for (SimpleDateFormat dateFormat : DATE_FORMATS) {
            try {
                Date date = dateFormat.parse(dateString);
                return date.toInstant();
            } catch (ParseException ignored) {
            }
        }
        throw new IllegalArgumentException(String.format("Unsupported format: %s", dateString));
    }

    public static Instant getMinInstant(Instant instant1, Instant instant2) {
        return instant1.isBefore(instant2) ? instant1 : instant2;
    }

    public static Instant getMaxInstant(Instant instant1, Instant instant2) {
        return instant1.isAfter(instant2) ? instant1 : instant2;
    }
}
