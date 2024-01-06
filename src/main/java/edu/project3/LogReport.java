package edu.project3;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class LogReport {

    private final String root = Paths.get(".").toString();
    private final Pattern format =
        Pattern.compile("(\\d{1,3}\\.){3}\\d{1,3} - - \\[(.*?)] \"(.*?)\" (\\d{3}) (\\d*) \".*?\" \".*?\"");
    private String name;
    private int requestCount;
    private int averageResponseSize;

    private int summaryResponseSize;
    private Map<String, Integer> resources = new HashMap<>();
    private Map<Integer, Integer> responses = new HashMap<>();

    private LocalDate from;
    private LocalDate to;
    List<String> fileNames;

    public LogReport(List<String> fileNames, Stream<LogRecord> records, LocalDate from, LocalDate to) {
        this.fileNames = fileNames;

        this.from = from;
        this.to = to;

        records.forEach((rec) -> {

            if ((from == null || !rec.getDate().isBefore(from)) && (to == null || !rec.getDate().isAfter(to))) {

                requestCount++;

                int response = rec.getResponse();
                if (!responses.containsKey(response)) {
                    responses.put(response, 1);
                } else {
                    responses.put(response, responses.get(response) + 1);
                }

                summaryResponseSize += rec.getSize();

                String resource = rec.getResource();

                if (!resources.containsKey(resource)) {
                    resources.put(resource, 1);
                } else {
                    resources.put(resource, resources.get(resource) + 1);
                }

            }

        });

        if (requestCount == 0) {
            averageResponseSize = 0;
        } else {
            averageResponseSize = summaryResponseSize / requestCount;
        }
    }

    @SuppressWarnings("MultipleStringLiterals")
    void printToFile(String name, String format) {
        String fileName;
        StringBuilder reportStringBuilder = new StringBuilder();

        switch (format) {
            case "adoc" -> {
                fileName = name + ".adoc";

                reportStringBuilder
                    .append("==== Общая информация\n")
                    .append("[cols=2]\n")
                    .append("|===\n")
                    .append("|Метрика\n|Значение\n");

                for (int i = 0; i < fileNames.size(); i++) {
                    reportStringBuilder.append("|Файл ")
                        .append(i + 1).append("\n|")
                        .append(fileNames.get(i))
                        .append("\n");
                }
                    reportStringBuilder.append("|Начальная дата\n|").append((from == null) ? "-" : from).append("\n")
                    .append("|Конечная дата\n|").append((to == null) ? "-" : to).append("\n")
                    .append("|Количество запросов\n|").append(requestCount).append("\n")
                    .append("|Средний размер ответа\n|").append(averageResponseSize).append("\n")
                    .append("|===\n")

                    .append("==== Запрашиваемые ресурсы\n")
                    .append("[cols=2]\n")
                    .append("|===\n")
                    .append("|Ресурс\n|Количество\n");
                for (var resource : resources.entrySet()) {
                    reportStringBuilder.append("|`")
                        .append(resource.getKey())
                        .append("`\n|")
                        .append(resource.getValue())
                        .append("\n");
                }

                reportStringBuilder
                    .append("|===\n")
                    .append("==== Коды ответа\n")
                    .append("[cols=2]\n")
                    .append("|===\n")
                    .append("|Код\n|Количество\n");
                for (var response : responses.entrySet()) {
                    reportStringBuilder.append("|`")
                        .append(response.getKey())
                        .append("`\n|")
                        .append(response.getValue())
                        .append("\n");
                }
                reportStringBuilder.append("|===\n");
            }
            case "md" -> {
                fileName = name + ".md";
                reportStringBuilder
                    .append("#### Общая информация\n")
                    .append("|Метрика|Значение|\n")
                    .append("|:-:|-:|\n");

                for (int i = 0; i < fileNames.size(); i++) {
                    reportStringBuilder
                        .append("|Файл ")
                        .append(i + 1).append("|")
                        .append(fileNames.get(i))
                        .append("|\n");
                }

                reportStringBuilder.append("|Начальная дата|").append((from == null) ? "-" : from).append("|\n")
                    .append("|Конечная дата|").append((to == null) ? "-" : to).append("|\n")
                    .append("|Количество запросов|").append(requestCount).append("|\n")
                    .append("|Средний размер ответа|").append(averageResponseSize).append("|\n")

                    .append("#### Запрашиваемые ресурсы\n")
                    .append("|Ресурс|Количество|\n")
                    .append("|:-:|:-:|\n");
                for (var resource : resources.entrySet()) {
                    reportStringBuilder.append("|`")
                        .append(resource.getKey())
                        .append("`|")
                        .append(resource.getValue())
                        .append("|\n");
                }

                reportStringBuilder
                    .append("#### Коды ответа\n")
                    .append("|Код|Количество|\n")
                    .append("|:-:|:-:|\n");
                for (var response : responses.entrySet()) {
                    reportStringBuilder.append("|`")
                        .append(response.getKey())
                        .append("`|")
                        .append(response.getValue())
                        .append("|\n");
                }

            }
            default -> throw new IllegalArgumentException();
        }

        try (FileChannel channel = new RandomAccessFile(root + "/" + fileName, "rw").getChannel()) {
            ByteBuffer buf = ByteBuffer.wrap(reportStringBuilder.toString().getBytes(StandardCharsets.UTF_8));
            channel.write(buf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
