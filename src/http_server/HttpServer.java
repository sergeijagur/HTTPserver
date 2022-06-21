package http_server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

class Server {

    public static void main(String[] args) throws IOException {
        new Server();

    }


    HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8085), 1);
//    ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

    public Server() throws IOException {
        server.createContext("/test", new MyHttpHandler());
        server.createContext("/photo.jpeg", new MyHttpHandler2());
        server.createContext("/personal-code", new MyHttpHandler2());
        server.setExecutor(null);
        server.start();
//        logger.info(" Server started on port 8001");
    }

    private class MyHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            String requestParamValue=null;
            if("GET".equals(httpExchange.getRequestMethod())) {
                requestParamValue = handleGetRequest(httpExchange);
            }
//            else if("POST".equals(httpExchange)) {
//                requestParamValue = handlePostRequest(httpExchange);
//            }
            handleResponse(httpExchange,requestParamValue);
        }
        private String handleGetRequest(HttpExchange httpExchange) {
            return httpExchange.
                    getRequestURI()
                    .toString()
                    .split("\\?")[1]
                    .split("=")[1];
        }
        private void handleResponse(HttpExchange httpExchange, String requestParamValue)  throws  IOException {
            OutputStream outputStream = httpExchange.getResponseBody();
            StringBuilder htmlBuilder = new StringBuilder();
            htmlBuilder.append("<html>").
                    append("<body>").
                    append("<h1>").
                    append("Hello ")

                    .append(requestParamValue)
                    .append("</h1>")
                    .append("</body>")
                    .append("</html>");

            String htmlResponse = htmlBuilder.toString();
            httpExchange.sendResponseHeaders(200, htmlResponse.length());
            outputStream.write(htmlResponse.getBytes());
            outputStream.flush();
            outputStream.close();
        }
    }

    private class MyHttpHandler2 implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {

            String response = "";
            if("GET".equals(exchange.getRequestMethod())) {
                response = handleGetRequest(exchange);
            }
            handleResponse(exchange, response);
        }

        private String handleGetRequest(HttpExchange exchange) throws IOException {
            return exchange.
                    getRequestURI()
                    .toString()
                    .substring(1);
        }


        private void handleResponse(HttpExchange exchange, String response) throws IOException {
            OutputStream outputStream = exchange.getResponseBody();

            if (response.equals("personal-code")) {
                FileInputStream in = new FileInputStream("index.html");
                byte[] bytes = in.readAllBytes();
                in.close();

                exchange.getResponseHeaders().add("Comtent-type", "text/html");
                exchange.sendResponseHeaders(200, bytes.length);
                outputStream.write(bytes);
                outputStream.close();
            }

            InputStream picIn = new FileInputStream(response);
            byte[] bytes = picIn.readAllBytes();
            picIn.close();

            exchange.getResponseHeaders().add("Content-type", "image/jpeg");
            exchange.sendResponseHeaders(200, bytes.length);
            outputStream.write(bytes);
            outputStream.flush();
            outputStream.close();

        }

    }

}




