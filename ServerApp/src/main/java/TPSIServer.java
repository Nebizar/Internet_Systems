import com.cedarsoftware.util.io.JsonWriter;
import com.sun.net.httpserver.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.http.HttpHeaders;
import java.nio.charset.Charset;
import java.nio.file.Files;
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
        server.createContext("/echo", new EchoHandler());
        server.createContext("/redirect/", new RedirectHandler());
        server.createContext("/cookies", new CookiesHandler());
        server.createContext("/auth", new AuthHandler());
        HttpContext authCtx = server.createContext("/auth2", new Auth2Handler());
        authCtx.setAuthenticator(new BasicAuthenticator("Podaj login i haslo") {
            @Override
            public boolean checkCredentials(String s, String s1) {
                return "admin".equals(s) && "nimda".equals(s1);
            }
        });
        System.out.println("Starting server on port: " + port);
        server.start();
    }

    static class RootHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            //String response = "Hello World!";
            //Paths.get("src/index.html");

            byte[] file = Files.readAllBytes(Paths.get("src/main/java/index.html"));
            exchange.getResponseHeaders().set("Content-Type", "html");
            //File file = new File("src/index.html");
            exchange.sendResponseHeaders(200, file.length);
            try (OutputStream os = exchange.getResponseBody()) {
                //Files.copy(file.toPath(), os);
                os.write(file);
                //os.close();
            }
            //OutputStream os = exchange.getResponseBody();

        }
    }

    static class EchoHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            Map args = new HashMap();
            args.put(JsonWriter.PRETTY_PRINT, true);
            String jsonResponse = JsonWriter.objectToJson(exchange.getRequestHeaders(), args);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, jsonResponse.length());
            OutputStream os = exchange.getResponseBody();
            os.write(jsonResponse.getBytes());
            os.close();



        }
    }

    static class RedirectHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String uri = exchange.getRequestURI().toString();
            System.out.println(uri);
            int codes[] = {301, 302, 303, 307, 308};
            int code;
            try {
                code = Integer.parseInt(uri.split("/")[2]);
                boolean check = false;
                for( int codeIter: codes){
                    if(codeIter == code) {
                        check = true;
                    }
                }
                if(!check) {
                    throw new Exception();
                }
            } catch (Exception ex) {
                code = 404;
            }

            exchange.getResponseHeaders().set("Location", "https://www.google.com/");
            exchange.sendResponseHeaders(code, -1);
        }
    }

    static class CookiesHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            Headers headers = exchange.getResponseHeaders();
            headers.add("Set-Cookie", "Raw=cookie");
            var dom = exchange.getRequestHeaders().getFirst("Host");
            if (dom != null) {
                dom = dom.replaceAll(":\\d+","");
            } else {
                dom = "localhost";
            }
            headers.add("Set-Cookie", "ValidDomain=cookie; domain=" + dom);
            headers.add("Set-Cookie", "InvalidDomain=cookie; domain=google.pl");
            headers.add("Set-Cookie", "EchoOnly=cookie; path=/echo");
            exchange.sendResponseHeaders(200, -1);
        }
    }

    static class AuthHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            //if (auth == false) {
            //exchange.getResponseHeaders().set("WWW-Authenticate", "Basic");
            //exchange.sendResponseHeaders(401, -1);
            try {
                String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
                String[] split = authHeader.split("\\s");
                String credentials = split[1];
                byte[] decoded = Base64.getDecoder().decode(credentials);
                String decoded2 = new String(decoded);
                String[] userPass = decoded2.split(":");

                String username = userPass[0];
                String password = userPass[1];
                if (username.equals("admin") && password.equals("nimda")){
                    System.out.println("HELLO");
                    //auth = true;
                    exchange.sendResponseHeaders(200, -1);
                } else {
                    //   auth = false;
                    exchange.getResponseHeaders().set("WWW-Authenticate", "Basic");
                    exchange.sendResponseHeaders(401, -1);
                }
            } catch (Exception ex) {
                exchange.getResponseHeaders().set("WWW-Authenticate", "Basic");
                exchange.sendResponseHeaders(401, -1);
            }

        }
    }

    static class Auth2Handler implements HttpHandler { 
        public void handle(HttpExchange exchange) throws IOException {
            exchange.sendResponseHeaders(200, -1);
        }
    }
}