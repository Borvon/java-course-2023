package edu.project3;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class LogAnalyzerTest {

    final String ROOT_PATH = Paths.get(".").toString();

    @Test
    @DisplayName("Log Analyzer test")
    void logAnalyzerTest() {
        List<Path> paths =
            List.of(
                Paths.get(ROOT_PATH, "testLog 1.txt"),
                Paths.get(ROOT_PATH, "testLog 2.txt"),
                Paths.get(ROOT_PATH, "testLog 3.txt")
            );

        List<String> expectedReports = List.of(
            "#### Общая информация\n" +
                "|Метрика|Значение|\n" +
                "|:-:|-:|\n" +
                "|Файл 1|testLog 1.txt|\n" +
                "|Файл 2|testLog 2.txt|\n" +
                "|Файл 3|testLog 3.txt|\n" +
                "|Начальная дата|-|\n" +
                "|Конечная дата|-|\n" +
                "|Количество запросов|11|\n" +
                "|Средний размер ответа|242|\n" +
                "#### Запрашиваемые ресурсы\n" +
                "|Ресурс|Количество|\n" +
                "|:-:|:-:|\n" +
                "|`product_2`|5|\n" +
                "|`product_1`|6|\n" +
                "#### Коды ответа\n" +
                "|Код|Количество|\n" +
                "|:-:|:-:|\n" +
                "|`304`|4|\n" +
                "|`404`|5|\n" +
                "|`200`|2|\n",

            "==== Общая информация\n" +
                "[cols=2]\n" +
                "|===\n" +
                "|Метрика\n" +
                "|Значение\n" +
                "|Файл 1\n" +
                "|testLog 1.txt\n" +
                "|Файл 2\n" +
                "|testLog 2.txt\n" +
                "|Файл 3\n" +
                "|testLog 3.txt\n" +
                "|Начальная дата\n" +
                "|-\n" +
                "|Конечная дата\n" +
                "|-\n" +
                "|Количество запросов\n" +
                "|11\n" +
                "|Средний размер ответа\n" +
                "|242\n" +
                "|===\n" +
                "==== Запрашиваемые ресурсы\n" +
                "[cols=2]\n" +
                "|===\n" +
                "|Ресурс\n" +
                "|Количество\n" +
                "|`product_2`\n" +
                "|5\n" +
                "|`product_1`\n" +
                "|6\n" +
                "|===\n" +
                "==== Коды ответа\n" +
                "[cols=2]\n" +
                "|===\n" +
                "|Код\n" +
                "|Количество\n" +
                "|`304`\n" +
                "|4\n" +
                "|`404`\n" +
                "|5\n" +
                "|`200`\n" +
                "|2\n" +
                "|===\n",

            "#### Общая информация\n" +
                "|Метрика|Значение|\n" +
                "|:-:|-:|\n" +
                "|Файл 1|testLog 1.txt|\n" +
                "|Файл 2|testLog 2.txt|\n" +
                "|Файл 3|testLog 3.txt|\n" +
                "|Начальная дата|2015-05-24|\n" +
                "|Конечная дата|2015-05-30|\n" +
                "|Количество запросов|6|\n" +
                "|Средний размер ответа|280|\n" +
                "#### Запрашиваемые ресурсы\n" +
                "|Ресурс|Количество|\n" +
                "|:-:|:-:|\n" +
                "|`product_2`|4|\n" +
                "|`product_1`|2|\n" +
                "#### Коды ответа\n" +
                "|Код|Количество|\n" +
                "|:-:|:-:|\n" +
                "|`304`|1|\n" +
                "|`404`|5|\n",

            "==== Общая информация\n" +
                "[cols=2]\n" +
                "|===\n" +
                "|Метрика\n" +
                "|Значение\n" +
                "|Файл 1\n" +
                "|testLog 1.txt\n" +
                "|Файл 2\n" +
                "|testLog 2.txt\n" +
                "|Файл 3\n" +
                "|testLog 3.txt\n" +
                "|Начальная дата\n" +
                "|2015-05-24\n" +
                "|Конечная дата\n" +
                "|2015-05-30\n" +
                "|Количество запросов\n" +
                "|6\n" +
                "|Средний размер ответа\n" +
                "|280\n" +
                "|===\n" +
                "==== Запрашиваемые ресурсы\n" +
                "[cols=2]\n" +
                "|===\n" +
                "|Ресурс\n" +
                "|Количество\n" +
                "|`product_2`\n" +
                "|4\n" +
                "|`product_1`\n" +
                "|2\n" +
                "|===\n" +
                "==== Коды ответа\n" +
                "[cols=2]\n" +
                "|===\n" +
                "|Код\n" +
                "|Количество\n" +
                "|`304`\n" +
                "|1\n" +
                "|`404`\n" +
                "|5\n" +
                "|===\n"
        );

        try (FileChannel channel = new RandomAccessFile(paths.get(0).toString(), "rw").getChannel()) {
            String testLogs = """
                93.180.71.3 - - [17/May/2015:08:05:32 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)\""
                "93.180.71.3 - - [17/May/2015:08:05:23 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)\""
                "80.91.33.133 - - [17/May/2015:08:05:24 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.17)\""
                "217.168.17.5 - - [17/May/2015:08:05:34 +0000] \"GET /downloads/product_1 HTTP/1.1\" 200 490 \"-\" \"Debian APT-HTTP/1.3 (0.8.10.3)\""
                "217.168.17.5 - - [17/May/2015:08:05:09 +0000] \"GET /downloads/product_2 HTTP/1.1\" 200 490 \"-\" \"Debian APT-HTTP/1.3 (0.8.10.3)\"""";
            ByteBuffer buf = ByteBuffer.wrap(testLogs.getBytes(StandardCharsets.UTF_8));
            channel.write(buf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (FileChannel channel = new RandomAccessFile(paths.get(1).toString(), "rw").getChannel()) {
            String testLogs = """
                194.132.177.109 - - [24/May/2015:10:05:18 +0000] "GET /downloads/product_2 HTTP/1.1" 404 337 "-" "Debian APT-HTTP/1.3 (1.0.1ubuntu2)"
                180.179.174.219 - - [24/May/2015:10:05:30 +0000] "GET /downloads/product_2 HTTP/1.1" 304 0 "-" "Debian APT-HTTP/1.3 (0.9.7.9)"
                194.132.177.109 - - [24/May/2015:10:05:18 +0000] "GET /downloads/product_2 HTTP/1.1" 404 339 "-" "Debian APT-HTTP/1.3 (1.0.1ubuntu2)"
                194.132.177.109 - - [24/May/2015:10:05:46 +0000] "GET /downloads/product_2 HTTP/1.1" 404 336 "-" "Debian APT-HTTP/1.3 (1.0.1ubuntu2)\"""";
            ByteBuffer buf = ByteBuffer.wrap(testLogs.getBytes(StandardCharsets.UTF_8));
            channel.write(buf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (FileChannel channel = new RandomAccessFile(paths.get(2).toString(), "rw").getChannel()) {
            String testLogs = """
                54.84.191.5 - - [30/May/2015:11:05:32 +0000] "GET /downloads/product_1 HTTP/1.1" 404 337 "-" "Debian APT-HTTP/1.3 (1.0.1ubuntu2)"
                54.84.191.5 - - [30/May/2015:11:05:50 +0000] "GET /downloads/product_1 HTTP/1.1" 404 336 "-" "Debian APT-HTTP/1.3 (1.0.1ubuntu2)\"""";
            ByteBuffer buf = ByteBuffer.wrap(testLogs.getBytes(StandardCharsets.UTF_8));
            channel.write(buf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        LogAnalyzer firstAnalyzer = new LogAnalyzer();
        LogAnalyzer secondAnalyzer = new LogAnalyzer();

        LogReport reportWithoutDates = firstAnalyzer.makeReport(firstAnalyzer.input(paths, null), null, null);
        LogReport reportWithDates = secondAnalyzer.makeReport(secondAnalyzer.input(paths, null),
            LocalDate.of(2015, 5, 24),
            LocalDate.of(2015, 5, 30)
        );

        reportWithoutDates.printToFile("report 1", "md");
        reportWithoutDates.printToFile("report 1", "adoc");

        reportWithDates.printToFile("report 2", "md");
        reportWithDates.printToFile("report 2", "adoc");

        try (FileChannel channel = new RandomAccessFile(
            Paths.get(ROOT_PATH, "report 1.md").toString(),
            "r"
        ).getChannel()) {
            ByteBuffer buf = ByteBuffer.allocate(Math.toIntExact(channel.size()));
            channel.read(buf);
            assertThat(new String(buf.array())).isEqualTo(expectedReports.get(0));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (FileChannel channel = new RandomAccessFile(
            Paths.get(ROOT_PATH, "report 1.adoc").toString(),
            "r"
        ).getChannel()) {
            ByteBuffer buf = ByteBuffer.allocate(Math.toIntExact(channel.size()));
            channel.read(buf);
            assertThat(new String(buf.array())).isEqualTo(expectedReports.get(1));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (FileChannel channel = new RandomAccessFile(
            Paths.get(ROOT_PATH, "report 2.md").toString(),
            "r"
        ).getChannel()) {
            ByteBuffer buf = ByteBuffer.allocate(Math.toIntExact(channel.size()));
            channel.read(buf);
            assertThat(new String(buf.array())).isEqualTo(expectedReports.get(2));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (FileChannel channel = new RandomAccessFile(
            Paths.get(ROOT_PATH, "report 2.adoc").toString(),
            "r"
        ).getChannel()) {
            ByteBuffer buf = ByteBuffer.allocate(Math.toIntExact(channel.size()));
            channel.read(buf);
            assertThat(new String(buf.array())).isEqualTo(expectedReports.get(3));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
