package java_server;

import api.personal_code.EstonianPersonalCode;
import api.personal_code.PersonalCodeService;
import api.salary_calculator.GrossSalary;
import api.salary_calculator.NetSalary;
import api.salary_calculator.SalaryInformationResponse;
import api.salary_calculator.TotalExpense;
import pk_html.EstonianPersonalCodeGenerator;
import pk_html.NewInfoRequest;
import pk_html.PersonalInfo;

import java.io.*;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaServer {

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
        System.out.println(firstLine);
        String method = firstLine.split(" ")[0];
        String wholePath = firstLine.split(" ")[1];
        String path = "";
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
            handleRequestBody(request, client);
        } else {
            findFilesByPath(client, request.getPath());
        }
    }

    private static List<Headers> getHeaders(BufferedReader bf) throws IOException {
        String line = bf.readLine();
        List<Headers> headers = new ArrayList<>();
        while (!line.isEmpty()) {
            headers.add(new Headers(line.split(" ")[0], line.split(" ")[1]));
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
        int contentLength = 0;
        for (Headers header : request.getHeaders()) {
            if (header.getName().equals("Content-Length:")) {
                contentLength = Integer.parseInt(header.getValue());
                break;
            }
        }
        char[] buf = new char[contentLength];
        bf.read(buf);
        String requestBodyString = new String(buf);
        if (request.getPath().equals("/fileuploadservlet")) {
            saveFile(client, requestBodyString);
        }
        String[] split = requestBodyString.split("&");
        Map<String, String> requestBody = new HashMap<>();
        for (String s : split) {
            requestBody.put(s.split("=")[0], s.split("=")[1]);
        }
        request.setRequestBody(requestBody);
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
            controlPersonalCode(request.getParams().get("personalcode"), client);
        } else if (request.getParams().containsKey("dateofbirth") && request.getParams().containsKey("gender")) {
            generatePersonalCode(request.getParams().get("dateofbirth"), request.getParams().get("gender"), client);
        }
    }

    private static void handleRequestBody(Request request, Socket client) throws IOException {
        if (request.getRequestBody().containsKey("salary")) {
            calculateSalaryAndTaxes(request, client);
        } else if (request.getRequestBody().containsKey("birthDay") & request.getRequestBody().containsKey("pk")) {
            PersonalInfo person = new PersonalInfo();
            person.setFirstName(request.getRequestBody().get("firstName"));
            person.setLastName(request.getRequestBody().get("lastName"));
            person.setDateOfBirth(request.getRequestBody().get("birthDay"));
            person.setPersonalCode(request.getRequestBody().get("pk"));
            addPersonalInfoToList(client, person);
        }
    }

    private static String getHtmlResponse(String content) {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<html>").
                append("<body>").
                append("<h1>").
                append(content)
                .append("</h1>")
                .append("</body>")
                .append("</html>");
        String htmlResponse = htmlBuilder.toString();
        return htmlResponse;
    }

    private static void handleResponseToBrowser(Socket client, String status, String contentType, byte[] content) throws IOException {
        OutputStream out = client.getOutputStream();
        out.write(("HTTP/1.1 \r\n" + status).getBytes());
        out.write(("ContentType: " + contentType + "\r\n").getBytes());
        out.write("\r\n".getBytes());
        out.write(content);
        out.write("\r\n\r\n".getBytes());
        out.flush();
        client.close();
    }


    private static void saveFile(Socket client, String requestBody) throws IOException {
        String fileName = requestBody.split("\r\n")[1].split(" ")[3].split("=")[1];
        String fn = fileName.substring(1, fileName.length() - 1);
        System.out.println(fn);
        String data = requestBody.split("\r\n")[4];
        byte[] bytes = data.getBytes(StandardCharsets.ISO_8859_1);
        FileOutputStream file = new FileOutputStream(fn);
        file.write(bytes);
        file.close();
        handleResponseToBrowser(client, "200 OK", "text/html", "File is saved".getBytes());
    }

    private static void calculateSalaryAndTaxes(Request request, Socket client) throws IOException {
        SalaryInformationResponse response = new SalaryInformationResponse();
        String salaryType = request.getRequestBody().get("salary-type");
        String salary = request.getRequestBody().get("salary");
        switch (salaryType) {
            case "gross": {
                response = new GrossSalary(new BigDecimal(salary)).getSalaryInformation();
                break;
            }
            case "net": {
                response = new NetSalary(new BigDecimal(salary)).getSalaryInformation();
                break;
            }
            case "total-expense": {
                response = new TotalExpense(new BigDecimal(salary)).getSalaryInformation();
                break;
            }
        }
        handleSalaryCalculatorResponse(response, client);
    }

    private static void handleSalaryCalculatorResponse(SalaryInformationResponse response, Socket client) throws IOException {
        String content = "<br>Total Cost for Employer (Wage Fund): " + response.getEmployerExpense() + "<br>" +
                "Social Tax: " + response.getSocialTax() + "<br>" +
                "Unemployment insurance (employer): " + response.getUnemploymentInsuranceByEmployer() + "<br>" +
                "Gross Salary/Wage: " + response.getGrossSalary() + "<br>" +
                "Funded pension (II pillar): " + response.getPension() + "<br>" +
                "Unemployment insurance (employee): " + response.getUnemploymentInsuranceByEmployee() + "<br>" +
                "Income Tax: " + response.getIncomeTax() + "<br>" +
                "Net Salary/Wage: " + response.getNetSalary() + "<br>";
        String htmlResponse = getHtmlResponse(content);
        handleResponseToBrowser(client, "200 OK", "text/html", htmlResponse.getBytes());
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

    private static void generatePersonalCode(String birthdate, String gender, Socket client) throws IOException {
        String personalCode = new PersonalCodeService(new EstonianPersonalCodeGenerator(new NewInfoRequest(birthdate, gender))).generatePersonalCode();
        handleResponseWithGeneratedPersonalCode(client, personalCode);
    }

    private static void handleResponseWithGeneratedPersonalCode(Socket client, String personalCode) throws IOException {
        String content = "Generated personal code is " + personalCode;
        String htmlResponse = getHtmlResponse(content);
        handleResponseToBrowser(client, "200 OK", "text/html", htmlResponse.getBytes());
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
        String content = "Personal code " + response + isValid;
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
