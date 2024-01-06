package edu.project3;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogRecord {

    private LocalDate date;
    private String resource;
    private int response;
    private int size;

    public LogRecord(String recordString) {
        final Pattern format =
            Pattern.compile("(\\d{1,3}\\.){3}\\d{1,3} - - \\[(.*?)] \"(.*?)\" (\\d{3}) (\\d*) \".*?\" \".*?\"");

        Matcher matcher = format.matcher(recordString);

        if (matcher.find()) {
            final int dateGroup = 2;
            String dateString = matcher.group(dateGroup);
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("dd/MMM/yyyy:HH:mm:ss Z")
                .toFormatter(Locale.ENGLISH);

            date = LocalDateTime.parse(dateString, formatter).toLocalDate();

            final int responseGroup = 4;
            response = Integer.parseInt(matcher.group(responseGroup));

            final int sizeGroup = 5;
            size = Integer.parseInt(matcher.group(sizeGroup));

            final int requestGroup = 3;
            String request = matcher.group(requestGroup).split(" ")[1];

            Matcher requestMatcher = Pattern.compile("/(?!.*/)(.*)").matcher(request);

            if (requestMatcher.find()) {
                resource = requestMatcher.group(1);
            }

        }
    }

    public int getResponse() {
        return response;
    }

    public int getSize() {
        return size;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getResource() {
        return resource;
    }
}
