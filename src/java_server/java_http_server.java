package java_server;

import personalCode.EstonianPersonalCode;
import personalCode.PersonalCodeService;
import personalCode.html.EstonianPersonalCodeGenerator;
import personalCode.html.NewInfoRequest;
import personalCode.html.PersonalInfo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class java_http_server {

    public static void main(String[] args) throws IOException {
        int port = 8080;
        ServerSocket server = new ServerSocket(port);
        System.out.println("Listening for connection on port " + port);
        while (true) {
            try (Socket client = server.accept()) {
                handleRequest(client);
            }
        }
    }

    private static void handleRequest(Socket client) throws IOException {
        InputStreamReader is = new InputStreamReader(client.getInputStream());
        BufferedReader bf = new BufferedReader(is);
        String firstLine = bf.readLine();
        String[] s = firstLine.split(" ");
        String method = s[0];
        String path = s[1];
        System.out.println(firstLine);
        String line = bf.readLine();
        if (method.equals("GET") & (path.contains("=") || path.contains("&"))) {
            handleGetWithQueryParameters(client, bf, path, line);
        } else if (method.equals("POST")) {
            handlePostMethod(client, bf, line);
        } else {
            printRequest(line, bf);
            controlFilesByPath(client, path);
        }
    }

    private static void handleGetWithQueryParameters(Socket client, BufferedReader bf, String path, String line) throws IOException {
        printRequest(line, bf);
        handleRequestURIWithParameters(path, client);
    }

    private static void handlePostMethod(Socket client, BufferedReader bf, String line) throws IOException {
        int contentLength = 0;
        while (!line.isEmpty()) {
            System.out.println(line);
            line = bf.readLine();
            if (line.startsWith("Content-Length:")) {
                String cl = line.substring("Content-Length:".length()).trim();
                contentLength = Integer.parseInt(cl);
            } else if (line.isEmpty()) {
                break;
            }
        }
        char[] buf = new char[contentLength];
        bf.read(buf);
        String requestBody = new String(buf);
        handleRequestBody(requestBody, client);
    }

    private static void controlFilesByPath(Socket client, String path) throws IOException {
        Path filePath = getFilePath(path);
        if (Files.exists(filePath)) {
            String contentType = guessContentType(filePath);
            handleResponseWithFile(client, "200 OK", contentType, Files.readAllBytes(filePath));
        } else {
            byte[] notFoundContent = "<h1>URL not found</h1>".getBytes();
            handleResponseWithFile(client, "404 Not Found", "text/html", notFoundContent);
        }
    }

    private static void printRequest(String line, BufferedReader bf) throws IOException {
        while (!line.isEmpty()) {
            System.out.println(line);
            line = bf.readLine();
            if (line.isEmpty()) {
                break;
            }
        }
    }

    private static void handleRequestBody(String requestBody, Socket client) throws IOException {
        String[] split = requestBody.split("&");
        PersonalInfo person = new PersonalInfo();
        for (String s : split) {
            if (s.contains("firstName")) {
                person.setFirstName(s.split("=")[1]);
            } else if (s.contains("lastName")) {
                person.setLastName(s.split("=")[1]);
            } else if (s.contains("birthDay")) {
                person.setDateOfBirth(s.split("=")[1]);
            } else if (s.contains("pk")) {
                person.setPersonalCode(s.split("=")[1]);
            }
        }
        handlePostMethodResponse(client, person);
    }

    private static void handlePostMethodResponse(Socket client, PersonalInfo person) throws IOException {
        String p = person.getFirstName() + " " + person.getLastName() + "," + person.getPersonalCode() + "," + person.getDateOfBirth() + "\n";
        FileInputStream read = new FileInputStream("inimesed.txt");
        byte[] bytes = read.readAllBytes();
        OutputStream fileSave = new FileOutputStream("inimesed.txt");
        fileSave.write(bytes);
        fileSave.write(p.getBytes());
        fileSave.close();
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<html>").
                append("<body>").
                append("<h1>").
                append("New person " + p + "is added to list")
                .append("</h1>")
                .append("</body>")
                .append("</html>");
        String htmlResponse = htmlBuilder.toString();
        OutputStream out = client.getOutputStream();
        out.write(("HTTP/1.1 200 OK \r\n").getBytes());
//        out.write(("ContentType: " + contentType + "\r\n").getBytes());
        out.write("\r\n".getBytes());
        out.write(htmlResponse.getBytes());
        out.write("\r\n\r\n".getBytes());
        out.flush();
        client.close();
    }

    private static void handleResponseWithFile(Socket client, String status, String contentType, byte[] content) throws IOException {
        OutputStream out = client.getOutputStream();
        out.write(("HTTP/1.1 \r\n" + status).getBytes());
        out.write(("ContentType: " + contentType + "\r\n").getBytes());
        out.write("\r\n".getBytes());
        out.write(content);
        out.write("\r\n\r\n".getBytes());
        out.flush();
        client.close();
    }

    private static void handleRequestURIWithParameters(String path, Socket client) throws IOException {
        String response = "";
        if (path.contains("personalcode")) {
            response = path.split("\\?")[1].split("=")[1];
            controlPersonalCode(response, client);
        } else if (path.contains("dateofbirth") & path.contains("gender")) {
            String[] strings = path.split("&");
            String gender = "";
            String birthdate = "";
            for (String string : strings) {
                if (string.contains("gender")) {
                    gender = string.split("=")[1];
                } else if (string.contains("dateofbirth")) {
                    birthdate = string.split("=")[1];
                }
                response = gender + "&" + birthdate;
            }
            generatePersonalCode(response, client);
        }
    }

    private static void generatePersonalCode(String response, Socket client) throws IOException {
        String gender = "";
        String birthdate = "";
        String[] strings = response.split("&");
        for (String string : strings) {
            if (string.contains("male")) {
                gender = string;
            } else {
                birthdate = string;
            }
        }
        String personalCode = new PersonalCodeService(new EstonianPersonalCodeGenerator(new NewInfoRequest(birthdate, gender))).generatePersonalCode();
        handleResponseWithGeneratedPersonalCode(client, personalCode);
    }

    private static void handleResponseWithGeneratedPersonalCode(Socket client, String personalCode) throws IOException {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<html>").
                append("<body>").
                append("<h1>").
                append("Generated personal code is " + personalCode)
                .append("</h1>")
                .append("</body>")
                .append("</html>");
        String htmlResponse = htmlBuilder.toString();
        OutputStream out = client.getOutputStream();
        out.write(("HTTP/1.1 200 OK \r\n").getBytes());
//        out.write(("ContentType: " + contentType + "\r\n").getBytes());
        out.write("\r\n".getBytes());
        out.write(htmlResponse.getBytes());
        out.write("\r\n\r\n".getBytes());
        out.flush();
        client.close();
    }

    private static void controlPersonalCode(String response, Socket client) throws IOException {
        boolean validPersonalCode = new PersonalCodeService(new EstonianPersonalCode(response)).isValidPersonalCode();
        String isValid = "";
        if (validPersonalCode) {
            isValid = " is valid";
        } else {
            isValid = " is not valid";
        }
        handleResponseForPersonalCodeController(response, client, isValid);
    }

    private static void handleResponseForPersonalCodeController(String response, Socket client, String isValid) throws IOException {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<html>").
                append("<body>").
                append("<h1>").
                append("Personal code " + response + isValid)
                .append("</h1>")
                .append("</body>")
                .append("</html>");
        String htmlResponse = htmlBuilder.toString();
        OutputStream out = client.getOutputStream();
        out.write(("HTTP/1.1 200 OK \r\n").getBytes());
//        out.write(("ContentType: " + contentType + "\r\n").getBytes());
        out.write("\r\n".getBytes());
        out.write(htmlResponse.getBytes());
        out.write("\r\n\r\n".getBytes());
        out.flush();
        client.close();
    }

    private static Path getFilePath(String path) {
        if ("/".equals(path)) {
            path = "/main.html";
        }
//        if ("/favicon.ico".equals(path)) {
//            path = "/main.html";
//        }
        return Paths.get("html_files", path);
    }

    private static String guessContentType(Path filePath) throws IOException {
        return Files.probeContentType(filePath);
    }

}
