package eist.aammn;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A helper to build RFC5545 .ics files.
 * @implSpec https://datatracker.ietf.org/doc/html/rfc5545
 */
public class ICSBuilder {
    public static final String MIME_TYPE = "text/calendar";
    private final StringBuilder s;
    private static final AtomicInteger uidCounter = new AtomicInteger();

    public ICSBuilder() {
        s = new StringBuilder();
        field("BEGIN", "VCALENDAR");
        field("VERSION", "2.0");
        field("PRODID", "AAMMN/Reservations");
    }

    /**
     * @implSpec https://datatracker.ietf.org/doc/html/rfc5545#section-3.6.1
     */
    public ICSBuilder event(LocalDateTime startTime, LocalDateTime endTime, String summary) {
        field("BEGIN", "VEVENT");
        field("DTSTAMP", LocalDateTime.now());
        field("UID", genUid());
        field("DTSTART", formatDateTime(startTime));
        field("DTEND", formatDateTime(endTime));
        field("SUMMARY", summary);
        field("END", "VEVENT");
        return this;
    }

    /**
     * @implSpec https://datatracker.ietf.org/doc/html/rfc5545#section-3.6.6
     */
    public ICSBuilder alarm(LocalDateTime time, String desc) {
        field("BEGIN", "VALARM");
        field("DTSTAMP", LocalDateTime.now());
        field("UID", genUid());
        field("ACTION", "DISPLAY");
        field("TRIGGER", "VALUE=DATE-TIME:" + formatDateTime(time));
        field("DESCRIPTION", desc);
        field("END", "VALARM");
        return this;
    }

    public String build() {
        return s + "END:VCALENDAR\r\n";
    }

    private void field(String k, String v) {
        s.append(k).append(":").append(v).append("\r\n");
    }

    private void field(String k, LocalDateTime v) {
        field(k, formatDateTime(v));
    }

    private void field(String k, LocalTime v) {
        field(k, formatTime(v));
    }

    /**
     * @implSpec https://datatracker.ietf.org/doc/html/rfc5545#section-3.8.4.7
     */
    private String genUid() {
        return formatDateTime(LocalDateTime.now()) + "-" + uidCounter.getAndIncrement() + "@reservations.aammn.eist";
    }

    /**
     * @implSpec https://datatracker.ietf.org/doc/html/rfc5545#section-3.3.4
     */
    private String formatDate(LocalDate d) {
        return d.format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    private static final DateTimeFormatter BASIC_ISO_TIME = new DateTimeFormatterBuilder()
                .appendValue(ChronoField.HOUR_OF_DAY, 2)
                .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
                .appendValue(ChronoField.SECOND_OF_MINUTE, 2).toFormatter();

    /**
     * @implSpec https://datatracker.ietf.org/doc/html/rfc5545#section-3.3.12
     */
    private String formatTime(LocalTime t) {
        return t.format(BASIC_ISO_TIME);
    }

    /**
     * @implSpec https://datatracker.ietf.org/doc/html/rfc5545#section-3.3.5
     */
    private String formatDateTime(LocalDateTime t) {
        return formatDate(t.toLocalDate()) + "T" + formatTime(t.toLocalTime());
    }
}
