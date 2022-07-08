package java_server;

import com.google.gson.Gson;
import java_server.service.FilesToServerService;
import java_server.service.PersonalCodeControllerAndGeneratorService;
import java_server.service.SalaryCalculatorService;
import org.json.JSONObject;
import personal_code_custom.PersonalInfo;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ClientHandler implements Runnable {

    private final Socket client;

    public ClientHandler(Socket socket) {
        this.client = socket;
    }

    @Override
    public void run() {
        try {
            handleRequest(client);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleRequest(Socket client) throws IOException {
        InputStreamReader is = new InputStreamReader(client.getInputStream());
        BufferedReader bf = new BufferedReader(is);
        String firstLine = bf.readLine();
        System.out.println(firstLine);
        String method = firstLine.split(" ")[0];
        String wholePath = firstLine.split(" ")[1];
        String path;
        List<Headers> headers = getHeaders(bf);
        Map<String, String> paramsMap = new HashMap<>();
        if (wholePath.contains("?") && wholePath.contains("=")) {
            path = getParamsAndPath(wholePath, paramsMap);
        } else {
            path = wholePath;
            paramsMap = null;
        }
        Request request = new Request(method, path, null, paramsMap, headers);
        if (request.getMethod().equals("GET") && !(request.getParams() == null)) {
            handleRequestURIWithParameters(request, client);
        } else if (request.getMethod().equals("POST")) {
            getRequestBody(client, bf, request);
        } else {
            if (request.getPath().equals("/personal-code-generator.html")) {
                authenticationControl(client, request);
            } else {
                findFilesByPath(client, request.getPath());
            }
        }
    }

    private static void authenticationControl(Socket client, Request request) throws IOException {
        if (isAuthorized(request)) {
            findFilesByPath(client, request.getPath());
        } else {
            OutputStream out = client.getOutputStream();
            out.write(("HTTP/1.1 401 Unauthorized \r\n").getBytes());
            out.write(("ContentType: text/html\r\n").getBytes());
            out.write(("WWW-Authenticate: Basic realm=/User Visible Realm \r\n").getBytes());
            out.flush();
            client.close();
        }
    }

    private static boolean isAuthorized(Request request) {
        boolean result = false;
        for (Headers header : request.getHeaders()) {
            if (header.getName().equals("Authorization")) {
                String message = header.getValue().split(" ")[1];
                String s = new String(Base64.getDecoder().decode(message));
                if (isValidPassword(s)) {
                    result = true;
                }
            }
        }
        return result;
    }

    private static boolean isValidPassword(String s) {
        String username = "user";
        String password = "password";
        String key = username + ":" + password;
        return s.equals(key);
    }

    private static List<Headers> getHeaders(BufferedReader bf) throws IOException {
        String line = bf.readLine();
        List<Headers> headers = new ArrayList<>();
        while (!line.isEmpty()) {
            headers.add(new Headers(line.split(": ")[0], line.split(": ")[1]));
            line = bf.readLine();
            if (line.isEmpty()) {
                break;
            }
        }
        return headers;
    }

    private static String getParamsAndPath(String wholePath, Map<String, String> paramsMap) {
        String path;
        String params = wholePath.split("\\?")[1];
        path = wholePath.split("\\?")[0];
        String[] paramsArray = params.split("&");
        for (String param : paramsArray) {
            paramsMap.put(param.split("=")[0], param.split("=")[1]);
        }
        return path;
    }

    private static void getRequestBody(Socket client, BufferedReader bf, Request request) throws IOException {
        String requestBodyString = getRequestBodyString(bf, request);
        if (request.getPath().equals("/fileuploadservlet")) {
            FilesToServerService.saveFile(client, requestBodyString);
        } else if (isJson(request)) {
            handleJsonRequest(client, request, requestBodyString);
        } else {
            generateRequestBodyWithParams(client, request, requestBodyString);
        }
    }

    private static String getRequestBodyString(BufferedReader bf, Request request) throws IOException {
        int contentLength = 0;
        for (Headers header : request.getHeaders()) {
            if (header.getName().equals("Content-Length")) {
                contentLength = Integer.parseInt(header.getValue());
                break;
            }
        }
        char[] buf = new char[contentLength];
        bf.read(buf);
        return new String(buf);
    }

    private static void generateRequestBodyWithParams(Socket client, Request request, String requestBodyString) throws IOException {
        String[] split = requestBodyString.split("&");
        Map<String, String> requestBody = new HashMap<>();
        for (String s : split) {
            requestBody.put(s.split("=")[0], s.split("=")[1]);
        }
        request.setRequestBody(requestBody);
        handleRequestBody(request, client);
    }

    private static void handleJsonRequest(Socket client, Request request, String requestBodyString) throws IOException {
        Gson gson = new Gson();
        JSONObject object = new JSONObject(requestBodyString);
        if (request.getPath().equals("/salaryResult")) {
            SalaryCalculatorService.calculateSalaryJson(object, client);
        } else {
            handleJsonObject(object, client);
        }
    }

    private static boolean isJson(Request request) {
        for (Headers header : request.getHeaders()) {
            if (header.getValue().equals("application/json")) {
                return true;
            }
        }
        return false;
    }

    private static void handleJsonObject(JSONObject object, Socket client) throws IOException {
        OutputStream out = new FileOutputStream("test.json");
        out.write(object.toString().getBytes());
        out.close();
//        String response = getHtmlResponse("JSON SENT");
//        String response = object.toString();
//        String response = "{\"firstName\":\"MIKK\",\"lastName\":\"MAKK\",\"birthDay\":\"1990-07-13\"}";
        PersonalInfo pi = new PersonalInfo("Asa", "Ala", "1990-12-12", "12345678909");
        String jsonInString = new Gson().toJson(pi);
        JSONObject mJSONObject = new JSONObject(jsonInString);
        String response = mJSONObject.toString();
        handleResponseToBrowser(client, "200 OK", "application/json", response.getBytes());
    }

    private static void findFilesByPath(Socket client, String path) throws IOException {
        Path filePath = getFilePath(path);
        if (Files.exists(filePath)) {
            String contentType = guessContentType(filePath);
            handleResponseToBrowser(client, "200 OK", contentType, Files.readAllBytes(filePath));
        } else {
            handleResponseToBrowser(client, "404 Not Found", "image/jpeg", Files.readAllBytes(Path.of("html_files/pictures/404.jpeg")));
        }
    }

    private static void badRequestError(Socket client) throws IOException {
        String content = "BAD REQUEST";
        String htmlResponse = getHtmlResponse(content);
        handleResponseToBrowser(client, "400 BAD REQUEST", "text/html", htmlResponse.getBytes());
    }

    private static void handleRequestURIWithParameters(Request request, Socket client) throws IOException {
        if (request.getParams().isEmpty()) {
            // parameetrite validatsioon
            badRequestError(client);
        } else if (request.getParams().containsKey("personalcode")) {
            PersonalCodeControllerAndGeneratorService.controlPersonalCode(request.getParams().get("personalcode"), client);
        } else if (request.getParams().containsKey("dateofbirth") && request.getParams().containsKey("gender")) {
            PersonalCodeControllerAndGeneratorService.generatePersonalCode(request.getParams().get("dateofbirth"), request.getParams().get("gender"), client);
        }
    }

    private static void handleRequestBody(Request request, Socket client) throws IOException {
        if (request.getRequestBody().containsKey("salary")) {
            SalaryCalculatorService.calculateSalaryAndTaxes(request, client);
        } else if (request.getRequestBody().containsKey("birthDay") & request.getRequestBody().containsKey("pk")) {
            PersonalInfo person = new PersonalInfo();
            person.setFirstName(request.getRequestBody().get("firstName"));
            person.setLastName(request.getRequestBody().get("lastName"));
            person.setDateOfBirth(request.getRequestBody().get("birthDay"));
            person.setPersonalCode(request.getRequestBody().get("pk"));
            addPersonalInfoToList(client, person);
        }
    }

    public static String getHtmlResponse(String content) {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<html>").
                append("<body>").
                append("<h1>").
                append(content)
                .append("</h1>")
                .append("</body>")
                .append("</html>");
        return htmlBuilder.toString();
    }

    public static void handleResponseToBrowser(Socket client, String status, String contentType, byte[] content) throws IOException {
        OutputStream out = client.getOutputStream();
        out.write(("HTTP/1.1 \r\n" + status).getBytes());
        out.write(("ContentType: " + contentType + "\r\n").getBytes());
        out.write("\r\n".getBytes());
        out.write(content);
        out.write("\r\n\r\n".getBytes());
        out.flush();
        client.close();
    }

    private static void addPersonalInfoToList(Socket client, PersonalInfo person) throws IOException {
        String p = person.getFirstName() + " " + person.getLastName() + ", " + person.getPersonalCode() + ", " + person.getDateOfBirth() + "\n";
        FileInputStream read = new FileInputStream("inimesed.txt");
        byte[] bytes = read.readAllBytes();
        OutputStream fileSave = new FileOutputStream("inimesed.txt");
        fileSave.write(bytes);
        fileSave.write(p.getBytes());
        fileSave.close();
        String content = "New person " + p + "is added to list";
        String htmlResponse = getHtmlResponse(content);
        handleResponseToBrowser(client, "200 OK", "text/html", htmlResponse.getBytes());
    }

    private static Path getFilePath(String path) {
        if ("/".equals(path)) {
            path = "/main.html";
        }
        return Paths.get("html_files", path);
    }

    private static String guessContentType(Path filePath) throws IOException {
        return Files.probeContentType(filePath);
    }


}
