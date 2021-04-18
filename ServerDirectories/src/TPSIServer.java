import com.sun.net.httpserver.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpHeaders;
import java.nio.charset.Charset;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Base64;

public class TPSIServer {



    public static void main(String[] args) throws Exception {
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new RootHandler());
        System.out.println("Starting server on port: " + port);
        server.start();
    }

    static class RootHandler implements HttpHandler {

        private File base = null;

        public RootHandler(){
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Input base directory: ");
            // Reading data using readLine C:\Users\apasi\Documents\MAGISTERKA\V1\metaheuristics-and-evolutionary-algorithms-master\TSPproblem
            String dir = "";
            try {
                 dir = reader.readLine();
            } catch (IOException ex){
                System.out.println("Error");
            }
            this.base = new File(dir);
        }

        public void handle(HttpExchange exchange) throws IOException {
            try {
                try {
                    String uri = URLDecoder.decode(exchange.getRequestURI().getPath(), "utf-8");
                    File file = new File(base, uri).getCanonicalFile();

                    if (!file.getPath().equals(this.base.getPath()) && !file.getPath().startsWith(this.base.getPath() + File.separator)) {
                        throw new IllegalAccessException();
                    }

                    if (file.isFile()) {
                        serveFile(exchange, file);
                    } else if (file.isDirectory()) {
                        if (!uri.endsWith("/")){
                            setLocation(exchange);
                        } else {
                            showDirectory(exchange, file);
                        }
                    } else {
                        throw new AccessDeniedException(uri);
                    }

                } catch (IllegalAccessException ex) {
                    exchange.sendResponseHeaders(403, -1);
                } catch (Exception ex) {
                    exchange.sendResponseHeaders(404, -1);
                }
            } catch (Throwable ex) {
                exchange.sendResponseHeaders(500, -1);
            }

        }

        private void serveFile(HttpExchange exchange, File file) throws IOException {
            Path path = file.toPath();
            var mime = Files.probeContentType(path);
            if (mime != null) {
                exchange.getResponseHeaders().set("Content-Type", mime);
            }
            exchange.sendResponseHeaders(200, file.length());

            try (OutputStream os = exchange.getResponseBody()){
                Files.copy(path, os);
            }
        }

        private void showDirectory(HttpExchange exchange, File file) throws IOException {
            StringBuilder build = new StringBuilder("<!DOCTYPE html>\n");
            build.append("<html>");
            build.append("<head><meta charset='utf-8' /></head>");
            build.append("<body>");

            if (this.base.getPath() != file.getPath()) {
                build.append("<a href='..'>..</a></br>");
            }

            for (var child: file.listFiles()) {
                var url = URLEncoder.encode(child.getName(), "UTF-8") + (child.isDirectory() ? "/" : "");
                build.append("<a href='" + url + "'>" + child.getName() + "</a></br>");
            }
            build.append("</body>");
            build.append("</html>");

            byte[] bytes = build.toString().getBytes("UTF-8");

            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200,bytes.length);
            try (var os = exchange.getResponseBody()) {
                os.write(bytes);
            }

        }

        private void setLocation(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Location", exchange.getRequestURI().getPath() + "/");
            exchange.sendResponseHeaders(301, -1);
        }
    }
}