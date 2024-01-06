package edu.project3;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class LogAnalyzer {

    List<String> fileNames = new ArrayList<>();

    public Stream<LogRecord> input(List<Path> names, List<String> urls) {

        ArrayList<LogRecord> records = new ArrayList<>();

        if (names != null) {
            for (var name : names) {

                fileNames.add(name.getFileName().toString());

                try (Stream<String> lines = Files.lines(name)) {
                    lines.forEach((string) ->
                        records.add(new LogRecord(string))
                    );
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (urls != null) {
            for (var url : urls) {
                HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();

                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
                    .build();

                try {
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    Stream<String> lines = Arrays.stream(response.body().split("\n"));

                    lines.forEach((string) ->
                        records.add(new LogRecord(string))
                    );

                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        }

        return records.stream();
    }

    public LogReport makeReport(Stream<LogRecord> records, LocalDate from, LocalDate to) {
        return new LogReport(fileNames, records, from, to);
    }
}
