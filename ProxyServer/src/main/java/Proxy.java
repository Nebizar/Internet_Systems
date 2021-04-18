import com.sun.net.httpserver.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.apache.commons.io.IOUtils;

public class Proxy {
    private String test;

    public static void main(String[] args) throws Exception {
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        RootHandler handler = new RootHandler();
        server.createContext("/", handler);
        System.out.println("Starting server on port: " + port);
        server.start();
    }

    static class RootHandler implements HttpHandler {

        //private String test;
        private List<String> blacklist;
        private Map<String, Statistics> stats;
        private String statsPath = "stats.csv";

        RootHandler() {
            this.blacklist = Collections.emptyList();
            try {
                this.blacklist = Files.readAllLines(Paths.get("blacklist.txt"), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stats = new TreeMap<String, Statistics>();
            try {
                BufferedReader csvReader = new BufferedReader(new FileReader(this.statsPath));
                String row = csvReader.readLine();
                while ((row = csvReader.readLine()) != null) {
                    String[] data = row.split(",");
                    try {
                        Statistics stat = new Statistics(data[0]);
                        stat.initExisting(Long.parseLong(data[1]), Long.parseLong(data[2]), Long.parseLong(data[3]));
                        this.stats.put(data[0], stat);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                csvReader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.out.println(this.stats);
        }

        public void handle(HttpExchange exchange) throws IOException {
            byte[] requestBody = null;
            byte[] responseBody = null;
            HttpURLConnection conn = null;
            String domain = exchange.getRequestURI().getHost();

            if (this.blacklist.contains(domain)){
                byte[] msg = "Domain forbidden".getBytes();
                exchange.sendResponseHeaders(403, msg.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(msg);
                }
                return;
            }

            try {

                conn = (HttpURLConnection) exchange.getRequestURI().toURL().openConnection();
                conn.setRequestMethod(exchange.getRequestMethod());
                conn.setInstanceFollowRedirects(false);
                //System.out.println("OK");
                for (var set: exchange.getRequestHeaders().entrySet()) {
                    for (var value: set.getValue()) {
                        conn.addRequestProperty(set.getKey(), value);
                    }
                }

                conn.setDoInput(true);

                try (var requestBodyStream = exchange.getRequestBody()) {
                    requestBody = IOUtils.toByteArray(requestBodyStream);
                    if (requestBody.length > 0){
                        conn.setDoOutput(true);
                        try (OutputStream os = conn.getOutputStream()) {
                            os.write(requestBody);
                        }
                    }
                }

                try {
                    responseBody = IOUtils.toByteArray(conn.getInputStream());
                } catch (Exception ex) {
                    responseBody = IOUtils.toByteArray(conn.getErrorStream());
                }

                saveRequest(domain, requestBody.length, responseBody.length);

            } catch (Throwable ex) {
                ex.printStackTrace();
            } finally {
                if (conn != null) {
                    var responseHead = exchange.getResponseHeaders();
                    for (var header: conn.getHeaderFields().entrySet()) {
                        if (header.getKey() != null && header.getValue() != null && !header.getKey().equalsIgnoreCase("Transfer-Encoding")) {
                            responseHead.put(header.getKey(), header.getValue());
                        }
                    }
                    responseHead.add("Via", InetAddress.getLocalHost().toString());
                    if (responseBody != null) {
                        exchange.sendResponseHeaders(conn.getResponseCode(),responseBody.length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(responseBody);
                        }
                    } else {
                        exchange.sendResponseHeaders(conn.getResponseCode(), -1);
                    }
                }
            }

        }

        private void saveStats(String filename) throws IOException {
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename),Charset.forName("UTF-8"))) {
                writer.write("Domain,RequestCount,RequestTotal,ResponseTotal\n");
                for (Statistics stat: this.stats.values()) {
                    writer.write(stat.getLine());
                    writer.newLine();
                }
            }
        }

        private void saveRequest(String domain, long requestSize, long responseSize) throws IOException {
            synchronized (this.stats) {
                Statistics stat = this.stats.get(domain);
                if (stat == null) {
                    stat = new Statistics(domain);
                    this.stats.put(domain, stat);
                }
                stat.saveRequest(requestSize, responseSize);
                this.saveStats(this.statsPath);

            }
        }
    }
}
