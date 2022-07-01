package java_server.service;

import api.personal_code.EstonianPersonalCode;
import api.personal_code.PersonalCodeService;
import personal_code_custom.EstonianPersonalCodeGenerator;
import personal_code_custom.NewInfoRequest;

import java.io.IOException;
import java.net.Socket;

import static java_server.JavaServer.getHtmlResponse;
import static java_server.JavaServer.handleResponseToBrowser;

public class PersonalCodeControllerAndGeneratorService {

    public static void generatePersonalCode(String birthdate, String gender, Socket client) throws IOException {
        String personalCode = new PersonalCodeService(new EstonianPersonalCodeGenerator(new NewInfoRequest(birthdate, gender))).generatePersonalCode();
        handleResponseWithGeneratedPersonalCode(client, personalCode);
    }

    private static void handleResponseWithGeneratedPersonalCode(Socket client, String personalCode) throws IOException {
        String content = "Generated personal code is " + personalCode;
        String htmlResponse = getHtmlResponse(content);
        handleResponseToBrowser(client, "200 OK", "text/html", htmlResponse.getBytes());
    }

    public static void controlPersonalCode(String response, Socket client) throws IOException {
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


}
