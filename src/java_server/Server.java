package java_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Server {


        public static void main( String[] args ) throws Exception {
            try (ServerSocket serverSocket = new ServerSocket(8082)) {
                while (true) {
                    try (Socket client = serverSocket.accept()) {
                        handleClient(client);
                    }
                }
            }
        }

        private static void handleClient(Socket client) throws IOException {
            BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));

            String firstLine = br.readLine();
            String[] s = firstLine.split(" ");
            String method = s[0];
            String path = s[1];
            String version = s[2];

            System.out.println(firstLine);
            String line = br.readLine();
            while (!line.isEmpty()) {
                System.out.println(line);
                line= br.readLine();
            }

            Path filePath = getFilePath(path);
            if (Files.exists(filePath)) {
                // file exist
                String contentType = guessContentType(filePath);
                sendResponse(client, "200 OK", contentType, Files.readAllBytes(filePath));
            } else {
                // 404
                byte[] notFoundContent = "<h1>Not found :(</h1>".getBytes();
                sendResponse(client, "404 Not Found", "text/html", notFoundContent);
            }

        }

        private static void sendResponse(Socket client, String status, String contentType, byte[] content) throws IOException {
            OutputStream clientOutput = client.getOutputStream();
            clientOutput.write(("HTTP/1.1 \r\n" + status).getBytes());
            clientOutput.write(("ContentType: " + contentType + "\r\n").getBytes());
            clientOutput.write("\r\n".getBytes());
            clientOutput.write(content);
            clientOutput.write("\r\n\r\n".getBytes());
            clientOutput.flush();
            client.close();
        }

        private static Path getFilePath(String path) {
            if ("/".equals(path)) {
                path = "/main.html";
            }
            if ("/favicon.ico".equals(path)) {
                path = "/main.html";
            }

            return Paths.get("html_files", path);
        }

        private static String guessContentType(Path filePath) throws IOException {
            return Files.probeContentType(filePath);
        }


}
