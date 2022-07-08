package java_server.service;

import java_server.ClientHandler;
import java_server.Headers;
import java_server.Request;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Base64;


public class BasicAuthentication {

    public static void authenticationControl(Socket client, Request request) throws IOException {
        if (isAuthorized(request)) {
            ClientHandler.findFilesByPath(client, request.getPath());
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
}
