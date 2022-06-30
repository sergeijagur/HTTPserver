package http_server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import personalCode.*;
import personalCode.html.EstonianPersonalCodeGenerator;
import personalCode.html.NewInfoRequest;
import personalCode.html.PersonalInfo;

import java.io.*;
import java.net.InetSocketAddress;

class Server {

    public static void main(String[] args) throws IOException {
        new Server();
    }
    HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8085), 1);

    public Server() throws IOException {
        server.createContext("/pictures/", new PictureHttpHandler());
        server.createContext("/main", new PersonalCodeHttpHandler());
        server.createContext("/personal-code-generator.html", new PersonalCodeGeneratorHttpHandler());
        server.createContext("/post", new PostHttpHandler());
        server.setExecutor(null);
        server.start();
    }

    private class PostHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            PersonalInfo person = new PersonalInfo();
            if (exchange.getRequestMethod().equals("POST")) {
                person = handleRequest(exchange);
            }
            handleResponse(exchange, person);
        }

        public PersonalInfo handleRequest(HttpExchange exchange) throws IOException {
            InputStream is = exchange.getRequestBody();
            byte[] bytes = is.readAllBytes();
            is.close();
            String a = new String(bytes, "ISO-8859-15");
            String[] split = a.split("&");
            PersonalInfo person = new PersonalInfo();
            for (String s : split) {
                if (s.contains("firstName")) {
                    person.setFirstName(s.split("=")[1]);
                }else if (s.contains("lastName")) {
                    person.setLastName(s.split("=")[1]);
                }else if (s.contains("birthDay")) {
                    person.setDateOfBirth(s.split("=")[1]);
                }else if (s.contains("pk")) {
                    person.setPersonalCode(s.split("=")[1]);
                }
            }
            return person;
        }

        private void handleResponse(HttpExchange exchange, PersonalInfo person) throws IOException {
            String p = person.getFirstName() + " " + person.getLastName() + "," + person.getPersonalCode()+ "\n";
            FileInputStream read = new FileInputStream("inimesed.txt");
            byte[] bytes = read.readAllBytes();
            OutputStream fileSave = new FileOutputStream("inimesed.txt");
            fileSave.write(bytes);
            fileSave.write(p.getBytes());
            fileSave.close();
            OutputStream outputStream = exchange.getResponseBody();
            StringBuilder htmlBuilder = new StringBuilder();
            htmlBuilder.append("<html>").
                    append("<body>").
                    append("<h1>").
                    append("Person is added to list")
                    .append("</h1>")
                    .append("</body>")
                    .append("</html>");
            String htmlResponse = htmlBuilder.toString();
            exchange.sendResponseHeaders(200, htmlResponse.length());
            outputStream.write(htmlResponse.getBytes());
            outputStream.flush();
            outputStream.close();
        }

    }


//        @Override
//        public void handle(HttpExchange httpExchange) throws IOException {
//
//            String requestParamValue=null;
//            if("GET".equals(httpExchange.getRequestMethod())) {
//                requestParamValue = handleGetRequest(httpExchange);
//            }
//            else if("POST".equals(httpExchange.getRequestMethod())) {
////                requestParamValue = handlePostRequest(httpExchange);
//            }
//            handleResponse(httpExchange,requestParamValue);
//        }
//        private String handleGetRequest(HttpExchange httpExchange) {
//            return httpExchange.
//                    getRequestURI()
//                    .toString()
//                    .split("\\?")[1]
//                    .split("=")[1];
//        }
//        private void handleResponse(HttpExchange httpExchange, String requestParamValue)  throws  IOException {
//            OutputStream outputStream = httpExchange.getResponseBody();
//            StringBuilder htmlBuilder = new StringBuilder();
//            htmlBuilder.append("<html>").
//                    append("<body>").
//                    append("<h1>").
//                    append("Hello ")
//                    .append(requestParamValue)
//                    .append("</h1>")
//                    .append("</body>")
//                    .append("</html>");
//
//            String htmlResponse = htmlBuilder.toString();
//            httpExchange.sendResponseHeaders(200, htmlResponse.length());
//            outputStream.write(htmlResponse.getBytes());
//            outputStream.flush();
//            outputStream.close();
//        }
//    }

    private static class PictureHttpHandler implements HttpHandler {
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

    private static class PersonalCodeHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "";
            if ("GET".equals(exchange.getRequestMethod())) {
                response = handleGetRequest(exchange);
            }
            handleResponse(exchange, response);
        }
        private String handleGetRequest(HttpExchange exchange) throws IOException {
                if (exchange.getRequestURI().toString().contains("personalcode")) {
                return exchange.
                        getRequestURI()
                        .toString()
                        .split("\\?")[1]
                        .split("=")[1];
            }
            return exchange.
                    getRequestURI()
                    .toString()
                    .substring(1);
        }
        private void handleResponse(HttpExchange exchange, String response) throws IOException {
            OutputStream outputStream = exchange.getResponseBody();
            if (response.equals("main")) {
                FileInputStream in = new FileInputStream("html_files/"+ response + ".html");
                byte[] bytes = in.readAllBytes();
                in.close();
                exchange.getResponseHeaders().add("Content-type", "text/html");
                exchange.sendResponseHeaders(200, bytes.length);
                outputStream.write(bytes);
                outputStream.close();
            } else {
                controlPersonalCode(exchange, response, outputStream);
            }
        }
        private void controlPersonalCode(HttpExchange exchange, String response, OutputStream outputStream) throws IOException {
            boolean validPersonalCode = new PersonalCodeService(new EstonianPersonalCode(response)).isValidPersonalCode();
            String isValid = "";
            if (validPersonalCode) {
                isValid = " is valid";
            } else {
                isValid = " is not valid";
            }
            StringBuilder htmlBuilder = new StringBuilder();
            htmlBuilder.append("<html>").
                    append("<body>").
                    append("<h1>").
                    append("Personal code " + response + isValid)
                    .append("</h1>")
                    .append("</body>")
                    .append("</html>");
            String htmlResponse = htmlBuilder.toString();
            exchange.sendResponseHeaders(200, htmlResponse.length());
            outputStream.write(htmlResponse.getBytes());
            outputStream.flush();
            outputStream.close();
        }
    }

    private static class PersonalCodeGeneratorHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "";
            if("GET".equals(exchange.getRequestMethod())) {
                response = handleGetRequest(exchange);}
            handleResponse(exchange, response);
        }
        private String handleGetRequest(HttpExchange exchange) throws IOException {
            String gender = "";
            String birthday = "";
            if (exchange.getRequestURI().toString().contains("dateofbirth")
                    & exchange.getRequestURI().toString().contains("gender")) {
                String[] strings = exchange.getRequestURI().toString().split("&");
                for (String string : strings) {
                    if (string.contains("gender")) {
                        gender = string.split("=")[1];
                    } else if (string.contains("dateofbirth")) {
                        birthday = string.split("=")[1];
                    }}
                return gender + "&" + birthday;}
            return exchange.
                    getRequestURI()
                    .toString()
                    .substring(1);
        }
        private void handleResponse(HttpExchange exchange, String response) throws IOException {
            OutputStream outputStream = exchange.getResponseBody();
            if (response.contains("male")) {
                generatePersonalCode(exchange, response, outputStream);
            } else {
                FileInputStream in = new FileInputStream("html_files/" + response + ".html");
                byte[] bytes = in.readAllBytes();
                in.close();
                exchange.getResponseHeaders().add("Content-type", "text/html");
                exchange.sendResponseHeaders(200, bytes.length);
                outputStream.write(bytes);
                outputStream.close();
            }}

        private void generatePersonalCode(HttpExchange exchange, String response, OutputStream outputStream) throws IOException {
            String gender = "";
            String birthdate = "";
            String[] strings = response.split("&");
            for (String string : strings) {
                if (string.contains("male")) {
                    gender = string;
                } else {
                    birthdate = string;
                }}
            String personalCode = new PersonalCodeService(new EstonianPersonalCodeGenerator(new NewInfoRequest(birthdate, gender))).generatePersonalCode();
            StringBuilder htmlBuilder = new StringBuilder();
            htmlBuilder.append("<html>").
                    append("<body>").
                    append("<h1>").
                    append("Generated personal code is " + personalCode)
                    .append("</h1>")
                    .append("</body>")
                    .append("</html>");
            String htmlResponse = htmlBuilder.toString();
            exchange.sendResponseHeaders(200, htmlResponse.length());
            outputStream.write(htmlResponse.getBytes());
            outputStream.flush();
            outputStream.close();
        }
    }
    }






