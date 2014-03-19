package solrTest;

import javafx.util.StringConverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


/**
 * User: hansolo
 * Date: 16.12.13
 * Time: 15:05
 */
public class LocalDateTimeStringConverter extends StringConverter<LocalDateTime> {
    protected final Locale            locale;
    protected final String            pattern;
    protected final DateTimeFormatter dateTimeFormat;

    public LocalDateTimeStringConverter() {
        this(Locale.getDefault());
    }
    public LocalDateTimeStringConverter(Locale locale) {
        this(locale, null);
    }
    public LocalDateTimeStringConverter(String pattern) {
        this(Locale.getDefault(), pattern, null);
    }
    public LocalDateTimeStringConverter(Locale locale, String pattern) {
        this(locale, pattern, null);
    }
    public LocalDateTimeStringConverter(DateTimeFormatter dateTimeFormat) {
        this(null, null, dateTimeFormat);
    }
    LocalDateTimeStringConverter(Locale locale, String pattern, DateTimeFormatter dateTimeFormat) {
        this.locale = locale;
        this.pattern = pattern;
        this.dateTimeFormat = dateTimeFormat;
    }


    @Override public LocalDateTime fromString(String value) {
        // If the specified value is null or zero-length, return null
        if (value == null) {
            return (null);
        }

        value = value.trim();

        if (value.length() < 1) {
            return (null);
        }

        // Create and configure the parser to be used
        DateTimeFormatter parser = getDateTimeFormat();

        // Perform the requested parsing
        return LocalDateTime.from(parser.parse(value));
    }

    @Override public String toString(LocalDateTime value) {
        // If the specified value is null, return a zero-length String
        if (value == null) {
            return "";
        }

        // Create and configure the formatter to be used
        DateTimeFormatter formatter = getDateTimeFormat();

        // Perform the requested formatting
        return formatter.format(value);
    }

    protected DateTimeFormatter getDateTimeFormat() {
        Locale _locale = locale == null ? Locale.getDefault() : locale;

        DateTimeFormatter df;
        if (dateTimeFormat != null) {
            return dateTimeFormat;
        } else if (pattern != null) {
            df = DateTimeFormatter.ofPattern(pattern, _locale);
        } else {
            df = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        }
        return df;
    }
}
